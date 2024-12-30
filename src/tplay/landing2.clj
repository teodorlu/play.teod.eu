(ns tplay.landing2
  (:require
   [babashka.fs :as fs]
   [clojure.pprint]
   [clojure.string :as str]
   [hiccup2.core :as hiccup]
   [tplay.index]
   [tplay.landing2-assets :as assets]
   [tplay.page :as page]))

;; Goal: try replacing the *content* on the landing page with hand-written hiccup and inline styles.

;; A tint of color.

(def bright-green "hsl(124, 100%, 88%)")
(def brighter-green "hsl(122.67 89% 94%)")
(def blackish "black")
(def greyish "hsl(108, 5%, 40%);")
(def bright-blue "rgb(109 219 253)")
(def dark-blue "rgb(0, 91, 119)")

(defn valid-theme? [theme]
  (every? #(contains? theme %)
          [:theme/primary-color
           :theme/secondary-color
           :theme/unobtrusive-color
           :theme/emphasis-color]))

(def theme-main
  {:theme/primary-color bright-green
   :theme/secondary-color blackish
   :theme/unobtrusive-color greyish
   :theme/emphasis-color bright-blue})

(def theme-other-brighter
  {:theme/primary-color blackish
   :theme/secondary-color brighter-green
   :theme/unobtrusive-color greyish
   :theme/emphasis-color dark-blue})

(assert (every? valid-theme? [theme-main theme-other-brighter]))

(def section-style-center
  "Center text in sections"
  {:text-align :center :justify-content :center})
(def section-style-left-adjust
  "Left adjust text in sections"
  {})
(def section-style-paragraph-indented-text
  "Left adjust text in sections, indenting text after first line"
  {:text-indent "3em hanging"})

(defn principles-component
  "Display some principles worthy of being top front."
  ([theme]
   (principles-component theme {}))
  ([theme opts]
   (assert (valid-theme? theme))
   [:div {:style (merge {:height "100%"
                         :margin 0
                         :font-size "1.8rem"
                         :padding-left "1rem"
                         :padding-right "1rem"
                         :background-color (:theme/secondary-color theme)
                         :font-family "serif"}
                        (:container-style/overrides opts))}
    [:section {:style (merge {:height "100%"
                              :display :flex
                              :flex-direction :column
                              :gap "2rem"
                              :justify-content :center
                              :line-height "100%"
                              :color (:theme/primary-color theme)}
                             (:section-style/overrides opts section-style-left-adjust))}
     (for [[principle-core principle-extras]
           (partition 2 ["Balance." "Body ↔ Mind ↔ Emotions."
                         "Habits for action" "get you started."
                         "Creation & curiosity" "over consumption & passivity."
                         "Techne ≠ episteme." "Not the same thing."
                         "Rest or focus?" (str "Search for balance."
                                               " Body ↔ Mind ↔ Emotions.")])]
       [:div [:span {:style {:color (:theme/emphasis-color theme )}}
              (str/upper-case principle-core)]
        " " principle-extras])]]))

(defn spaced
  "Put a space (\" \") betwee the items"
  [& items]
  (interpose " " items))

(def todo [:p {:style {:opacity "0.67"}} [:em "TODO"]])

(defn pprn [x]
  (with-out-str
    (clojure.pprint/pprint x)))

(defn category2 [{:keys [lang readiness form] :as page}]
  (cond
    (= readiness :noindex)                        :page-category/noindex
    (= readiness :deprecated)                     :page-category/deprecated
    (= form :remote-reference)                    :page-category/remote-reference
    (and (= readiness :wtf-is-this) (= lang :no)) :page-category/wtf-is-this-norwegian
    (= lang :no)                                  :page-category/norwegian
    (= readiness :ready-for-comments)             :page-category/ready-for-comments
    (= readiness :wtf-is-this)                    :page-category/wtf-is-this
    (= readiness :forever-incomplete)             :page-category/forever-incomplete
    :else :other))

(defn slug [page]
  (or (:page/slug page)
      (:id page)))

(defn pandoc-component [subpages]
  (let [subpages-by-category (group-by category2 subpages)]
    [:div.pandoc-vertical
     [:div.pandoc-indented
      [:header#title-block-header
       [:h1.title "Towards an iterated game 🩵"]]
      [:button {:is :iki-goto-random-page
                :style {:display :block
                        :font-size "16px"
                        :margin "0px auto"}}
       "Go to random page"]
      [:div {:style {:height "2rem"}}]
      [:p (spaced "Intent: bring ideas to life."
                  "Discuss, sharpen, play."
                  "Minimize distance between intent and reality.")]
      [:p (spaced "Process: aim intent towards curiosity—explore—refactor towards orthogonality."
                  "Embrace remix culture."
                  "Legibility is a partially provided affordance, not a design constraint.")]
      [:p (spaced "Status: work in progress, plenty of rough edges."
                  "But you're" [:emph "very"] "much welcome to have a look around!")]
      [:p (spaced "Most content on this site is authored by Teodor Heggelund"
                  (list "(" [:a {:href "https://teod.eu"} "https://teod.eu"] ")"))]
      (when-let [ready-for-comments (:page-category/ready-for-comments subpages-by-category)]
        (list
         [:h2 "Content that's ready for the eyes of others"]
         (when-let [timeless (seq (->> ready-for-comments
                                       (remove :created)
                                       (sort-by :title)))]
           (list
            [:p "Things I believe:"]
            [:ul
             (for [p timeless]
               [:li [:a {:href (page/link p)}
                     (page/title p)]])]))
         (when-let [timestamped (seq (->> ready-for-comments
                                          (filter page/published-or-created)
                                          (sort-by page/published-or-created)
                                          reverse))]
           (list
            [:p "Things I've written:"]
            [:ul
             (for [p timestamped]
               (list
                [:li [:a {:href (page/link p)}
                      (page/title p)]
                 " (" (page/published-or-created p) ")"]

                )
               )]

            #_
            (map #(vector :pre (pprn %)) timestamped)))))
      [:h2 "Other people's sites"]
      todo
      [:h2 "Forever incomplete"]
      todo
      [:h2 "Norwegian content"]
      todo
      [:h2 "Seeds, drafts and vague ideas, feel free to skip."]
      todo
      [:h2 "Seeds, drafts and vague ideas in Norwegian, feel free to skip."]
      todo
      [:h2 "Remote references"]
      todo
      [:h2 "Uncategorized"]
      todo
      [:h2 "Deprecated"]
      todo
      [:h2 "Efforts at “writing things down together” commonly fail because:"]
      todo
      [:h2 "What is this?"]
      todo
      [:h2 "Then, what is this " [:em "for"] "?"]
      todo
      [:h2 "But what is it???"]
      todo
      [:div {:style {:height "96px"}}]]]))

#_ (index-page [] theme-main)
#_ (index-page theme-main)

(defn index-page
  ([subpages theme]
   (index-page subpages theme {}))
  ([subpages theme opts]
   (assert (valid-theme? theme))
   [:html {:lang "en"
           :style {:height "100%"}}
    [:head
     [:title "landing 🌊"]
     [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
     [:meta {:charset "utf-8"}]
     assets/favicon-link
     assets/pandoc-css-link]
    [:body {:style {:width "100%"
                    :height "100%"
                    :margin 0}}
     (principles-component theme opts)
     (pandoc-component subpages)]]))

;; Setup from automatically building the HTML file when this file (buffer) is
;; evaluated. Set !autobuild to true, then evaluate buffer.

(defonce !autobuild (atom false))

#_(reset! !autobuild true)
#_(deref !autobuild)

(defn build []
  (fs/create-dirs "landing2")
  (spit "landing2/index.html"
        (hiccup/html {}
          (hiccup/raw "<!DOCTYPE html>")
          (index-page (tplay.index/find-pages) theme-main)))
  ::build-complete)

(when @!autobuild
  (build))
