(ns rickroll
  (:require
   [clojure.walk :refer [prewalk]] ; recursive transformation
   [clojure.edn]  ; read pandoc JSON as EDN
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
           {:big ["nested" "structure"]}) ; big thing
  )

;; We must identify pandoc JSON links
(defn pandoc-link?
  "Is this a valid Pandoc link?"
  [pandoc]
  (= "Link" (:t pandoc)))

;; What's the simplest link transform we could do?
;; Removing links is easy.
;;
;; For the interested reader, Geepaw Hill provides some
;; great commentary on you should take small steps.
;;
;;   https://www.geepawhill.org/2021/09/29/many-more-much-smaller-steps-first-sketch/
;;
;; But I digress. Back to our totally serious project.
;; Let's remove some links.

(defn remove-links [pandoc]
  (prewalk (fn [el]
             (if (pandoc-link? el)
               {} ; emtpy element
               el))
           pandoc))

;; To try, set `transform` to `remove-links` below :)

;; Finally, here's a rickroll pandoc filter:
(defn rickroll [pandoc]
  (let [;; I like to see an example of the data
        ;; structure I'm working with
        _link-example {:t "Link",
                       :c [["" [] []]
                           [{:t "Str", :c "teod.eu"}]
                           ["https://www.youtube.com/watch?v=dQw4w9WgXcQ" ""]]}
        ;; which made the assoc-in okay to write:
        rick-link (fn [el]
                    (assoc-in el [:c 2 0]
                              "https://www.youtube.com/watch?v=dQw4w9WgXcQ"))]
    ;; now, same (if predicate change no-change) pattern
    (prewalk (fn [el]
               (if (pandoc-link? el)
                 (rick-link el)
                 el))
             pandoc)))

;; I first tried running it all at once:
;;
;;   pandoc -i doc.md --filter "bash -c \"jet --from json --keywordize | bb rickroll.clj | jet --to json\" -o doc-no-links.md
;;
;; But it turns out, pandoc doesn't support this.
;; A filter must be a single script.
;; Filters can't take arguments.
;; So we need a wrapper.
;; More on the wrapper later.

;; I hard-code some example data so that "just running" gives me feedback:
(def example
  {:pandoc-api-version [1 22 2], :meta {},
   :blocks [{:t "Para",
             :c [{:t "Str", :c "See"}
                 {:t "Space"}
                 {:t "Link",
                  :c [["" [] []]
                      [{:t "Str", :c "teod.eu"}]
                      ["https://teod.eu" ""]]}]}]})

;; ... but if *in* looks right, use that.
(def input
  (try
    (clojure.edn/read *in*)
    (catch RuntimeException _
        ())))

(let [transform rickroll] ; choose rickroll or remove-links here
  (if (map? input)
    (transform input)
    (transform example)))

;; How to run without pandoc:
;;
;;   cat link.json \
;;       | jet --from json --keywordize \
;;       | bb rickroll.clj \
;;       | jet --to json --keywordize
;;
