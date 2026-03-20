(ns site
  (:require
   [babashka.fs :as fs]
   [pages.command-palette]
   [pages.index]
   [pages.popover-minimal]
   [replicant.string]))

(def files
  {"index.html"                   #'pages.index/render
   "command-palette/index.html"   #'pages.command-palette/render
   "popover-minimal/index.html"   #'pages.popover-minimal/render})

(defn pagify
  "Given [file render-fn], return route entries for preview.
   index.html files also get a directory route."
  [[file render-fn]]
  (let [path (str "/" file)]
    (if (.endsWith file "index.html")
      [[path render-fn]
       [(subs path 0 (- (count path) (count "index.html"))) render-fn]]
      [[path render-fn]])))

(def pages
  (->> files
       (mapcat pagify)
       (into {})))

(defn inject [req]
  (assoc req :site/pages pages))

;; clojure -X site/build
(defn build
  ([] (build {}))
  ([_]
   (doseq [[file render] files]
     (let [out (fs/file "build" file)]
       (fs/create-dirs (fs/parent out))
       (spit out (str "<!DOCTYPE html>\n"
                      (replicant.string/render (render))))))))
