(ns tplay.terran
  "I choose to inhabit this world."
  (:require [terra.assetwatch]
            [terra.instance]))

(defonce watch-once
  (terra.assetwatch/watch! "."))

(defonce !last-req (atom nil))

(defn handler [req]
  (reset! !last-req req)
  (terra.assetwatch/handler req))

(comment

  terra.assetwatch/!last-response

  (terra.instance/start! #'handler {:port 7777})
  (terra.instance/browse!)

  )
