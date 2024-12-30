(ns tplay.nopandoc.landing
  (:require
   [clojure.pprint]
   [clojure.string :as str]
   [hiccup2.core]
   [tplay.nopandoc.other-people :as other-people]
   [tplay.page :as page]))

;; A tint of color.

(def bright-green "hsl(124, 100%, 88%)")
(def blackish "black")
(def greyish "hsl(108, 5%, 40%);")
(def bright-blue "rgb(109 219 253)")

(defn valid-theme? [theme]
  (every? #(contains? theme %)
          [:theme/primary-color
           :theme/secondary-color
           :theme/unobtrusive
           :theme/emphasis]))

(def theme-main
  {:theme/primary-color bright-green
   :theme/secondary-color blackish
   :theme/unobtrusive greyish
   :theme/emphasis bright-blue})

(defn principles-component
  [theme]
  (assert (valid-theme? theme))
  [:div {:style {:height "100%"
                 :margin 0
                 :font-size "1.8rem"
                 :padding-left "1rem"
                 :padding-right "1rem"
                 :background-color (:theme/secondary-color theme)
                 :font-family "serif"}}
   [:section {:style {:height "100%"
                      :display :flex
                      :flex-direction :column
                      :gap "2rem"
                      :justify-content :center
                      :line-height "100%"
                      :color (:theme/primary-color theme)}}
    (for [[principle-core principle-extras]
          (partition 2 ["Balance." "Body ↔ Mind ↔ Emotions."
                        "Habits for action" "get you started."
                        "Creation & curiosity" "over consumption & passivity."
                        "Techne ≠ episteme." "Not the same thing."
                        "Rest or focus?" (str "Search for balance."
                                              " Body ↔ Mind ↔ Emotions.")])]
      [:div [:span {:style {:color (:theme/emphasis theme )}}
             (str/upper-case principle-core)]
       " " principle-extras])]])

(defn spaced [& items] (interpose " " items))

(def todo [:p {:style {:opacity "0.67"}} [:em "TODO"]])

(defn pprint-str [x]
  (with-out-str (clojure.pprint/pprint x)))

(defn todo-convert [code]
  [:pre [:code (pprint-str code)]])

(defn category [{:keys [lang readiness form]}]
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

(defn page-link [page]
  [:a {:href (page/link page)}
   (page/title page)])

(defn norwegian-page-link [page]
  (list [:a {:href (page/link page)}
         (page/title page)]
        (when (:created page)
          (str " (" (:created page) ")"))))

(defn pandoc-component [subpages]
  (let [by-category (group-by category subpages)]
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
      [:p "hello"]
      (when-let [ready-for-comments (:page-category/ready-for-comments by-category)]
        (list
         [:h2 "Content that's ready for the eyes of others"]
         (when-let [timeless (->> ready-for-comments
                                  (remove :created)
                                  (sort-by :title))]
           (list
            [:p "Things I believe:"]
            [:ul
             (for [p timeless]
               [:li [:a {:href (page/link p)}
                     (page/title p)]])]))
         (when-let [timestamped (->> ready-for-comments
                                     (filter page/published-or-created)
                                     (sort-by page/published-or-created)
                                     reverse)]
           (list
            [:p "Things I've written:"]
            [:ul
             (for [p timestamped]
               (list
                [:li [:a {:href (page/link p)}
                      (page/title p)]
                 " (" (page/published-or-created p) ")"]))]))))
      [:h2 "Other people's sites"]
      [:ul
       (for [site (sort-by :site/name other-people/sites)]
         [:li [:a {:href (:site/href site)} (:site/name site)]
          (when-let [{:as r :keys [href title]}
                     (:site/reference-article site)]
            (list ". Reference article: "
                  [:a {:href href} title (:title r)]
                  " (off-site link)."))])]
      (when-let [incomplete (:page-category/forever-incomplete by-category)]
        [:section
         [:h2 "Forever incomplete"]
         [:p "Some content is eventually " [:em "complete"] ". Not journals!"]
         [:p (interpose " — " (map page-link incomplete))]])
      (when-let [norwegian (:page-category/norwegian by-category)]
        [:section
         [:h2 "Norwegian content"]
         [:ul
          (for [page (reverse (sort-by :created norwegian))]
            [:li (norwegian-page-link page)])]])
      (when-let [seeds (:page-category/wtf-is-this by-category)]
        [:section
         [:h2 "Seeds, drafts and vague ideas, feel free to skip."]
         [:p "Or have a peek. Expect incomplete, ill-formed thoughts."]
         [:p (interpose " · " (map page-link seeds))]])
      (when-let [norwegian-seeds (:page-category/wtf-is-this-norwegian by-category)]
        [:section
         [:h2 "Seeds and vague ideas in Norwegian, feel free to skip."]
         [:p (interpose " · " (map page-link norwegian-seeds))]])
      (when-let [references (:page-category/remote-reference by-category)]
        [:section
         [:h2 "Remote references"]
         [:p  "Pointers to people, places and artifacts, sometimes interlaced with commentary."]
         [:p (interpose " · " (map page-link references))]])
      (when-let [uncategorized (:other by-category)]
        [:section
         [:h2 "Uncategorized"]
         [:p (interpose " · " (map page-link uncategorized))]])
      (when-let [deprecated (:page-category/deprecated by-category)]
        [:section
         [:h2 "Deprecated"]
         [:p (interpose " · " (map page-link deprecated))]])
      [:section
       [:h2 "Efforts at “writing things down together” commonly fail because:"]
       [:ol
        (map #(into [:li %])
             ["We put things prematurely into large hierarchies that collapse"
              "The inability to critique the hierarchy itself --- and iterate on the hierarchy"
              "The inability to have separate hierarchies"
              "More abstractly --- we need a /fractal/ structure, not a hierarchical one"
              "Unclear content authority --- whose content is this? Who is responsible?"
              "Mixing good stuff and bad stuff makes the good stuff hard to find"
              "It's hard to find any good stuff whatsoever"
              "It's hard to find anything that's not on the front page"
              "It's impossible to build other views than the default"
              "It's impossible to embed things that should not be built at all"])]
       [:p "I include this list as a personal reminder."]]
      [:section
       [:h2 "What is this?"]
       [:p "Good question! What this /is/ is perhaps less useful than what this is " [:em "for"] "."]]
      [:section
       [:h2 "Then, what is this " [:em "for"] "?"]
       [:p (spaced "It's for playing with ideas."
                   "Good ones, but mostly bad ones."
                   "Toss them around. Light them on fire."
                   "Then watch the smoke rise!")]]
      [:section
       [:h2 "But what is it???"]
       [:p "You're looking for the page describing this site, "
        [:a {:href "/play.teod.eu/"}
         "play.teod.eu"] "."]]
      [:div {:style {:height "16vh"}}]]]))

(def index-headers
  (list
   [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
   [:meta {:charset "utf-8"}]

   [:link {:rel "icon" :type "image/x-icon" :href "/green.png"}]
   [:script {:async true :type "module" :src "/index/big.js"}]
   [:script {:async true :type "module" :src "/iki/iki.js"}]
   [:script {:async true :type "module"}
    (hiccup2.core/raw
     "
import {IkiGotoRandomPage} from \"/iki/iki.js\";
customElements.define(\"iki-goto-random-page\", IkiGotoRandomPage, {extends: \"button\"});
")]
   ))

(defn index-page [subpages theme]
  (when-not (valid-theme? theme)
    (throw (ex-info "invalid theme" {:theme theme})))
  [:html {:lang "en" :style {:height "100%"}}
   [:head
    [:title "Towards an iterated game 🌊"]
    index-headers]
   [:body {:style {:width "100%" :height "100%" :margin 0}}
    (principles-component theme)
    (pandoc-component subpages)]])

(defn handler [{:keys [the-pages]}]
  (index-page the-pages theme-main))
