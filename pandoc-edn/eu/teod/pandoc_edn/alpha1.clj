(ns eu.teod.pandoc-edn.alpha1
  (:require [babashka.process]
            [cheshire.core :as json]
            [clojure.walk]))

;; design goals:
;;
;;  - support babashka and JVM clojure
;;  - encourage treating pandoc JSON as a narrow waist for documents
;;
;; non-goals (for now):
;;
;;  - wrap all of pandoc's CLI arguments.
;;    Pandoc supports /a lot/. There might be multiple ways to achieve the same thing.
;;    I don't want multiple ways to achieve the same thing.
;;    I want a nice, narrow data driven interface.
;;
;; stuff I'm uncertain about:
;;
;;  - I want to handle errors better. Not sure how to go about that.
;;  - Not sure how to do "library stuff" - publishing, etc.
;;  - How to balance stuff people might want with me wanting to take this slow
;;
;; todolist:
;;
;;  - [ ] Who do I want feedback from? I'd like some mentorship.
;;  - [x] Provide an idiomatic "walk" method.

(defn ^:private process-sync [cmd opts]
  (slurp (:out (babashka.process/process cmd opts))))

(defn from-string [s opts]
  (let [supported? #{:markdown :markdown+smart :org :org+smart :html :plain} ; whitelist for now
        format (:format opts)]
    (assert (supported? format))
    (json/parse-string (process-sync ["pandoc" "--from" (name format) "--to" "json"]
                                     {:in s})
                       keyword)))

(defn to-string [pandoc opts]
  (let [supported? #{:markdown :markdown+smart :org :org+smart :html :plain} ; whitelist for now
        format (:format opts)]
    (assert (supported? format))
    (process-sync ["pandoc" "--from" "json" "--to" (name format)]
                  {:in (json/generate-string pandoc)})))

(defn prewalk
  [pandoc filter-fn]
  ;; uncertain about argument order.
  ;;
  ;;  1. In this ns, I like to have PANDOC as first arg.
  ;;  2. In clojure.walk, FORM is second arg.
  (update pandoc :blocks
          (fn [blocks]
            (clojure.walk/prewalk filter-fn blocks))))

(defn postwalk
  [pandoc filter-fn]
  (update pandoc :blocks
          (fn [blocks]
            (clojure.walk/postwalk filter-fn blocks))))

(comment
  ;; example invocations

  ;; this ns is written to be aliased as "pandoc"
  (require '[eu.teod.pandoc-edn.alpha1 :as pandoc])

  (pandoc/from-string "# My heading" {:format :markdown})
  ;; returns
  {:pandoc-api-version [1 22 2 1],
   :meta {},
   :blocks
   [{:t "Header",
     :c
     [1
      ["my-heading" [] []]
      [{:t "Str", :c "My"} {:t "Space"} {:t "Str", :c "heading"}]]}]}

  (pandoc/from-string "<div>lol</div>" {:format :html})

  (-> "<div>lol</div>"
      (pandoc/from-string {:format :html})
      (pandoc/to-string {:format :html}))

  (-> "# Heading

Does this look nice?"
      (pandoc/from-string {:format :markdown})
      (pandoc/to-string {:format :html}))

(-> "# pandoc, just awesome

Does this look nice?"
      (pandoc/from-string {:format :markdown})
      :blocks
      first)

  (-> "# pandoc, just awesome

Does this look nice?"
      (pandoc/from-string {:format :markdown})
      :blocks
      first
      :c
      (get 2))

  (let [header? (fn [x]
                  (= (:t x)
                     "Header"))]
    (-> "# pandoc, just awesome

Does this look nice?"
        (pandoc/from-string {:format :markdown})
        (pandoc/prewalk (fn [x]
                          (if (header? x)
                            (update-in x [:c 2] (constantly [{:t "Str", :c "Pandoc is flexible."}]))
                            x)))
        (pandoc/to-string {:format :markdown}))

    )

  (-> "ye say \"smart quotes\", ye do?"
      (pandoc/from-string {:format :org+smart})
      (pandoc/to-string {:format :plain}))
  ;; =>
  "ye say “smart quotes”, ye do?\n"



  )
