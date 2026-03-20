(ns pages.popover-minimal)

(def css
  "#actions-btn {
      anchor-name: --actions-btn;
    }
    #actions-menu {
      position-anchor: --actions-btn;
      inset: unset;
      top: anchor(bottom);
      left: anchor(left);
      margin: 0;
    }")

(defn render []
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Menu + Popover example"]
    [:style css]]
   [:body
    [:button#actions-btn {:popovertarget "actions-menu"} "Actions ▾"]
    [:menu#actions-menu {:popover true}
     [:li [:button {:onclick "alert('Edit')"} "Edit"]]
     [:li [:button {:onclick "alert('Duplicate')"} "Duplicate"]]
     [:li [:button {:onclick "alert('Delete')"} "Delete"]]]]])
