(ns teod.play.understand7
  (:require
   [nextjournal.clerk]
   [nextjournal.clerk.viewer :as v]))

(v/with-viewer v/string-viewer "hello")
;; => #:nextjournal{:value "hello",
;;                  :viewer
;;                  {:name nextjournal.clerk.viewer/string-viewer,
;;                   :pred #function[clojure.core/string?--5475],
;;                   :render-fn nextjournal.clerk.render/render-quoted-string,
;;                   :opening-paren "\"",
;;                   :closing-paren "\"",
;;                   :page-size 80}}


{:nextjournal/value "hello"
 :nextjournal/viewer v/string-viewer}
