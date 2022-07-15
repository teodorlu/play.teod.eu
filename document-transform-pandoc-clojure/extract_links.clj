(ns extract-links
  (:require [clojure.walk]
            [clojure.edn]))

(defn link?
  "Is this a valid Pandoc link?"
  [pandoc]
  (= "Link" (:t pandoc)))

(defn link-href [el]
  (when (link? el)
    (get-in el [:c 2 0])))

;; Keeping the old =rickroll= function for comparison.

(defn rickroll [pandoc]
  (let [;; I just copied in an example of what I was going to generate
        _pandoc-link-example {:t "Link",
                      :c [["" [] []]
                          [{:t "Str", :c "teod.eu"}]
                          ["https://www.youtube.com/watch?v=dQw4w9WgXcQ" ""]]}
        ;; which made the assoc-in okay to write
        link-to-rick (fn [el]
                    (assoc-in el [:c 2 0] "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))]
    ;; now, just follow the walk pattern from above.
    (clojure.walk/prewalk (fn [el]
                            (if (link? el)
                              (link-to-rick el)
                              el))
                          pandoc)))

(defn links [pandoc]
  (let [links-found (atom [])]
    (clojure.walk/prewalk (fn [el]
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
