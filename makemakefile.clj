#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[clojure.java.shell :refer [sh]])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

(def targets
  (->> (bash "ls **/play.edn | grep -v '^play.edn$' | sed 's|/play.edn||g'")
       (str/split-lines)))

(defn org [target]
  (str target "/index.org"))

(defn html [target]
  (str target "/index.html"))

(defn play-edn [target]
  (str target "/play.edn"))

(println "# DO NOT EDIT DIRECTLY -- THIS MAKEFILE IS GENERATED")
(println "# SEE `make clean` TARGET")
(println "")
(println "")

;; (println "# Generate phony target for all pages - default target")
;; (println ".PHONY: everything")
;; (println "everything: " (str/join " " (concat ["index.html"] (map html targets))))
;; (println "")
;; (println "")

(println "# Generate target for root index")
;; TODO root index also depends on all the play.edn files found
(println (str/join " " (concat ["index.html:" "index.clj"] (map html targets) (map play-edn targets))))
(println "\t./index.clj")
(println "")
(println "")

(println  "# Generate target for each page")
(println
 (str/join "\n\n"
           (for [t targets]
             (str (html t) ": " (org t)
                  "\n\t"
                  "pandoc -s --shift-heading-level-by=1 --toc --from=org+smart -H live.html -i " (org t)
                  " -o " (html t)))))
(println "")
(println "")


(println ".PHONY: makefile")
(println "makefile:")
(println "\t./makemakefile.clj > Makefile")
(println "")
(println "")

(println "# Rengenerate the index")
(println ".PHONY: clean")
(println "clean:")
(println "\trm -f index.html")
(println "")
(println "")

(println "# Regenerate everything")
(println ".PHONY: ultraclean")
(println "ultraclean: clean")
(println (str "\t"
              "rm -f "
              (str/join " " (concat ["index.html"] (map html targets)))))
