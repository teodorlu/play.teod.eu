(ns pages.table-in-table-2)

(defn render []
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:title "Table in table 2 — rowspan with season"]]
   [:body
    [:h1 "Table in table 2 — sesongåpent"]
    [:table
     [:tr
      [:th {:style {:vertical-align "top"}} "Beliggenhet"]
      [:td {:colspan "2"} "Maridalsveien 154" [:br] "0461 Oslo"]]
     [:tr
      [:th {:rowspan "5" :style {:vertical-align "top"}} "Sesongåpent"]
      [:th {:colspan "2"} "1. september – 31. mars"]]
     [:tr [:td "Man–tors"] [:td "10:00–16:30"]]
     [:tr [:td "Fredag"]    [:td "09:30–19:30"]]
     [:tr [:td "Lørdag"]    [:td "11:00–21:00"]]
     [:tr [:td "Søndag"]    [:td "10:00–17:00"]]
     [:tr
      [:th {:style {:vertical-align "top"}} "Telefon"]
      [:td {:colspan "2"} "22 83 20 00"]]]]])
