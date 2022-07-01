#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/index.org | grep -v '^index.org$' | sed 's|/index.org||g'")
       (str/split-lines)))

(defn org [target]
  (str target "/index.org"))

(defn html [target]
  (str target "/index.html"))

(defn play-edn [target]
  (str target "/play.edn"))

;; Generate phony target for pages
(println ".PHONY: everything")
(println "everything: " (str/join " " (concat ["index.html"] (map html targets))))
(println "")
(println "")

;; Generate target for root index
;;
;; TODO root index also depends on all the play.edn files found
(println (str/join " " (concat ["index.html:" "index.clj"] (map play-edn targets))))
(println "\t./index.clj")
(println "")
(println "")

(def css false)

;; Generate target for each page
(println
 (str/join "\n\n"
           (for [t targets]
             (str (html t) ": " (org t)
                  "\n\t"
                  "pandoc -s --shift-heading-level-by --toc --from=org+smart -i " (org t)
                  (if css
                    " --css=../pandoc.css"
                      "")
                  " -o " (html t)))))
(println "")
(println "")

;; Clean target
(println ".PHONY: clean")
(println "clean:")
(println "\trm index.html")
