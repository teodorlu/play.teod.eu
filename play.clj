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

(deps/add-deps '{:deps {org.babashka/cli {;; :mvn/version "0.6.49"
                                          :git/url "git@github.com:babashka/cli.git"
                                          :sha "53329b5a8558159e0e6329255a8128f379bb8305"
                                          }
                        eu.teod/pandoc-toolbox {:local/root "../pandoc-toolbox"}}})

(require 'babashka.cli :reload)

(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.string :as str]
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

(defn words [& args] (str/join " " (map str args)))
(defn lines [& args] (str/join "\n" (map str args)))

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

(def relations-config
  {:sources {:files files->relations
             :lines lines->relations}
   :targets {:lines relations->lines
             :lines2 relations->lines2
             :lines+recent relations->lines+recent
             :pretty relations->pretty
             :files relations->files}})

(defn relations-helptext []
  (let [helptext (lines
                  "usage: ./play.clj relations :from SOURCE :to TARGET"
                  ""
                  "valid sources:"
                  (str "  " (apply words (keys (:sources relations-config))))
                  ""
                  "valid targets:"
                  (str "  " (apply words (keys (:targets relations-config)))))]
    helptext))

(defn relations [{:keys [opts]}]
  (let [sources {:files files->relations
                 :lines lines->relations}
        targets {:lines relations->lines
                 :lines2 relations->lines2
                 :lines+recent relations->lines+recent
                 :pretty relations->pretty
                 :files relations->files}
        {:keys [from to]} opts
        input-is-valid (and (contains? sources from)
                            (contains? targets to))]
    (if-not input-is-valid
      (println (relations-helptext))
      (let [rels ((sources from) opts)]
        ((targets to) rels)))))

(defn random-page [{:keys [opts]}]
  (if (:dry-run opts)
    (do
      ;; dry run is mostly for dev
      (pprint (->> (files->relations {})
                   vals
                   (remove #(= :remote-reference
                               (:form %)))
                   (take 5))))
    (let [n (or (:n opts) 1)]
      (println
       (str/join "\n"
                 (->> (files->relations {})
                      vals
                      (remove #(= :remote-reference
                                  (:form %)))
                      shuffle
                      (take n)
                      (map :slug)))))))

(defn create-page [{:keys [opts]}]
  (let [slug (:slug opts)
        title (or (:title opts) slug)
        uuid (or (:uuid opts) (bash "uuidgen"))
        lang (or (:lang opts) :en)
        valid-opts? (and slug title uuid lang)
        fake-spit (fn [path content]
                    (println (str  "Would write to " path ":"))
                    (println)
                    (doseq [l (str/split-lines content)]
                      (println "  " l))
                    (println))
        fake-bash (fn [& args]
                    (println "Would run:")
                    (println)
                    (println (str "  " (pr-str (apply list 'bash args))))
                    (println)
                    )
        helptext (str/trim "
Usage:

  ./play.clj create-page [:slug] SLUG [OPT...]

Allowed options:

  :title TITLE
  :uuid UUID
  :lang LANG
  :form FORM
")]
    (when (or (:h opts) (:help opts))
      (println helptext)
      (System/exit 0))
    (when (not valid-opts?)
      (println helptext)
      (System/exit 1))
    (assert slug "Page slug is required.")
    (let [org-file (str slug "/index.org")
          play-file (str slug "/play.edn")]
      (fs/create-dirs slug)

      ;; Org file
      (when-not (fs/exists? org-file)
        (let [org-file-contents (page-index-org {:title title
                                                 :uuid uuid})]
          (if (:dry-run opts)
            (fake-spit org-file org-file-contents)
            (spit org-file org-file-contents))))

      ;; Play file
      (when-not (fs/exists? play-file)
        (let [page-meta {:title title
                         :readiness :wtf-is-this
                         :uuid uuid
                         :author-url "https://teod.eu"
                         :created (str/trim (bash "date -I"))
                         :lang lang}
              page-meta (if (:form opts) (assoc page-meta :form (:form opts))
                            page-meta)
              play-file-content (with-out-str (pprint page-meta))
              ]
          (if (:dry-run opts)
            (fake-spit play-file play-file-content)
            (spit play-file play-file-content)))

        ;; Regenerate the makefile since we've added a new target
        (if (:dry-run opts)
          (fake-bash "./play.clj makefile")
          (bash "./play.clj makefile")))

      nil)))

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
         (words "pandoc -s --shift-heading-level-by=1 --from=org+smart -i" (org t) "-t json"
                "|"
                "./play.clj filter resolve-links"
                "|"
                "pandoc -f json -o" (html t) "--standalone --toc -H header-default-include.html -H scittle/scittle-with-extras.html"))))

(defn makefile [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        targets (->> (files->relations {})
                     vals
                     ;; For now, /only/ make builders for normal pandoc pages (same as default)
                     (filter (fn [rel]
                               (let [b (builder rel)]
                                 (or (= b :pandoc-page)
                                     (= b :smart-pandoc-page)))))
                     (map :slug)
                     sort)
        html (fn [target] (str target "/index.html"))
        play-edn (fn [target] (str target "/play.edn"))
        indent (fn [s] (str "\t" s))
        makefile (lines
                  "# DO NOT EDIT directly -- THIS MAKEFILE IS GENERATED"
                  "# SEE `make makefile` TARGET"
                  ""
                  ""

                  "# Generate target for root index"
                  ;; TODO root index also depends on all the play.edn files found
                  (str/join " " (concat ["index.html:" "index.clj"] (map html targets) (map play-edn targets) (list "404.html" "header-default-include.html")))
                  "\t./index.clj"
                  ""
                  ""

                  "# Generate target for 404"
                  "404.html: 404.org"
                  (indent
                   "pandoc --from=org+smart -i 404.org -t json | cat | pandoc -f json -o 404.html --standalone -H header-default-include.html -H scittle/scittle-with-extras.html")


                  ""
                  ""
                  "# Generate target for each page"
                  (str/join "\n\n"
                            (for [t targets]
                              ;; Here's the pandoc command I'd like for some pages:
                              (makefile-entry t)))
                  ""
                  ""


                  "# Note: generating the makefile with the makefile is sometimes problematic."
                  ".PHONY: makefile"
                  "makefile:"
                  "\t./play.clj makefile"
                  ""
                  ""

                  "# Rengenerate the index"
                  "# Note: we don't remove the makefile, as that gets us ... stuck."
                  ".PHONY: clean"
                  "clean:"
                  "\trm -f index.html"
                  ""
                  ""

                  "# Regenerate everything"
                  ".PHONY: ultraclean"
                  "ultraclean: clean"
                  (str "\t"
                       "rm -f "
                       ;; Here's a pandoc command I'd like for some pages
                       (str/join " " (concat ["index.html"] (map html targets))))
                  )]
    (if dry-run
      (println makefile)
      (spit "Makefile" (str (str/trim makefile) "\n")))))

(defn reindex
  "Create an index from page uuid to slug and title."
  [{:keys [opts]}]
  (let [{:keys [dry-run]} opts
        uuid-index (->> (files->relations {})
                        vals
                        (filter :uuid)
                        #_
                        (map #(select-keys % [:uuid :slug :title :id]))
                        (sort-by :uuid))
        big-index (atom [])]
    (if dry-run
      ;; just print pages
      (doseq [page uuid-index]
        (prn page))
      ;; otherwise, write to index/by-uuid/{UUID}.edn
      (do
        ;; ensure "index/by-uuid/" exists
        (fs/create-dirs "index/by-uuid/")
        ;; write an EDN file per UUID
        (doseq [page uuid-index]
          (spit (str "index/by-uuid/" (:uuid page) ".edn")
                (with-out-str (pprint page)))
          (swap! big-index conj page))
        ;; write one big EDN file
        (spit "index/big.edn" (with-out-str (pprint @big-index)))
        ;; write one big JSON index
        (spit "index/big.json" (json/generate-string @big-index {:pretty true}))
        ))))

(defn filter-pandoc [{:as opts}]
  ;; only supported filter for now is resolve-links
  ;;
  ;; Test with:
  ;;
  ;;   ./play.clj filter resolve-links < ../pandoc-toolbox/pandoc-examples/link.json

  (when (contains? (set (:rest-cmds opts))
                   "resolve-links")
    (let [pandoc-json (json/parse-string (slurp *in*))
          by-uuid (fn [uuid]
                    (let [path (str "index/by-uuid/" uuid ".edn")]
                      (when (fs/regular-file? path)
                        (edn/read-string (slurp path))))
                    )
          uuid->slug (fn [uuid] (:slug (by-uuid uuid)))
          replace-link (fn [x]
                         (if (pandoc/link? x)
                           (let [link x
                                 uuid (last (str/split (pandoc/link-target link) #":"))
                                 slug (uuid->slug uuid)]
                             (if slug
                               (assoc-in link pandoc/link-target-path (str "../" slug "/"))
                               link))
                           x))
          resolved (pandoc/filter-body-postwalk pandoc-json replace-link)]
      (println (json/generate-string resolved)))))

(defn print-help [{}]
  (println (str/trim "
Usage: ./play.clj <subcommand> <options>

Subcommands:

page SLUG :title PAGE_TITLE

relations :from RELATIONS_SOURCE :to RELATIONS_TARGET

makefile [--dry-run]

reindex [--dry-run]
")))

(defn iki.json [{:keys [opts]}]
  (let [resultat "hei på deg 🤗"]
    (if (:dry-run opts)
      (println resultat)
      (spit "iki.json" resultat))))

(def dispatch-table
  [{:cmds ["create-page"] :fn create-page :cmds-opts [:slug]}
   {:cmds ["filter"] :fn filter-pandoc :cmds-opts [:resolve-links]}
   {:cmds ["reindex"] :fn reindex}
   {:cmds ["makefile"] :fn makefile}
   {:cmds ["random-page"] :fn random-page}
   {:cmds ["relations"] :fn relations}
   {:cmds ["iki.json"] :fn iki.json}])

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
                          :from :keyword    ; page relation format
                          :to :keyword      ; page relation format
                          :dry-run :boolean
                          ;; page
                          :title :string    ; Page title
                          :n :long          ; Count - eg random page count
                          :uuid :string     ; UUID for me and Org-roam
                          :lang :keyword    ; Article language, :en or :no
                          ;; filter
                          :resolve-links :boolean
                          }}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

 ;; How to run:
 ;;
 ;;   ./play.clj relations :from :files :to :lines
