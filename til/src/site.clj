(ns site
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [pages.command-palette]
   [pages.index]
   [pages.popover-minimal]
   [pages.table-in-table]
   [pages.table-in-table-2]
   [replicant.string]))

(def files
  {"index.html" #'pages.index/render
   "command-palette/index.html" #'pages.command-palette/render
   "popover-minimal/index.html" #'pages.popover-minimal/render
   "table-in-table/index.html" #'pages.table-in-table/render
   "table-in-table-2/index.html" #'pages.table-in-table-2/render})

(def strip-index-html #(str/replace % #"index.html$" ""))

(defn pagify [[file render-fn]]
  ;; Ensure GET / finds index.html and such
  (->> (if (str/ends-with? file "index.html")
         [[file render-fn]
          [(strip-index-html file) render-fn]]
         [[file render-fn]])
       (map (fn [[file render-fn]]
              [(str "/" file) render-fn]))))

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

(comment
  (require 'preview)
  (preview/handler {:site/pages pages
                    :request-method :get
                    :uri "/"})
  )
