#!/usr/bin/env bb

(require '[clojure.string :as str]
         '[babashka.process :as p]
         '[babashka.fs :as fs]
         '[clojure.java.shell :refer [sh]]
         '[clojure.pprint :refer [pprint]]
         '[clojure.edn :as edn])

(defn bash [cmd]
  (-> (sh "bash" "-c" cmd)
      :out))

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
       (map (fn [id]
              {:id id}))))

(defn category [{:keys [lang readiness] :as page}]
  (cond
    (= lang :no) :norwegian
    (= readiness :ready-for-comments) :ready-for-comments
    (= readiness :wtf-is-this) :wtf-is-this
    :else :other))

(defn add-category [page]
  (assoc page :category (category page)))

(defn pages []
  (->> (pages-raw)
       (map lookup-meta)
       (map add-defaults)
       (map add-category)
       (sort-by :title)))

(defn link [{:keys [id title readiness] :as _page}]
  (str "- [[file:./" id "/][" (or title id) "]]"))

(defn org-markup [{:keys [pages]}]
  (let [{:keys [ready-for-comments norwegian wtf-is-this other]} (group-by :category pages)]
    (str/join "\n"
              (concat
               ["#+title: Towards an iterated game"
                ""
                "Intent: bring ideas to life. Discuss, sharpen, play."
                ""
                "Status: very much work in progress. Please advance at your own peril."
                ""]

               ["Ready for comments:"]
               (for [page ready-for-comments]
                 (link page))

               ["Uncategorized:"]
               (for [page other]
                 (link page))

               ["Messy notes, probably not useful."
                " But still open -- information wants to be free:"]
               (for [page wtf-is-this]
                 (link page))

               [""
                "Norwegian content:"]
               (for [page norwegian]
                 (link page))

               ["Comments? Hit me up! Details on [[https://teod.eu][teod.eu]]."]))))

;; For development:
;;
;;   export ALT=1
;;   watchexec -c -- ./index.clj
;;
;; For prod:
;;
;;   ./index.clj

(defn alt
  "Feel free to change this to whatever during dev.

  Functions should be modularized / parameterized to allow for reasonable experience in dev."
  []
  (pprint (group-by :category (pages)))
  #_#_
  (println (org-markup {:pages (pages)}))
  (prn (System/getenv "ALT")))

(defn main []
  (spit "index.html" (slurp (:out
                             @(p/process '[pandoc --from org --to html --standalone]
                                         {:in (org-markup {:pages (pages)})})))))

(if (= (System/getenv "ALT")
       "1")
  (alt)
  (main))
