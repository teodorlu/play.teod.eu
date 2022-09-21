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
    (and (= readiness :wtf-is-this) (= lang :no)) :wtf-is-this-norwegian
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

(defn org-link [{:keys [name href]}]
  (str "[[" href "][" name "]]"))

(defn page-link
  "Link to a page on play.teod.eu"
  [{:keys [id title] :as _page}]
  (str "[[file:./" id "/][" (or title id) "]]"))

(defn page-link-with-date [{:keys [created] :as page}]
  (let [date-str (when created
                   (str " (" created ")"))]
    (str (page-link page) (or date-str ""))))

(defn org-markup [{:keys [pages]}]
  (let [{:keys [ready-for-comments norwegian wtf-is-this wtf-is-this-norwegian other forever-incomplete]} (group-by :category pages)
        sentences (fn [& ss] (str/join " " ss))
        lines2 (fn [& ls] (str/join "\n" (apply concat ls)))
        lines (fn [& ls] (str/join "\n" (map str ls)))
        paragraphs (fn [& ps] (str/join "\n\n" ps))]
    (lines2
     [(paragraphs "#+title: Towards an iterated game"
                  (sentences "Intent: bring ideas to life."
                             "Discuss, sharpen, play."
                             "Minimize the distance between intent and reality.")
                  (sentences "Process: Aim intent towards curiosity --- explore --- refactor towards orthogonality."
                             "Embrace remix culture.")
                  (sentences "Status: work in progress, lots of rough edges."
                             "But you're /very/ much welcome to have a look around 🤗")
                  (str "Most content on this site is authored by Teodor Heggelund"
                       " (" (org-link {:href "https://teod.eu" :name "https://teod.eu"}) ")"
                       ".")

                  (lines "#+BEGIN_EXPORT html"
                         "<p>"
                         "<random-page-button>"
                         "Go to random page"
                         "</random-page-button>"
                         "(doesn't look like a button but please try pressing it)"
                         "</p>"
                         "#+END_EXPORT")

                  )]
     [""]

     [(paragraphs "** Content that's ready for the eyes of others"
                  "Feel free to have a look :)")]

     (for [page (sort-by :created  ready-for-comments)]
       (str "- " (page-link-with-date page)))

     [(paragraphs "** Other people's idea playgrounds"
                  (lines
                   "Ideas are best shared!"
                   "Some choose to share ideas in public."
                   "I think that's good.")
                  "In alphabetical order:")]
     (for [{:keys [name href]}
           (sort-by :name
                    [{:name "Sindre's Random Ramblings" :href "https://play.sindre.me/"}
                     {:name "Kevin's WikiBlog" :href "https://kevin.stravers.net/"}])]
       (str "- " (org-link {:name name :href href}) " (off-site link)"))

     (when (seq other)
       ["** Uncategorized\n"
        (str/join " --- " (for [page other] (page-link page)))
        ""])

     (when (seq forever-incomplete)
       [(paragraphs "** Forever incomplete"
                    (lines
                     "Some content is eventually /done/."
                     "Not journals.")
                    ":)"
                    (str/join " --- " (for [page forever-incomplete] (page-link page))))
        ""])

     ["** Norwegian content"
      ""
      "Not everybody speaks Norwegian. But some do!"
      ""]
     (for [page norwegian] (str "- " (page-link page)))
     [""]

     [(paragraphs "** Seeds, drafts, references and vague ideas, please ignore."
                  (lines "Feel free to skip all these."
                         "Or not."
                         "Expect the average seed to be messy and incomplete.")
                  (str/join " · " (for [page wtf-is-this] (page-link page))))
      ""]

     ["** Seeds and vague ideas in Norwegian, please ignore these too."
      ""
      (str/join " · " (for [page wtf-is-this-norwegian] (page-link page)))
      ""
      ]

     [""
      "** Efforts at \"writing things down together\" commonly fail because:

1. We put things prematurely into large hierarchies that collapse
2. The inability to critique the hierarchy itself --- and iterate on the hierarchy
3. The inability to have separate hierarchies
4. More abstractly --- we need a /fractal/ structure, not a hierarchical one.
5. Unclear content authority --- whose content is this? Who is responsible?
6. Mixing good stuff and bad stuff makes the good stuff hard to find
7. It's hard to find any good stuff whatsoever
8. It's hard to find anything that's not on the front page
9. It's impossible to build other views than the default
10. It's impossible to embed things that should not be built at all

I include this list as a personal reminder.
"]
     [(paragraphs "** What is this?"
                  (lines
                   "Good question!"
                   "What this /is/ is perhaps less useful than what this is /for/.")
                  "** Then, what is this /for/??"
                  (lines
                   "It's for playing with ideas."
                   "It's for breathing air into the bad ones, and the good ones.")
                  (lines
                   "Canonical URL for this page is [[https://play.teod.eu][play.teod.eu]]."
                   "Local development URL for this page is [[http://localhost:9945/][localhost:9945]]."
                   ))]



     )))

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
                             @(p/process '[pandoc --from org+smart -H header-default-include.html --to html --standalone]
                                         {:in (org-markup {:pages (pages)})})))))

(if (= (System/getenv "ALT")
       "1")
  (alt)
  (main))
