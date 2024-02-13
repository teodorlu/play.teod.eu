;; # I don't understand my own code 😂
;;
;; Let's fix that.

(ns teod.play.understand
  (:require
   [teod.play.cli :as cli]
   [teod.play.api :as api]
   [eu.teod.clerk-utils :as hammertime]))

;; Can I query for the relations from code?

(hammertime/doc cli/cmd-relations)

;; That did _not help_.
