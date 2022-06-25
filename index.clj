#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[babashka.process :as p]
         '[clojure.java.shell :refer [sh]]
         '[hiccup2.core :as hiccup])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sort | sed 's|/index.html||g'")
       (str/split-lines)))

(def org-markup
  (str/join "\n"
            (concat
             ["#+title: Towards an iterated game"
              ""
              "Intent: bring ideas to life. Discuss, sharpen, play."
              ""
              "Status: very much work in progress. Please advance at your own peril."
              ""
              "Pages:"]

             (for [target targets]
               (str "- [[file:./" target "][" target "]]"))

             ["Possible next steps:

- Write real content"]
             )))

(spit "index.html" (slurp (:out
                           @(p/process '[pandoc --from org --to html --standalone] {:in org-markup}))))
