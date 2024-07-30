(ns tplay.landing2
  (:require
   [clojure.string :as str]
   [hiccup2.core :as hiccup]
   [babashka.fs :as fs]))

;; Goal: try replacing the *content* on the landing page with hand-written hiccup and inline styles.

;; Perhaps add a bit of color.


(def bright-green "hsl(124, 100%, 88%)")
(def brighter-green "hsl(122.67 89% 94%)")
(def blackish "black")
(def greyish "hsl(108, 5%, 40%);")
(def bright-blue "rgb(109 219 253)")
(def dark-blue "rgb(0, 91, 119)")
(def crimson "hsl(19, 100%, 44%)")

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

(def theme-other-brighter
  {:theme/primary-color blackish
   :theme/secondary-color brighter-green
   :theme/unobtrusive greyish
   :theme/emphasis dark-blue})

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
  ([theme]
   (principles-component theme {}))
  ([theme opts]
   (assert (valid-theme? theme))
   [:div {:style (merge {:height "100%" :margin 0
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
       [:div [:span {:style {:color (:theme/emphasis theme )}}
              (str/upper-case principle-core)]
        " " principle-extras])]]))

(defn principles-page
  ([title theme]
   (principles-page title theme {}))
  ([title theme opts]
   (assert (valid-theme? theme))
   [:html {:lang "en"
           :style {:height "100%"}}
    [:head
     [:title title]
     [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]
     [:meta {:charset "utf-8"}]]
    [:body {:style {:height "100%"
                    :margin 0
                    :font-size "1.8rem"
                    :padding-left "1rem"
                    :padding-right "1rem"
                    :background-color (:theme/secondary-color theme)
                    :font-family "serif"}}
     [:section {:style (merge {:height "100%"
                               :display :flex
                               :flex-direction :column
                               :gap "2rem"
                               :justify-content :center
                               :line-height "100%"
                               :color (:theme/primary-color theme)}
                              (:section-style/overrides opts section-style-center))}
      (for [[principle-core principle-extras]
            (partition 2 ["Balance." "Body ↔ Mind ↔ Emotions."
                          "Habits for action" "get you started."
                          "Creation & curiosity" "over consumption & passivity."
                          "Techne ≠ episteme." "Not the same thing."
                          "Rest or focus?" (str "Search for balance."
                                                " Body ↔ Mind ↔ Emotions.")])]
        [:div [:span {:style {:color (:theme/emphasis theme )}}
               (str/upper-case principle-core)]
         " " principle-extras])]]]))

;; Setup from automatically building the HTML file when this file (buffer) is
;; evaluated. Set !autobuild to true, then evaluate buffer.

(defonce !autobuild (atom false))

#_(reset! !autobuild true)
#_(deref !autobuild)

(defn build []
  (fs/create-dirs "landing2")
  (spit "landing2/index.html"
        (hiccup/html (hiccup/raw "<!DOCTYPE html>")
          (principles-page "landing 2 🧟"
                           theme-other-brighter)))
  ::build-complete)

(when @!autobuild
  (build))
