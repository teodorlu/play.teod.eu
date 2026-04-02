(ns dev
  (:require [clj-reload.core]
            main))

(comment ;; s-:

  (clj-reload.core/reload)
  (clj-reload.core/reload :all)

  (main/browse)
  (main/preview)
  (main/stop)

  )
