(ns extract-links
  (:require [clojure.walk :refer [prewalk]]
            [clojure.edn]))

(defn link?
  "Is this a valid Pandoc link?"
  [pandoc]
  (= "Link" (:t pandoc)))

(defn link-href [el]
  (when (link? el)
    (get-in el [:c 2 0])))

(defn links [pandoc]
  (let [links-found (atom [])]
    (prewalk (fn [el]
               (if (link? el)
                 (do (swap! links-found conj
                            {:href (link-href el)})
                     el)
                 el))
             pandoc)
    @links-found))

(def example
  {:pandoc-api-version [1 22 2], :meta {},
   :blocks [{:t "Para",
             :c [{:t "Str", :c "See"}
                 {:t "Space"}
                 {:t "Link",
                  :c [["" [] []]
                      [{:t "Str", :c "teod.eu"}]
                      ["https://teod.eu" ""]]}]}]})


(def input
  (try
    (clojure.edn/read *in*)
    (catch RuntimeException _
        ())))

(if (map? input)
  (links input)
  (links example))
