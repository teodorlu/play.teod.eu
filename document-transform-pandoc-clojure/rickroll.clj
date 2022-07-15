(ns rickroll
  (:require [clojure.walk]
            [clojure.edn]))

(defn pandoc-link?
  "Is this a valid Pandoc link?"
  [pandoc]
  (= "Link" (:t pandoc)))

(defn pandoc-empty
  "Empty Pandoc element"
  []
  {})

(defn remove-links [pandoc]
  (clojure.walk/prewalk (fn [el]
                          (if (pandoc-link? el)
                            (pandoc-empty) ; empty object is just empty,
                            el))
                        pandoc))

(defn rickroll [pandoc]
  (let [_pandoc-link-example {:t "Link",
                      :c [["" [] []]
                          [{:t "Str", :c "teod.eu"}]
                          ["https://www.youtube.com/watch?v=dQw4w9WgXcQ" ""]]}
        link-to-rick (fn [el]
                    (assoc-in el [:c 2 0] "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))]
    (clojure.walk/prewalk (fn [el]
                            (if (pandoc-link? el)
                              (link-to-rick el)
                              el))
                          pandoc)))

;; (remove-links *input*)
;; pandoc -i doc.md --filter "bash -c \"jet --from json --keywordize | bb rickroll.clj | jet --to json\" -o doc-no-links.md

(def example
  {:pandoc-api-version [1 22 2], :meta {}, :blocks [{:t "Para", :c [{:t "Str", :c "See"} {:t "Space"} {:t "Link", :c [["" [] []] [{:t "Str", :c "teod.eu"}] ["https://teod.eu" ""]]}]}]})

;; If *in* looks like something we can use, use it.

(def input
  (try
    (clojure.edn/read *in*)
    (catch RuntimeException _
        ())))

(def transform rickroll)

(if (map? input)
  (transform input)
  (transform example))

;; Usage:
;;
;;  cat link.json | jet --from json --keywordize | bb rickroll.clj | jet --to json
