#!/usr/bin/env bb

;; Action - edit tags
;;
;;   ./play relations :from :files :to :lines > lines.txt
;;   # edit lines.txt - do stuff in batch ...
;;   ./play relations :from :lines :to :files < lines.txt
;;
;; Wanted commands:
;;
;;   ./play index > index.html # generate index.html
;;   ./play makefile > makefile # generate makefile
;;   ./play page rdf-intro :title "Introduction to linked data with RDF and Datalog"
;;   ./play preview
;;
;; Wanted behavior:
;;
;;   When I create a new page, I write :author-url and :created-at

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/cli {:mvn/version "0.3.30"}}})
(require '[babashka.cli :as cli]
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

(defn pages []
  (let [bash (fn [cmd] (:out (clojure.java.shell/sh "bash" "-c" cmd)))]
    (->> (bash "ls **/play.edn | sed 's|/play.edn||g'")
         (str/split-lines)
         (map (fn [id] {:id id})))))

(defn files->relations
  "Read relations from play.edn files on disk"
  [{}]
  (->> (pages)
       (map (fn [{:keys [id] :as p}]
              (merge p (edn/read-string (slurp (str id "/play.edn"))))))
       (map (juxt :id identity))
       (into {})))

(defn relations->lines
  "Produce a list of strings looking like

  :id emacs, :title \"(Doom) Emacs learning journal\", :form :rambling, :readiness :in-progress
  "
  [rels]
  (doseq [l (vals rels)]
    (prn l)))

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

(defn relations->table
  "Produce a table of relations

  Example table:

  | :id                    | :title                           |
  |------------------------+----------------------------------|
  | \"unix-signals-intro\" | \"Unix signals: a crash course\" |
  | \"aphorisms\"          | \"Aphorisms\"                    |
  "
  [rels]
  (let [pr-value (fn [value]
                   (if (= value ::missing)
                     ""
                     (pr-str value)))
        columns (keys (apply merge (vals rels)))
        value-row (fn [items]
                    (str "| " (str/join " | " (map pr-value items)) " |"))
        string-row (fn [items]
                    (str "|" (str/join "|" items) "|"))]
    ;; print header
    (println (value-row columns))
    (println (string-row (map (fn [_] "-----") columns)))
    (doseq [page (vals rels)]
      (println (value-row
                (map (fn [column]
                       (get page column ::missing))
                     columns))))))

(defn table->relations
  "Read relations from a table on stdin

  Example table:

  | :id                    | :title                           |
  |------------------------+----------------------------------|
  | \"unix-signals-intro\" | \"Unix signals: a crash course\" |
  | \"aphorisms\"          | \"Aphorisms\"                    |
  "
  [{}]
  (let [lines (into [] (str/split-lines (slurp *in*)))
        header-raw (first lines)
        header (->> (str/split header-raw #"\|") ; | is column sep
                    (drop 1) (drop-last 1)       ; first and last is sole |
                    (map str/trim)
                    (map edn/read-string))
        body-raw (drop 2 lines)
        parse-line (fn [s]
                     (->>
                      (str/split s #"\|")
                      (drop 1) (drop-last 1)
                      (map str/trim)
                      (map (fn [key value] [key value]) header)
                      (filter (fn [[_ value]]
                                (not= value "")))
                      (map (fn [[key value]]
                             [key (edn/read-string value)]))
                      (into {})))]
    (->> body-raw
         (map parse-line)
         (map (fn [{:keys [id] :as page}] {id page}))
         (into {}))))

(defn page-index-org [{:keys [title trailing-blank-lines]}]
  (str/join "\n"
            (concat
             ;; Header, title, link up
             [(str "#+title: " title)
              "

DRAFT

[[./..][..]]
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
                 :lines lines->relations
                 :table table->relations}
        targets {:lines relations->lines
                 :pretty relations->pretty
                 :files relations->files
                 :table relations->table}
        {:keys [from to]} opts]
    (assert (sources from))
    (assert (targets to))
    (let [rels ((sources from) {})]
      ((targets to) rels))))

(defn random-page [{:keys [_opts]}]
  (println
   (->> (files->relations {})
        vals
        rand-nth
        :id)))

(defn page [{:keys [opts]}]
  (prn opts))

(defn main [& args]
  (cli/dispatch [{:cmds ["relations"] :fn relations}
                 {:cmds ["page"] :fn page}
                 {:cmds ["random-page"] :fn random-page}]
                args
                {:coerce {;; relations
                          :from :keyword
                          :to :keyword
                          :dry-run :boolean
                          ;; page
                          :title :string
                          }}))

(apply main *command-line-args*)

 ;; How to run:
 ;;
 ;;   ./play2.clj relations :from :files :to :lines
