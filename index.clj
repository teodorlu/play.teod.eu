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
    (= readiness :forever-incomplete) :forever-incomplete
    :else :other))

(defn add-category [page]
  (assoc page :category (category page)))

(defn pages []
  (->> (pages-raw)
       (map lookup-meta)
       (map add-defaults)
       (map add-category)
       (sort-by :title)))

(defn link [{:keys [id title] :as _page}]
  (str "[[file:./" id "/][" (or title id) "]]"))

(defn org-markup [{:keys [pages]}]
  (let [{:keys [ready-for-comments norwegian wtf-is-this other forever-incomplete]} (group-by :category pages)
        sentences (fn [& ss] (str/join " " ss))]
    (str/join "\n"
              (concat
               ["#+title: Towards an iterated game"
                ""
                (sentences "Intent: bring ideas to life."
                           "Discuss, sharpen, play."
                           "Minimize the distance between intent and reality.")
                ""
                (sentences "Process: Aim intent towards curiosity --- explore --- refactor towards orthogonality."
                           "Embrace remix culture.")

                ""
                "Status: work in progress, lots of rough edges."
                ""]

               ["** Ready for comments"]
               (for [page ready-for-comments]
                 (str "- " (link page)))

               (when (seq other)
                 ["** Uncategorized\n"
                  (str/join " --- " (for [page other] (link page)))
                  ""])

               (when (seq forever-incomplete)
                 ["** Forever incomplete\n"
                  (str/join " --- " (for [page forever-incomplete] (link page)))
                  ""])

               ["** Vague ideas, please ignore."
                ""
                "Links to these mostly exist for me (Teodor)."
                "But still open, information wants to be free."
                ""
                (str/join " ?? " (for [page wtf-is-this] (link page)))
                ""]

               [""
                "** Norwegian content"
                ""
                "Not everybody speaks Norwegian. But some do!"
                ""]
               (for [page norwegian] (str "- " (link page)))

               ["** Comments? Hit me up."
                "Details on [[https://teod.eu][teod.eu]]."]
               [""
                "** Efforts at \"writing things down together\" commonly fail because:

1. We put things prematurely into large hierarchies that collapse
2. The inability to critique the hierarchy itself --- and iterate on the hierarchy
3. The inability to have separate hierarchies
4. More abstractly --- we need a /fractal/ structure, not a hierarchical one.
5. Unclear content authority --- whose content is this? Who is responsible?
6. Mixing good stuff and bad stuff makes the good stuff hard to find
7. It's hard to find the good stuff whatsoever
8. It's hard to find anything that's not on the front page
9. It's impossible to build other views than the default
10. It's impossible to embed things that should not be built at all

I include this list as a personal reminder.
"]
               ["** What is this?"
                ""
                "Good question :)"
                "Having a look at the source might be useful: [[https://github.com/teodorlu/play.teod.eu][teodorlu/play.teod.eu]]."
                ""
                "PRs are probably a bad idea."
                "I prefer a good discussion to rewriting each other's ideas."
                "And please, let yourself be inspired if you want to create something similar."
                ""
                "[[https://play.teod.eu][play.teod.eu]]"
                ]

               ))))

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
                             @(p/process '[pandoc --from org+smart -H live.html --to html --standalone]
                                         {:in (org-markup {:pages (pages)})})))))

(if (= (System/getenv "ALT")
       "1")
  (alt)
  (main))
