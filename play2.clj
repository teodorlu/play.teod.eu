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

(defn page-index-org [{:keys [title trailing-blank-lines]}]
  (str/join "\n"
            (concat
             ;; Header, title, link up
             [(str "#+title: " title)
              "

[[./..][..]]

DRAFT

TODO write article
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
  (let [page (:page opts)]
    (assert page "Please specify which page to create!")
    (let [org-file (str page "/index.org")
          play-file (str page "/play.edn")]
      (fs/create-dirs page)

      ;; Org file
      (when-not (fs/exists? org-file)
        (spit org-file (page-index-org {:title page
                                        :trailing-blank-lines 20})))

      ;; Play file
      (when-not (fs/exists? play-file)
        (spit play-file (pr-str {:title page
                                 :readiness :wtf-is-this
                                 :author-url "https://teod.eu"})))

      nil)))

(defn main [& args]
  (cli/dispatch [{:cmds ["relations"] :fn relations}
                 {:cmds ["page"] :fn create-page :cmds-opts [:page]}
                 {:cmds ["create-page"] :fn create-page :cmds-opts [:page]}
                 {:cmds ["random-page"] :fn random-page}]
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
 ;;   ./play2.clj relations :from :files :to :lines
