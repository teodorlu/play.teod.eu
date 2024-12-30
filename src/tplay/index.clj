(ns tplay.index
  (:require
   [babashka.fs :as fs]
   [clojure.edn :as edn]
   [clojure.java.shell :refer [sh]]
   [clojure.string :as str]
   [hiccup2.core :as hiccup]
   [tplay.nopandoc.landing :as landing]))

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd) :out))

(defn lookup-meta [{:keys [id] :as page}]
  (if (fs/exists? (str id "/play.edn"))
    ;; if there's a title set, we use it
    (merge page (edn/read-string (slurp (str id "/play.edn"))))
    page))

(defn add-defaults [{:keys [id title] :as page}]
  (assoc page :title (or title id)))

(defn pages-raw []
  (->> (bash "ls **/index.html | grep -v '^index.html$' | sort | sed 's|/index.html||g'")
       (str/split-lines)
       (map (fn [id] {:id id}))))

(defn category [{:keys [lang readiness form] :as page}]
  (cond
    (= readiness :noindex) :noindex       ;; don't link to this at all
    (= readiness :deprecated) :deprecated ;; nothing to see here
    (= form :remote-reference) :remote-reference
    (and (= readiness :wtf-is-this) (= lang :no)) :wtf-is-this-norwegian
    (= lang :no) :norwegian
    (= readiness :ready-for-comments) :ready-for-comments
    (= readiness :wtf-is-this) :wtf-is-this
    (= readiness :forever-incomplete) :forever-incomplete
    :else :other))

(defn add-category [page]
  (assoc page :category (category page)))

(defn find-pages []
  (->> (pages-raw)
       (map lookup-meta)
       (map add-defaults)
       (map add-category)
       (sort-by :title)))

(defn main2 []
  (spit "index.html"
        (str "<!DOCTYPE html>"
             (hiccup2.core/html {}
               (landing/handler {:the-pages (find-pages)})))))
