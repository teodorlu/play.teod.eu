#!/usr/bin/env bb

;; Action - edit tags
;;
;;   ./play relations :from :files :to :lines > lines.txt
;;   # edit lines.txt - do stuff in batch ...
;;   ./play relations :from :lines :to :files < lines.txt
;;
;; Action - create new page
;;
;;   ./play page        rdf-intro :title "Introduction to linked data with RDF and Datalog"
;;   ./play create-page rdf-intro :title "Introduction to linked data with RDF and Datalog"
;;
;; Wanted commands:
;;
;;   ./play index > index.html # generate index.html
;;   ./play preview
;;
;; Wanted behavior:
;;
;;   When I create a new page, I write :author-url and :created-at

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/cli {:mvn/version "0.3.31"}
                        eu.teod/pandoc-toolbox {:local/root "../pandoc-toolbox"}}})
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.java.shell]
         '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[clojure.pprint :refer [pprint]]
         '[cheshire.core :as json]
         '[teod.pandoc-toolbox :as pandoc])

;; relations example
;;
;; page id -> page metadata
{"emacs" {:slug "emacs"
          :title "(Doom) Emacs learning journal"
          :form :rambling
          :readiness :in-progress}
 "feedback-design-impl" {:title "Feedback loops, API design and how stuff works"}}

(defn bash [cmd]
  (str/trim (:out (clojure.java.shell/sh "bash" "-c" cmd))))

(defn pages []
  (->> (bash "ls **/play.edn | sed 's|/play.edn||g'")
       (str/split-lines)
       (map (fn [slug] {:slug slug}))))

(defn files->relations
  "Read relations from play.edn files on disk

  :uuid-from-org - when true, try to crudely find UUIDs from index.org files.
  "
  [{:keys [uuid-from-org]}]
  (let [enrich (fn [page]
                 (if (and uuid-from-org (not (:builder page))) ;; only normal org thing builders please
                   (let [uuid-found-on-org (-> (bash (str "cat " (:slug page) "/index.org | grep ID"))
                                               (str/replace #":ID:\s+" "")
                                               (str/split #"\s+")
                                               first)]
                     (if (str/blank? uuid-found-on-org)
                       page
                       (assoc page :uuid uuid-found-on-org)))
                   page))]
    (->> (pages)
         (map (fn [{:keys [slug] :as p}]
                (merge p (edn/read-string (slurp (str slug "/play.edn"))))))
         (map enrich)
         (map (juxt :slug identity))
         (into {}))))

(defn relations->lines
  "Produce one line per page

  Example:

    :slug emacs, :title \"(Doom) Emacs learning journal\", :form :rambling, :readiness :in-progress"
  [rels]
  (doseq [l (vals rels)]
    (prn l)))

(defn relations->lines2
  "Produce one line per relation

  Output format:

    [:slug \"emacs\"] :title \"(Doom) Emacs learning journal\"
    [:slug \"emacs\"] :form :rambling
    [:slug \"emacs\"] :readiness :in-progress

  Example: how many WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep -i \":readiness :wtf\" | wc -l
    30

  Example: how many non-WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep :readiness | grep -v \":readiness :wtf\" | wc -l
    9
  "
  [rels]
  (doseq [[slug page-meta] rels]
    (doseq [[attribute value] (dissoc page-meta :slug)]
      (prn [:slug slug] attribute value))))

(defn relations->lines+recent
  "Produce one line per relation, recent first

  Example: list recently created pages, with page ID and created date:

    ./play.clj relations :from files :to lines+recent | ./play.clj relations :from lines :to lines2 | grep :created
  "
  [rels]
  (let [recent-lines (->> (vals rels)
                          (filter :created)
                          (sort-by :created)
                          reverse)]
    (doseq [l recent-lines]
      (prn l))))

(defn relations->pretty
  [rels]
  (pprint rels))

(defn lines->relations
  "Relations from lines on stdin"
  [{}]
  (->> (str/split-lines (slurp *in*))
       (map edn/read-string)
       (map (fn [{:keys [slug] :as page}] {slug page}))
       (into {})))

(defn relations->files [rels]
  (doseq [[id page] rels]
    (spit (str id "/play.edn")
          (with-out-str
            (pprint (dissoc page :slug))))))

(defn page-index-org [{:keys [title trailing-blank-lines uuid]}]
  (str/join "\n"
            (concat
             ;; Link for org-roam
             (when uuid
               [":PROPERTIES:"
                (str ":ID: " uuid)
                ":END:"])

             ;; Header, title, link up
             [(str "#+TITLE: " title)
              ""
              "[[file:..][..]]

DRAFT
"
              ]

             ;; Trailing whitespace.
             (when trailing-blank-lines
               (concat ["#+begin_verse"]
                       (repeat trailing-blank-lines "")
                       ["#+end_verse"
                        ""])))))

(defn relations [{:keys [opts]}]
  (let [sources {:files files->relations
                 :lines lines->relations}
        targets {:lines relations->lines
                 :lines2 relations->lines2
                 :lines+recent relations->lines+recent
                 :pretty relations->pretty
                 :files relations->files}
        {:keys [from to]} opts]
    (assert (sources from))
    (assert (targets to))
    (let [rels ((sources from) opts)]
      ((targets to) rels))))

(defn random-page [{:keys [opts]}]
  (let [n (or (:n opts) 1)]
    (println
     (str/join "\n"
               (->> (files->relations {})
                    vals
                    shuffle
                    (take n)
                    (map :slug))))))

(defn create-page [{:keys [opts]}]
  (let [slug (:slug opts)
        title (or (:title opts) slug)
        uuid (or (:uuid opts) (bash "uuidgen"))]
    (assert slug "Page slug is required.")
    (let [org-file (str slug "/index.org")
          play-file (str slug "/play.edn")]
      (fs/create-dirs slug)

      ;; Org file
      (when-not (fs/exists? org-file)
        (spit org-file (page-index-org {:title title
                                        :uuid uuid})))

      ;; Play file
      (when-not (fs/exists? play-file)
        (spit play-file (with-out-str
                          (pprint {:title title
                                   :readiness :wtf-is-this
                                   :uuid uuid
                                   :author-url "https://teod.eu"
                                   :created (str/trim (bash "date -I"))}))))

      ;; Regenerate the makefile since we've added a new target
      (bash "./play.clj makefile")

      nil)))

(defn thing [{:keys [x y]}]
  (list x y))

(defn builder [rel]
  (:builder rel :pandoc-page))

(defmulti makefile-entry builder)

(defmethod makefile-entry :pandoc-page
  [rel]
  (let [t rel
        org (fn [target] (str target "/index.org"))
        html (fn [target] (str target "/index.html"))]
    (str (html t) ": " (org t)
         "\n\t"
         "pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i " (org t)
         " -o " (html t))))

(defn makefile [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        targets (->> (files->relations {})
                     vals
                     ;; For now, /only/ make builders for normal pandoc pages (same as default)
                     (filter (fn [rel]
                               (= :pandoc-page (builder rel))))
                     (map :slug)
                     sort)
        html (fn [target] (str target "/index.html"))
        play-edn (fn [target] (str target "/play.edn"))
        makefile-str (with-out-str
                       (println "# DO NOT EDIT DIRECTLY -- THIS MAKEFILE IS GENERATED")
                       (println "# SEE `make clean` TARGET")
                       (println "")
                       (println "")

                       (println "# Generate target for root index")
                       ;; TODO root index also depends on all the play.edn files found
                       (println (str/join " " (concat ["index.html:" "index.clj"] (map html targets) (map play-edn targets))))
                       (println "\t./index.clj")
                       (println "")
                       (println "")

                       (println  "# Generate target for each page")
                       (println
                        (str/join "\n\n"
                                  (for [t targets]
                                    (makefile-entry t))))
                       (println "")
                       (println "")


                       (println ".PHONY: makefile")
                       (println "makefile:")
                       (println "\t./play.clj makefile")
                       (println "")
                       (println "")

                       (println "# Rengenerate the index")
                       (println ".PHONY: clean")
                       (println "clean:")
                       (println "\trm -f index.html")
                       (println "")
                       (println "")

                       (println "# Regenerate everything")
                       (println ".PHONY: ultraclean")
                       (println "ultraclean: clean")
                       (println (str "\t"
                                     "rm -f "
                                     (str/join " " (concat ["index.html"] (map html targets)))))
                       )]
    (if dry-run
      (print makefile-str)
      (spit "Makefile" makefile-str))))

(defn index-by-uuid
  "Create an index from page uuid to slug and title."
  [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        uuid-index (->> (files->relations {})
                        vals
                        (filter :uuid)
                        (map #(select-keys % [:uuid :slug :title :id])))]
    (if dry-run
      ;; just print pages
      (doseq [page uuid-index]
        (prn page))
      ;; otherwise, write to index/by-uuid/{UUID}.edn
      (doseq [page uuid-index]
        (spit (str "index/by-uuid/" (:uuid page) ".edn")
              (with-out-str (pprint page)))))))

(defn filter-pandoc [{:as opts}]
  ;; only supported filter for now is resolve-links
  ;;
  ;; Test with:
  ;;
  ;;   ./play.clj filter resolve-links < ../pandoc-toolbox/pandoc-examples/link.json

  (when (contains? (set (:rest-cmds opts))
                   "resolve-links")
    (let [pandoc-json (json/parse-string (slurp *in*))
          resolve-link (fn [x]
                         (if (pandoc/link? x)
                           (let [link x]
                             (prn "link!!!!!!")
                             (prn x)
                             (assoc-in link pandoc/link-target-path "https://teod.eu"))
                           x))
          resolved (pandoc/filter-body-postwalk pandoc-json resolve-link)]
      (println)
      (println "Pandoc JSON:")
      (println (json/generate-string resolved))
      )))

(defn print-help [{}]
  (println (str/trim "
Usage: ./play.clj <subcommand> <options>

Subcommands:

page SLUG :title PAGE_TITLE

relations :from RELATIONS_SOURCE :to RELATIONS_TARGET

makefile [--dry-run]

index-by-uuid [--dry-run]
")))

(def dispatch-table
  [{:cmds ["create-page"] :fn create-page :cmds-opts [:slug]}
   {:cmds ["filter"] :fn filter-pandoc :cmds-opts [:resolve-links]}
   {:cmds ["index-by-uuid"] :fn index-by-uuid}
   {:cmds ["makefile"] :fn makefile}
   {:cmds ["random-page"] :fn random-page}
   {:cmds ["relations"] :fn relations}])

(defn print-subcommands [{}]
  (println "usage: ./play.clj <command>")
  (println "")
  (println "available commands:")
  (doseq [{:keys [cmds]} dispatch-table]
    (println (str "  "
                  (str/join " " cmds)))))

(defn main [& args]
  (cli/dispatch (concat dispatch-table
                        [{:cmds ["help"] :fn print-subcommands}
                         {:cmds [] :fn print-subcommands}])
                args
                {:coerce {;; relations
                          :from :keyword ;; page relation format
                          :to :keyword   ;; page relation format
                          :dry-run :boolean
                          ;; page
                          :title :string ;; Page title
                          :n :long       ;; Count - eg random page count
                          :uuid :string
                          ;; filter
                          :resolve-links :boolean
                          }}))

(apply main *command-line-args*)

 ;; How to run:
 ;;
 ;;   ./play.clj relations :from :files :to :lines
