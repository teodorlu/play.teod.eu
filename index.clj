#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[babashka.process :as p]
         '[clojure.java.shell :refer [sh]]
         '[hiccup2.core :as hiccup])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(defn pages []
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sort | sed 's|/index.html||g'")
       (str/split-lines)))

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

             (for [target pages]
               (str "- [[file:./" target "][" target "]]"))

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

(defn alt []
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
