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
                  "pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -i " (org t)
                  (if css
                    " --css=../pandoc.css"
                      "")
                  " -o " (html t)))))
(println "")
(println "")

;; Generate target for Makefile (LOL)
;; Nope, this doesn't work. Or, we'd have to somehow ...
;; No.
;; Making the makefile would have to be done in advance of every run, I think.
;; If it would work
;; But perhaps I should just automate that as part of the "create new page" action".
;; 🤔
;; (println "Makefile: makemakefile.clj")
;; (println "\t./makemakefile.clj > Makefile")
;; (println "")
;; (println "")

;; Clean target
(println ".PHONY: clean")
(println "clean:")
(println "\trm index.html")
