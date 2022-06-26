#!/usr/bin/env bb



;; Action - edit tags
;;
;;   ./play relations :from :files :to :lines > lines.txt
;;   # edit lines.txt - do stuff in batch ...
;;   ./play relations :from :lines :to :files < lines.txt

(require '[babashka.deps :as deps])
(deps/add-deps '{:deps {org.babashka/cli {:mvn/version "0.3.30"}}})
(require '[babashka.cli :as cli]
         '[clojure.java.shell]
         '[clojure.string :as str]
         '[clojure.edn :as edn]
         '[clojure.pprint :refer [pprint]])

;; relations example
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
  (let [stdin (slurp *in*)]
    (->> (for [line (str/split-lines stdin)]
           (let [{:keys [id] :as page} (edn/read-string line)]
             page
             ))
         (map (fn [{:keys [id] :as page}] {id page}))
         (into {}))))

(defn relations [{:keys [opts]}]
  (let [sources {:files files->relations
                 :lines lines->relations}
        targets {:lines relations->lines
                 :pretty relations->pretty}
        {:keys [from to]} opts]
    (assert (sources from))
    (assert (targets to))
    (let [rels ((sources from) {})]
      ((targets to) rels))))

(defn main [& args]
  (cli/dispatch [{:cmds ["relations"] :fn relations}]
                args
                {:coerce {:from :keyword
                          :to :keyword
                          :dry-run :boolean}}))

(apply main *command-line-args*)

 ;; How to run:
 ;;
 ;;   ./play2.clj relations :from :files :to :lines
