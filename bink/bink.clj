#!/usr/bin/env bb

(ns bink
  (:require [babashka.fs :as fs]
            [clojure.edn :as edn]
            [babashka.process :as process]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; BINK - DESIGN DRAFT

;; CLI
;;
;; To open a link:
;;
;;   $ bink
;;
;; Select link with fzf
;; Then it's opened in your browser.
;;
;; Add a link
;;
;;   $ bink add "Clojure Deref" https://clojure.org/news/news

;; CONFIG
;;
;; | Path                        | Purpose                      |
;; |-----------------------------+------------------------------|
;; | =~/.config/bink/config.edn= | Bink main configuration file |
;; | =~/.config/bink/provider/*= | Additional link providers    |

;; LINK FORMAT
;;
;; in EDN
;;
;;   {:links [{:title "Clojure Deref" :href "https://clojure.org/news/news"}
;;            {:title "Clojureverse" :href "https://clojureverse.org/"}]}
;;
;; or JSON
;;
;;   {
;;       "links": [
;;           {"title": "Clojure Deref", "href": "https://clojure.org/news/news"},
;;           {"title": "Clojureverse", "href": "https://clojureverse.org/"}
;;       ]
;;   }


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; BINK - IMPLEMENTATION DRAFT

;; First, collect all the link providers

(defn providers []
  ;; Per 2022-09-14, we only support EDN links.
  (let [support-json false]
    (if support-json
      (fs/glob (fs/expand-home "~/.config/bink/provider") "*.{edn,json}")
      (fs/glob (fs/expand-home "~/.config/bink/provider") "*.edn"))))

(defn all-links []
  (mapcat (fn [p]
            (:links (edn/read-string (slurp (str p)))))
          (providers)))

(defn fzf [options]
  (-> @(process/process ["fzf"] {:in (str/join "\n" options)
                                 :out :string
                                 :err :inherit})
      :out
      str/trim))

(defn firefox [url]
  (process/process ["firefox" url]))

;; get links
(let [links (all-links)
      links-by-title (into {}
                           (for [l links]
                             [(:title l) l]))
      _ (assert (= (count links) (count links-by-title)) "Duplicate titles are not allowed")
      choice-title (fzf (map :title links))
      choice (get links-by-title choice-title)
      _ (firefox (:href choice))
      ])
