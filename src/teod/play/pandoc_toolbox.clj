(ns teod.play.pandoc-toolbox
  (:require [clojure.walk]))

;; teod/pandoc-toolbox lets you work with Pandoc JSON as plain Clojure data.
;;
;; The JSON is exported directly from upstream Haskell types with the Aeson JSON
;; library. That means the closest we come to an official API spec for Pandoc
;; JSON is probably the Haskell types:
;;
;;    https://hackage.haskell.org/package/pandoc-types-1.22.2.1/docs/Text-Pandoc-Definition.html
;;
;; But it's just data, really. Here's an example in pandoc JSON:

{"pandoc-api-version" [1 22 2],
 "meta" {"title" {"t" "MetaInlines", "c" [{"t" "Str", "c" "Hello"}]}},
 "blocks" [{"t" "Para", "c" [{"t" "Str", "c" "Link:"}]}
           {"t" "Para",
            "c" [{"t" "Link",
                  "c" [["" [] []]
                       [{"t" "Str", "c" "Open"}
                        {"t" "Space"}
                        {"t" "Str", "c" "problems"}]
                       ["id:9dfae94f-677a-49a6-bee3-98a2bb470e48" ""]]}]}]}

;; In Org-mode:
;;
;;     #+title: Hello
;;
;;     Link:
;;
;;     [[id:9dfae94f-677a-49a6-bee3-98a2bb470e48][Open problems]]
;;
;; Or in markdown:
;;
;;     Link:
;;
;;     [Open problems](id:9dfae94f-677a-49a6-bee3-98a2bb470e48)
;;
;; More examples: https://github.com/teodorlu/pandoc-toolbox/tree/master/pandoc-examples


;;; Links
;;
;; A link is a Pandoc Inline. Inline docs:
;;
;;   https://hackage.haskell.org/package/pandoc-types-1.22.2.1/docs/Text-Pandoc-Definition.html#t:Inline

;; Links look like this:

{"t" "Link",
 "c" [["" [] []]
      [{"t" "Str", "c" "Open"}
       {"t" "Space"}
       {"t" "Str", "c" "problems"}]
      ["id:9dfae94f-677a-49a6-bee3-98a2bb470e48" ""]]}

(defn link? [el]
  (= "Link" (get el "t")))

(def link-target-path ["c" 2 0])
(defn link-target [link] (get-in link link-target-path))

(def link-target-title-path ["c" 2 1])
(defn link-target-title [link] (get-in link link-target-title-path))

(comment
  (let [some-link {"t" "Link",
                   "c" [["" [] []]
                        [{"t" "Str", "c" "Open"}
                         {"t" "Space"}
                         {"t" "Str", "c" "problems"}]
                        ["id:9dfae94f-677a-49a6-bee3-98a2bb470e48" ""]]}]
    (-> some-link
        (assoc-in link-target-path "google.com")
        link-target)))

;;; Blocks
;;
;; A Pandoc JSON contains pandoc version, metadata and blocks.

(defn blocks [pandoc]
  (get pandoc "blocks"))

(defn update-blocks [pandoc f & args]
  (apply update pandoc "blocks" f args))

;; A Pandoc Filter transforms Pandoc JSON on STDIN to Pandoc JSON on stdout.
;; Upstream documentation for Pandoc filters:
;;
;;     https://pandoc.org/filters.html

(defn filter-body-postwalk
  [pandoc filter-fn]
  (update-blocks pandoc (fn [blocks]
                          (clojure.walk/postwalk filter-fn blocks))))

(defn filter-body-prewalk
  [pandoc filter-fn]
  (update-blocks pandoc (fn [blocks]
                          (clojure.walk/prewalk filter-fn blocks))))
