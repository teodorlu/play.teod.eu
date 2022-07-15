(require '[clojure.walk])

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

;; (remove-links *input*)
;; pandoc -i doc.md --filter "bash -c \"jet --from json --keywordize | bb rickroll.clj | jet --to json\" -o doc-no-links.md

(def example
  {:pandoc-api-version [1 22 2], :meta {}, :blocks [{:t "Para", :c [{:t "Str", :c "See"} {:t "Space"} {:t "Link", :c [["" [] []] [{:t "Str", :c "teod.eu"}] ["https://teod.eu" ""]]}]}]})

(prn (remove-links example))
