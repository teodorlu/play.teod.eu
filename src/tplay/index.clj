(ns tplay.index
  (:require
   [clojure.string :as str]
   [babashka.process :as p]
   [babashka.fs :as fs]
   [clojure.java.shell :refer [sh]]
   [clojure.edn :as edn]
   [hiccup2.core :as hiccup]
   [tplay.go :as go]))

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

(defn pages []
  (->> (pages-raw)
       (map lookup-meta)
       (map add-defaults)
       (map add-category)
       (sort-by :title)))

(defn org-link
  "Takes a map with :page/href and :page/name, returns an org-mode formatted link.

  Supports unqualified keywords (:href and :name) for backwards compatibility."
  [page]
  (let [href (or (:page/href page) (:href page))
        name (or (:page/title page) (:page/name page) (:name page))]
    (when (and href name)
      (str "[[" href "][" name "]]"))))

(defn page-link
  "Link to a page on play.teod.eu"
  [{:keys [id title] :as _page}]
  (str "[[file:./" id "/][" (or title id) "]]"))

(defn lines [& lines]
  (str/join "\n" (filter some? lines)))

#_ (lines "a" "b" nil "" "" "c")

(defn org-img
  ([image-file]
   (org-img image-file {}))
  ([image-file {:keys [caption name]}]
   (assert image-file)
   (lines (when caption (str "#+CAPTION: " caption))
          (when name (str "#+NAME: " name))
          (str "[[file:" image-file "]]"))))

#_(org-img "./map.webp")

(defn page-link-with-date [{:keys [created published] :as page}]
  (let [date-str (when-let [date (or published created)]
                   (str " (" date ")"))]
    (str (page-link page) (or date-str ""))))

(defn org-export [tag body]
  (str/join "\n" [(str "#+BEGIN_EXPORT " tag)
                  body
                  "#+END_EXPORT "]))

#_ (println (org-export "html" (hiccup/html [:pre "Hellooo"])))

(defn index-org-markup [{:keys [pages]}]
  (let [{:keys [ready-for-comments norwegian
                wtf-is-this wtf-is-this-norwegian
                other forever-incomplete
                remote-reference
                deprecated]}
        (group-by :category pages)
        sentences (fn [& ss] (str/join " " ss))
        words sentences
        lines2 (fn [& ls] (str/join "\n" (apply concat ls)))
        lines (fn [& ls] (str/join "\n" (map str ls)))
        paragraphs (fn [& ps] (str/join "\n\n" ps))]

    (lines2
     [(paragraphs "#+title: Towards an iterated game 🩵"

                  (org-export "html"
                              (hiccup/html [:button
                                            {:is "iki-goto-random-page"
                                             :style {:display :block
                                                     :margin "0px auto"
                                                     :font-size "16px"}}
                                            "Go to random page"]))

                  (org-export "html"
                              (hiccup/html [:div {:style {:height "2rem"}}]))

                  (org-export "html"
                              (hiccup/html
                                  [:div {:style {}}
                                   (go/principles-component go/theme-main
                                                            {:section-style/overrides go/section-style-left-adjust
                                                             :container-style/overrides {:padding "2rem"}})]))

                  (sentences "Intent: bring ideas to life."
                             "Discuss, sharpen, play."
                             "Minimize the distance between intent and reality.")
                  (sentences "Process: Aim intent towards curiosity---explore---refactor towards orthogonality."
                             "Embrace remix culture."
                             "Legibility is a partially provided affordance, not a design constraint.")
                  (sentences "Status: work in progress, plenty of rough edges."
                             "But you're /very/ much welcome to have a look around!")
                  (str "Most content on this site is authored by Teodor Heggelund"
                       " (" (org-link {:href "https://teod.eu" :name "https://teod.eu"}) ")"
                       "."))]
     [""]

     [(paragraphs "** Content that's ready for the eyes of others"
                  "Feel free to have a look :)"

                  "Things I believe:"
                  (apply lines
                         (for [page (filter #(nil? (:created %)) ready-for-comments)]
                           (str "- " (page-link-with-date page))))

                  "Things I've written:"
                  (apply lines
                         (for [page (reverse (sort-by :created (filter (comp some? :created ) ready-for-comments)))]
                           (str "- " (page-link-with-date page)))))]

     [(paragraphs "** Other people's sites"
                  (lines "Some people choose to share what they believe on the Internet."
                         "I appreciate that!")
                  "In alphabetical order:")]
     (for [site
           (sort-by #(or (:page/name %) (:name %))
                    [                   ; people I know to some extent
                     {:page/name "Sindre's Random Ramblings"
                      :page/href "https://play.sindre.me/"}
                     {:page/name "Kevin's WikiBlog"
                      :page/href "https://kevin.stravers.net/"
                      :site/reference-article {:page/title "SimpleLinearLists"
                                               :page/href "https://kevin.stravers.net/SimpleLinearLists"}}
                     {:page/name "blog.oddmundo.com"
                      :page/href "https://blog.oddmundo.com/"
                      :site/reference-article {:page/title "test && commit || revert ; pending"
                                               :page/href "https://blog.oddmundo.com/2019/01/27/test-commit-revert-pending.html"}}
                                        ; other people
                     {:page/name "Marc's blog"
                      :page/href "https://brooker.co.za/blog/"}
                     {:page/name "Yossi Kreinin's blog archive"
                      :page/href "https://yosefk.com/blog/"
                      :site/reference-article {:page/title "Advantages of incompetent management"
                                               :page/href "https://yosefk.com/blog/advantages-of-incompetent-management.html"}}
                     {:page/name "danluu.com"
                      :page/href "https://danluu.com"}
                     ])]
       (str "- " (org-link site) " (off-site link)"
            (when (:site/reference-article site)
              (str ". " "Reference article: " (org-link (:site/reference-article site))))))

     (when (seq forever-incomplete)
       [(paragraphs "** Forever incomplete"
                    (lines
                     "Some content is eventually /complete/."
                     "Not journals!")
                    (str/join " --- " (for [page forever-incomplete] (page-link page))))
        ""])

     ["** Norwegian content"
      ""
      "Not everybody speaks Norwegian. But some do!"
      ""]
     ;; (for [page norwegian] (str "- " (page-link page)))
     [(apply lines
             (for [page (reverse (sort-by :created norwegian))]
               (str "- " (page-link-with-date page))))]
     [""]

     [(paragraphs "** Seeds, drafts and vague ideas, feel free to skip."
                  (lines "Or have a peek."
                         "Expect messy, incomplete rambling."
                         "Consider letting me know if you find a title for a text you'd like to read!")
                  (str/join " · " (for [page wtf-is-this] (page-link page))))
      ""]

     ["** Seeds and vague ideas in Norwegian, feel free to skip."
      ""
      (str/join " · " (for [page wtf-is-this-norwegian] (page-link page)))
      ""
      ]

     ["** Remote references"
      ""
      "Pointers to people, places, artifacts, interlaced with some commentary."
      ""
      (str/join " · " (for [page remote-reference] (page-link page)))
      ""
      ]

     (when (seq other)
       ["** Uncategorized\n"
        (str/join  " · " (for [page other] (page-link page)))
        ""])

     (when (seq deprecated)
       ["** Deprecated\n"
        (lines "#+BEGIN_EXPORT html"
               "<div style=\"font-size: 0.75em;\">"
               "#+END_EXPORT")
        (str/join " --- " (for [page deprecated] (page-link page)))
        (lines "#+BEGIN_EXPORT html"
               "</div>"
               "#+END_EXPORT")
        ""])

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

                  "** Then, what is this /for/?"
                  (lines "It's for playing with ideas."
                         "Good ones, but mostly bad ones."
                         "Toss them around. Light them on fire."
                         "Then watch the smoke rise!")

                  "** But what is it???"
                  (lines "Okay, okay."
                         "You're looking for the page neatly called [[https://play.teod.eu/play.teod.eu/][play.teod.eu]]."
                         ":)")

                  (let [blank-lines 5]
                    (str "#+BEGIN_VERSE" "\n"
                         (apply str (repeat blank-lines "\n"))
                         "#+END_VERSE" "\n")))])))

(defn alt
  "Feel free to change this to whatever during dev.

  Functions should be modularized / parameterized to allow for reasonable experience in dev."
  []
  (let [r (->> (pages)
               (filter (fn [page]
                         (= (:form page) :remote-reference)))
               (take 5))]
    (doseq [rr r]
      (prn rr))))

(defn main []
  (spit "index.html" (slurp (:out
                             @(p/process '[pandoc --from org+smart -H header-default-include.html --to html --standalone]
                                         {:in (index-org-markup {:pages (pages)})})))))
