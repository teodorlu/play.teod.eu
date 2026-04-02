(ns main
  (:require
   [preview]
   [site]))

(def ^:export browse #(preview/browse! #'site/inject))
(def ^:export preview #(preview/start-server! #'site/inject))
(def ^:export stop #(preview/stop-server!))

(comment
  (browse)
  (preview)
  (stop)
  )
