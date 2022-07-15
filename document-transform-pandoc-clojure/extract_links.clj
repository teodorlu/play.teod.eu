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

;; Keep the old `rickroll` function for reference:
(defn rickroll [pandoc]
  (let [rick-link (fn [el]
                    (assoc-in el [:c 2 0]
                              "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))]
    (prewalk (fn [el]
               (if (link? el)
                 (rick-link el)
                 el))
             pandoc)))

(defn links [pandoc]
  (let [links-found (atom [])]
    (prewalk (fn [el]
               (if (link? el)
                 (do (swap! links-found conj
                            {:href (link-href el)}) el)
                 el))
             pandoc)
    @links-found))

(def example
  {:pandoc-api-version [1 22 2], :meta {},
   :blocks [{:t "Para", :c [{:t "Str", :c "See"}
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
