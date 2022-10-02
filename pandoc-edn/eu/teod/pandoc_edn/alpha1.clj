(ns eu.teod.pandoc-edn.alpha1
  (:require [babashka.process]
            [cheshire.core :as json]))

(defn ^:private process-sync [cmd opts]
  (slurp (:out (babashka.process/process cmd opts))))

(defn from-string [s opts]
  (let [supported #{:markdown :markdown+smart :org :org+smart}
        format (:format opts)]
    (assert (supported format))
    (json/parse-string (process-sync ["pandoc" "--from" (name format) "--to" "json"]
                                     {:in s})
                       keyword)))

(defn to-string [pandoc opts])

(comment
  ;; example invocations
  (require '[eu.teod.pandoc-edn.alpha1 :as pandoc])

  (process-sync ["pandoc" "--from" "markdown" "--to" "json"] {:in ""})

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

  (str :markdown)


  )
