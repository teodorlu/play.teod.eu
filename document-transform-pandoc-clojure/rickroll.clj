(ns rickroll
  (:require [clojure.walk :refer [prewalk]] ; for recursive transformation
            [clojure.edn]  ; for reading pandoc JSON as EDN
            ))

(comment
  ;; a nice pattern for recursive transformation in Clojure:
  ;;
  ;;   1. walk
  ;;   2. change element if (predicate?)
  ;;   3. otherwise, leave it be.
  ;;
  ;; Example:
  (prewalk (fn [el]
             (if (string? el) ; touch strings
               (keyword el)   ; do this to strings
               el))           ; otherwise let it be
           {:big ["nested" "structure"]}) ; big thing in here
  )

;; Here's the predicate we're going to use later:

(defn pandoc-link?
  "Is this a valid Pandoc link?"
  [pandoc]
  (= "Link" (:t pandoc)))

;; I choose to pull "this is an empty element" out of the walk logic

(defn pandoc-empty
  "Empty Pandoc element"
  []
  {})

;; What's the simplest link transform we could do?
;; Removing links is easy.
;; So let's try that first.
;;
;; For more info on small steps from Gewpaw Hill:
;;
;;   https://www.geepawhill.org/2021/09/29/many-more-much-smaller-steps-first-sketch/

(defn remove-links [pandoc]
  (prewalk (fn [el]
             (if (pandoc-link? el)
               (pandoc-empty) ; empty object is just empty,
               el))
           pandoc))

;; That worked!
;;
;; Set `transform` to `remove-links` below to try.

;; Here's the rickroll:

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
    (prewalk (fn [el]
               (if (pandoc-link? el)
                 (link-to-rick el)
                 el))
             pandoc)))

;; I first tried running it all at once:
;;
;;   pandoc -i doc.md --filter "bash -c \"jet --from json --keywordize | bb rickroll.clj | jet --to json\" -o doc-no-links.md
;;
;; But it turns out, pandoc doesn't support this.
;; A filter must be a single script.
;; So the thing above doesn't work.
;; Back to this later.

;; I hard-code some example data so that "just running" gives me feedback

(def example
  {:pandoc-api-version [1 22 2], :meta {},
   :blocks [{:t "Para", :c [{:t "Str", :c "See"}
                            {:t "Space"}
                            {:t "Link",
                             :c [["" [] []]
                                 [{:t "Str", :c "teod.eu"}]
                                 ["https://teod.eu" ""]]}]}]})

;; ... but if *in* looks like something we can use, use that instead.

(def input
  (try
    (clojure.edn/read *in*)
    (catch RuntimeException _
        ())))

(let [transform rickroll] ; I wanted a single place to choose what happens -- remove links or rickroll
  (if (map? input)
    (transform input)
    (transform example)))

;; Here's how you can run just this script --- no pandoc yet:
;;
;;   cat link.json \
;;       | jet --from json --keywordize \
;;       | bb rickroll.clj \
;;       | jet --to json --keywordize
;;
