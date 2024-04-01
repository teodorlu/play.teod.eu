(ns render-currmap
  (:require
   [clojure.edn :as edn]
   [hiccup2.core :as hiccup]))

(def currmap-ds (edn/read-string (slurp "currmap.edn")))

(do
  (defn currmap->hiccup-table [currmap]
    [:html
     [:head
      [:meta {:charset "utf-8"}]]
     [:body
      [:table
       [:thead [:th "hei"]]
       [:tbody [:tr [:td "på deg"]]]]]])

  (spit "currmap.html" (hiccup/html (currmap->hiccup-table currmap-ds))))
