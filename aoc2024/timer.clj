(ns timer
  (:require
   [duratom.core :refer [duratom]]
   [nextjournal.clerk :as clerk]))

^{::clerk/sync true
  ::clerk/visibility {:result :hide}}
(def !timer
  (duratom :local-file
           :file-path "timer.edn"
           :commit-mode :sync
           :init {}))

(def timer @!timer)

#_(swap! !timer assoc :lol 33)
