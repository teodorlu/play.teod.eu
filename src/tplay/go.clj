(ns tplay.go
  (:require
   [clojure.string :as str]))

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

(def theme-other
  {:theme/primary-color blackish
   :theme/secondary-color bright-green
   :theme/unobtrusive greyish
   :theme/emphasis crimson})

(def theme-other-crimson
  {:theme/primary-color blackish
   :theme/secondary-color brighter-green
   :theme/unobtrusive greyish
   :theme/emphasis crimson})

(def theme-other-brighter
  {:theme/primary-color blackish
   :theme/secondary-color brighter-green
   :theme/unobtrusive greyish
   :theme/emphasis dark-blue})

(def theme-bw
  {:theme/primary-color "rgba(0, 0, 0, 80%)"
   :theme/secondary-color "rgba(0, 0, 0, 0%)"
   :theme/unobtrusive "rgba(0, 0, 0, 60%)"
   :theme/emphasis "rgba(0, 0, 0, 100%)"})

(assert (every? valid-theme? [theme-main theme-other theme-other-crimson]))

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
                             (:section-style/overrides opts section-style-center))}
     (for [[principle-core principle-extras]
           (partition 2 ["Balance." "Body ↔ Mind ↔ Emotions."
                         "Habits for action" "get you started."
                         "Creation & curiosity" "over consumption & passivity."
                         #_#_"Techne ≠ episteme." "Not the same thing."
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
     [:meta {:name "viewport" :content "width=device-width,initial-scale=1"}]]
    [:body {:style {:height "100%" :margin 0
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
                          #_#_"Techne ≠ episteme." "Not the same thing."
                          "Rest or focus?" (str "Search for balance."
                                                " Body ↔ Mind ↔ Emotions.")])]
        [:div [:span {:style {:color (:theme/emphasis theme )}}
               (str/upper-case principle-core)]
         " " principle-extras])]]]))

(defn page
  ([req title-suffix theme] (page req title-suffix theme {}))
  ([req title-suffix theme opts]
   (principles-page (str (get {"localhost" "🩵"} (:server-name req) "🌊 🌊 🌊")
                         title-suffix)
                    theme
                    opts)))
