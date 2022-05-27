#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sed 's|/index.html||g'")
       (str/split-lines)))

(defn org [target]
  (str target "/index.org"))

(defn html [target]
  (str target "/index.html"))

;; Generate makefile target for each page
(println
 (str/join "\n\n"
           (for [t targets]
             (str (html t) ": " (org t)
                  "\n\t"
                  "pandoc -s -i " (org t) " -o " (html t)))))

;; Generate makefile target for all pages
(println "")
(println "@PHONY: pages")
(println "pages: " (str/join " " (map html targets)))
(println "")
