(require '[clojure.walk])

(defn link? [pandoc]
  (= "Link" (:t pandoc)))

(defn remove-links [pandoc]
  (clojure.walk/prewalk (fn [el]
                          (if (link? el)
                            nil
                            el))
                        (pandoc)))

(remove-links *input*)

;; pandoc -i doc.md --filter "bash -c \"jet --from json | bb rickroll.clj | jet --to json\" -o doc-no-links.md
