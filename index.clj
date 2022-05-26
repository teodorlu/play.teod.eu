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
            (for [target targets]
              (str "- [[" target "]]"))))

(defn pandoc [{:keys []}])

(println
 (slurp (:out
         @(p/process '[pandoc --from org --to html --standalone] {:in org-markup}))))
