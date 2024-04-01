(ns render-currmap
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [hiccup2.core :as hiccup]))

(def currmap-ds (edn/read-string (slurp "currmap.edn")))

(defn clojure-list->hiccup-list [items]
  (into [:ul]
        (for [el items]
          [:li el])))

(do
  (defn currmap->hiccup-table [currmap]
    [:html
     [:head
      [:meta {:charset "utf-8"}]]
     [:body
      [:table
       [:thead
        [:tr
         [:td #_ "empty table corner"]
         [:th [:strong "Fundamentals"]]
         [:th [:strong "Core"]]
         [:th [:strong "Advanced"]]]
        [:tr
         [:td #_ "empty table corner"]
         [:td {:style {:text-align "center"}} "(start here)"]
         [:td {:style {:text-align "center"}} "(get to here)"]
         [:td {:style {:text-align "center"}} "(don't worry about it)"]]]
       [:tbody
        (for [entry (:entries currmap)]
          [:tr
           [:td (str/join " / " (:path entry))]
           [:td (clojure-list->hiccup-list (:fundamentals entry))]
           [:td (clojure-list->hiccup-list (:core entry))]
           [:td (clojure-list->hiccup-list (:advanced entry))]])]]]])

  (spit "currmap.html" (hiccup/html (currmap->hiccup-table currmap-ds))))
