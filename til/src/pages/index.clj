(ns pages.index)

(defn render []
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "TIL demos"]
    [:style "body { font-family: system-ui, sans-serif; margin: 2rem; line-height: 1.6; }
    ul { padding-left: 1.2rem; }"]]
   [:body
    [:h1 "TIL demos"]
    [:ul
     [:li [:a {:href "command-palette/"} "Filter menu (command palette)"]]
     [:li [:a {:href "popover-minimal/"} "Menu + Popover"]]]]])
