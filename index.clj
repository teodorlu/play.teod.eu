#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[babashka.process :as p]
         '[babashka.fs :as fs]
         '[clojure.java.shell :refer [sh]]
         '[hiccup2.core :as hiccup]
         '[clojure.pprint :refer [pprint]]
         '[clojure.edn :as edn])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(prn
 (fs/exists? "journal/play.edn"))

(defn lookup-title [{:keys [id] :as page}]
  (if (fs/exists? (str id "/play.edn"))
    (assoc page :title (:title (edn/read-string (slurp (str id "/play.edn")))))
    page))

(defn pages-raw []
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sort | sed 's|/index.html||g'")
       (str/split-lines)
       (map (fn [id]
              {:id id}))))

(defn pages []
  (->> (pages-raw)
       (map lookup-title)))

(defn link [{:keys [id title] :as page}]
  (str "- [[file:./" id "][" id "]]"))

(defn org-markup [{:keys [pages]}]
  (str/join "\n"
            (concat
             ["#+title: Towards an iterated game"
              ""
              "Intent: bring ideas to life. Discuss, sharpen, play."
              ""
              "Status: very much work in progress. Please advance at your own peril."
              ""
              "Pages:"]

             (for [page pages]
               (link page))

             ["Possible next steps:

- Write real content"]
             )))

;; For development:
;;
;;   export ALT=1
;;   watchexec -c -- ./index.clj
;;
;; For prod:
;;
;;   ./index.clj

(defn alt
  "Feel free to change this to whatever during dev.

  Functions should be modularized / parameterized to allow for reasonable experience in dev."
  []
  (pprint (pages))
  #_#_
  (println (org-markup {:pages (pages)}))
  (prn (System/getenv "ALT")))

(defn main []
  (spit "index.html" (slurp (:out
                             @(p/process '[pandoc --from org --to html --standalone]
                                         {:in (org-markup {:pages (pages)})})))))

(if (= (System/getenv "ALT")
       "1")
  (alt)
  (main))
