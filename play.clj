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
;;   ./play makefile > makefile # generate makefile
;;   ./play preview
;;
;; Wanted behavior:
;;
;;   When I create a new page, I write :author-url and :created-at

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/cli {:mvn/version "0.3.31"}}})
(require '[babashka.cli :as cli]
         '[babashka.fs :as fs]
         '[clojure.java.shell]
         '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[clojure.pprint :refer [pprint]])

;; relations example
;;
;; page id -> page metadata
{"emacs" {:id "emacs"
          :title "(Doom) Emacs learning journal"
          :form :rambling
          :readiness :in-progress}
 "feedback-design-impl" {:title "Feedback loops, API design and how stuff works"}}

(defn bash [cmd]
  (str/trim (:out (clojure.java.shell/sh "bash" "-c" cmd))))

(defn pages []
  (->> (bash "ls **/play.edn | sed 's|/play.edn||g'")
       (str/split-lines)
       (map (fn [id] {:id id}))))

(defn files->relations
  "Read relations from play.edn files on disk"
  [{}]
  (->> (pages)
       (map (fn [{:keys [id] :as p}]
              (merge p (edn/read-string (slurp (str id "/play.edn"))))))
       (map (juxt :id identity))
       (into {})))

(defn relations->lines
  "Produce one line per page

  Example:

    :id emacs, :title \"(Doom) Emacs learning journal\", :form :rambling, :readiness :in-progress"
  [rels]
  (doseq [l (vals rels)]
    (prn l)))

(defn relations->lines2
  "Produce one line per relation

  Output format:

    [:id \"emacs\"] :title \"(Doom) Emacs learning journal\"
    [:id \"emacs\"] :form :rambling
    [:id \"emacs\"] :readiness :in-progress

  Example: how many WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep -i \":readiness :wtf\" | wc -l
    30

  Example: how many non-WTF-pages do we have?

    ./play.clj relations :from files :to lines2 | grep :readiness | grep -v \":readiness :wtf\" | wc -l
    9
  "
  [rels]
  (doseq [[id page-meta] rels]
    (doseq [[attribute value] (dissoc page-meta :id)]
      (prn [:id id] attribute value))))

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
       (map (fn [{:keys [id] :as page}] {id page}))
       (into {})))

(defn relations->files [rels]
  (doseq [[id page] rels]
    (spit (str id "/play.edn")
          (with-out-str
            (pprint (dissoc page :id))))))

(defn page-index-org [{:keys [title trailing-blank-lines org-id]}]
  (str/join "\n"
            (concat
             ;; Link for org-roam
             (when org-id
               [":PROPERTIES:"
                (str ":ID: " org-id)
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
    (let [rels ((sources from) {})]
      ((targets to) rels))))

(defn random-page [{:keys [opts]}]
  (let [n (or (:n opts) 1)]
    (println
     (str/join "\n"
               (->> (files->relations {})
                    vals
                    shuffle
                    (take n)
                    (map :id))))))

(defn create-page [{:keys [opts]}]
  (let [page (:page opts)
        title (or (:title opts) page)]
    (assert page "Please specify which page to create!")
    (let [org-file (str page "/index.org")
          play-file (str page "/play.edn")]
      (fs/create-dirs page)

      ;; Org file
      (when-not (fs/exists? org-file)
        (spit org-file (page-index-org {:title title
                                        :org-id (bash "uuidgen")})))

      ;; Play file
      (when-not (fs/exists? play-file)
        (spit play-file (pr-str {:title title
                                 :readiness :wtf-is-this
                                 :author-url "https://teod.eu"
                                 :created (str/trim (bash "date -I"))})))

      ;; Regenerate the makefile since we've added a new target
      (bash "./makemakefile.clj > Makefile")

      nil)))

(defn print-help [{}]
  (println (str/trim "
Usage: ./play.clj <subcommand> <options>

Subcommands:

page PAGE_ID :title PAGE_TITLE

relations :from :files :to :lines
")))

(defn main [& args]
  (cli/dispatch [{:cmds ["relations"] :fn relations}
                 {:cmds ["page"] :fn create-page :cmds-opts [:page]}
                 {:cmds ["create-page"] :fn create-page :cmds-opts [:page]}
                 {:cmds ["random-page"] :fn random-page}
                 {:cmds ["help"] :fn print-help}
                 {:cmds [] :fn print-help}]
                args
                {:coerce {;; relations
                          :from :keyword ;; page relation format
                          :to :keyword   ;; page relation format
                          :dry-run :boolean
                          ;; page
                          :title :string ;; Page title
                          :n :long       ;; Count - eg random page count
                          }}))

(apply main *command-line-args*)

 ;; How to run:
 ;;
 ;;   ./play.clj relations :from :files :to :lines
