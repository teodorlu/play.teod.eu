#!/usr/bin/env bb

(ns slideshow
  (:require
   [babashka.fs :as fs]))

(defn this-folder [] (fs/parent *file*))

(defn slide-files []
  (->> (fs/glob (fs/file (this-folder) "slides") "*.txt")
       (map str)
       sort))

(defn -main []
  (doseq [f (slide-files)]
    (println (slurp f))
    (read-line)))

(when (= *file* (System/getProperty "babashka.file"))
  (apply -main *command-line-args*))
