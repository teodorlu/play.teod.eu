(ns eu.teod.pandoc-edn.alpha1
  (:require [babashka.process]
            [cheshire.core :as json]
            [eu.teod.pandoc-edn.alpha1 :as pandoc]))

;; design goals:
;;
;;  - support babashka and JVM clojure
;;  - encourage treating pandoc JSON as a narrow waist for documents
;;
;; non-goals (for now):
;;
;;  - wrap all of pandoc's API interface
;;
;; stuff I'm uncertain about:
;;
;;  - I want to handle errors better. Not sure how to go about that.
;;  - Not sure how to do "library stuff" - publishing, etc.

(defn ^:private process-sync [cmd opts]
  (slurp (:out (babashka.process/process cmd opts))))

(defn from-string [s opts]
  (let [supported? #{:markdown :markdown+smart :org :org+smart :html} ; whitelist for now
        format (:format opts)]
    (assert (supported? format))
    (json/parse-string (process-sync ["pandoc" "--from" (name format) "--to" "json"]
                                     {:in s})
                       keyword)))

(defn to-string [pandoc opts]
  (let [supported? #{:markdown :markdown+smart :org :org+smart :html} ; whitelist for now
        format (:format opts)]
    (assert (supported? format))
    (process-sync ["pandoc" "--from" "json" "--to" (name format)]
                  {:in (json/generate-string pandoc)})))

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

  )
