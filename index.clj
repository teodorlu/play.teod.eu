#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[babashka.process :as p]
         '[clojure.java.shell :refer [sh]]
         '[hiccup2.core :as hiccup])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sed 's|/index.html||g'")
       (str/split-lines)))

(def org-markup
  (str/join "\n"
            (concat
             ["#+title: Teodor's playground"
              ""
              "Intent: bring ideas to life. Discuss, sharpen."
              ""
              "Pages:"]

             (for [target targets]
               (str "- [[file:./" target "][" target "]]"))

             ["Possible next steps:

- Write actual content
- Organize build with normal makefile. I could use .pandoc.org for the stuff I just want to build with pandoc."]
             )))

(spit "index.html" (slurp (:out
                           @(p/process '[pandoc --from org --to html --standalone] {:in org-markup}))))
