(ns tplay.landing
  (:require
   [clojure.pprint]
   [clojure.string :as str]
   [hiccup2.core]
   [tplay.page :as page]))

;; Others on the web

(def others-on-the-web
  [;; people I know to some extent
   {:site/name "Sindre's Random Ramblings"
    :site/href "https://play.sindre.me/"}
   {:site/name "Kevin's WikiBlog"
    :site/href "https://kevin.stravers.net/"
    :site/reference-article {:title "SimpleLinearLists"
                             :href "https://kevin.stravers.net/SimpleLinearLists"}}
   {:site/name "blog.oddmundo.com"
    :site/href "https://blog.oddmundo.com/"
    :site/reference-article {:title "test && commit || revert ; pending"
                             :href "https://blog.oddmundo.com/2019/01/27/test-commit-revert-pending.html"}}
   {:site/name "Christian Johansen - Software developer, musician, amateur baker"
    :site/href "https://cjohansen.no/"}
   {:site/name "magnars"
    :site/href "https://magnars.com/"}

   {:site/name "Mathive Thoughts"
    :site/href "https://mathivethoughts.no/"
    :site/reference-article {:title "Følg dopamin-hitsene"
                             :href "https://mathivethoughts.no/blog-posts/folg-dopamin-hitsene/"}}

   ;; other people
   {:site/name "Marc's blog"
    :site/href "https://brooker.co.za/blog/"}
   {:site/name "Yossi Kreinin's blog archive"
    :site/href "https://yosefk.com/blog/"
    :site/reference-article {:title "Advantages of incompetent management"
                             :href "https://yosefk.com/blog/advantages-of-incompetent-management.html"}}

   {:site/name "danluu.com"
    :site/href "https://danluu.com"}

   {:site/name "Et sted for design og kreativitet (Niklas Håkonsen)"
    :site/href "https://www.niklashakonsen.no/"
    :site/reference-article {:title "Kreativ i tomrommet"
                             :href "https://www.niklashakonsen.no/post/kreativ-i-tomrommet"}}

   {:site/name "macwright.com (Tom MacWright)"
    :site/href "https://macwright.com/"
    :site/reference-article {:title "A clean start for the web"
                             :href "https://macwright.com/2020/08/22/clean-starts-for-the-web"}}


   {:site/name "Wikiblogarden (Lu Wilson)"
    :site/href "https://www.todepond.com/wikiblogarden/"
    :site/reference-article {:title "Just"
                             :href "https://www.todepond.com/wikiblogarden/better-computing/just/"}}

   {:site/name "Jack Rusher: Multidisciplined"
    :site/href "https://jackrusher.com/"
    :site/reference-article {:title "Leopards in the Source Code"
                             :href "https://jackrusher.com/journal/leopards-in-the-source-code.html"}}

   #_=])

;; A tint of color.

(def bright-green "hsl(124, 100%, 88%)")
(def blackish "black")
(def greyish "hsl(108, 5%, 40%);")
(def bright-blue "rgb(109 219 253)")

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

(defn linkroll [theme tplay-root tgo-root]
  [:div {:style {:css.prop/font-size "1.2rem"
                 :css.prop/margin-top "1em"
                 :css.prop/color (:theme/unobtrusive-color theme)}}
   (interpose
    " · "
    (for [{:keys [href text]} [{:href tplay-root
                                :text "play.teod.eu"}
                               {:href tgo-root
                                :text "go.teod.eu"}
                               {:href (str tgo-root "/bretroulette")
                                :text "Bret Roulette"}]]
      [:a {:href href
           :style {:css.prop/color (:theme/unobtrusive-color theme)}}
       text]))])

(defn linkroll-from-tplay [theme]
  (linkroll theme "" "https://go.teod.eu"))

#_(view-links theme-main)

(defn orange-link [target text]
  [:a {:href target :style {:css.prop/color "hsl(20 83.3% 76%)"}} text])

(def one-must-imagine-sisyphus-lol-ing-href
  "https://medium.com/@visakanv/one-must-imagine-sisyphus-lol-ing-565f2bad0340")

(defn principles-banner
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
                        "Rest or focus?" (str "Search for balance."
                                              " Body ↔ Mind ↔ Emotions.")])]
      [:div [:span {:style {:color (:theme/emphasis-color theme )}}
             (str/upper-case principle-core)]
       " " principle-extras])
    (linkroll-from-tplay theme)]])

(defn spaced [& items] (interpose " " items))

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

(defn content-index [subpages]
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
       (for [site (sort-by :site/name others-on-the-web)]
         [:li [:a {:href (:site/href site)} (:site/name site)]
          (when-let [{:keys [href title]}
                     (:site/reference-article site)]
            (list ". Reference article: "
                  [:a {:href href} title]
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
        (map (partial into [:li])
             ["We put things prematurely into large hierarchies that collapse"
              "The inability to critique the hierarchy itself—and iterate on the hierarchy"
              "The inability to have separate hierarchies"
              "More abstractly—we need a /fractal/ structure, not a hierarchical one"
              "Unclear content authority—whose content is this? Who is responsible?"
              "Mixing good stuff and bad stuff makes the good stuff hard to find"
              "It's hard to find any good stuff whatsoever"
              "It's hard to find anything that's not on the front page"
              "It's impossible to build other views than the default"
              "It's impossible to embed things that should not be built at all"])]
       [:p "I include this list as a personal reminder."]]
      [:section
       [:h2 "What is this?"]
       [:p "Good question! What this " [:em "is"] " is perhaps less useful than what this is " [:em "for"] "."]
       [:p "Then, what is this " [:em "for"] "?"]
       [:p (spaced "It's for playing with ideas."
                   "Good ones, but mostly bad ones."
                   "Toss them around. Light them on fire."
                   "Then watch the smoke rise!")]
       [:p "“What this is” is in flux."
        " See " [:a {:href "/play.teod.eu/"} "play.teod.eu"] " for discussion."]]
      [:div {:style {:height "16vh"}}]]]))

(defn index-page [subpages theme]
  (when-not (valid-theme? theme)
    (throw (ex-info "invalid theme" {:theme theme})))
  [:html {:lang "en" :style {:height "88.5%"}}
   [:head
    [:meta {:charset "utf-8"}]
    [:title "Towards an iterated game 🌊"]
    [:title "Towards an iterated game 🌊"]
    [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]

    [:link {:rel "icon" :type "image/x-icon" :href "/green.png"}]
    [:link {:rel "stylesheet" :href "/tplay.css"}]

    [:script {:async true :type "module" :src "/index/big.js"}]
    [:script {:async true :type "module" :src "/iki/iki.js"}]
    [:script {:async true :type "module"}
     (hiccup2.core/raw
      "
import {IkiGotoRandomPage} from \"/iki/iki.js\";
customElements.define(\"iki-goto-random-page\", IkiGotoRandomPage, {extends: \"button\"});
"
      )]
    [:script {:async true :type "module"}
     (hiccup2.core/raw "
  const hostCanonical = 'play.teod.eu';
  const hostAlias1 = 'playground.teod.eu';
  const hostLocal1 = 'localhost:9945';
  if (
    window.location.host === hostAlias1
    // || window.location.host === hostLocal1
  ) {
    // redirect playground.teod.eu to play.teod.eu
    // redirect playground.teod.eu/aphorisms to play.teod.eu/aphorisms
    const pathname = window.location.pathname || '';
    window.location.replace('https://' + hostCanonical + pathname);
  }
")
     ]

    ]
   [:body {:style {:width "100%" :height "100%" :margin 0}}
    (principles-banner theme)
    (content-index subpages)]])

(defn handler [{:keys [the-pages]}]
  (index-page the-pages theme-main))

(comment
  ;; Changing this handler may require regenerating index.html.
  (do
    (require '[babashka.process :as p])
    (p/shell "rm index.html")
    (p/shell "make index.html"))
  )
