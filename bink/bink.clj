#!/usr/bin/env bb

(ns bink
  (:require
   [babashka.cli :as cli]
   [babashka.fs :as fs]
   [babashka.process :as process]
   [clojure.edn :as edn]
   [clojure.java.browse]
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
  (when (fs/directory? (fs/expand-home "~/.config/bink/provider"))
    (let [support-json false]
      (if support-json
        (fs/glob (fs/expand-home "~/.config/bink/provider") "*.{edn,json}")
        (fs/glob (fs/expand-home "~/.config/bink/provider") "*.edn")))))

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

(defn help-getting-started []
  (println (str/trim "
Hi!

It doesn't look like you have any links. bink doesn't currently help you add or
edit links.

To get a starting point, try:

    mkdir -p ~/.config/bink/provider
    cat <<EOF >> ~/.config/bink/provider/clojure.edn
{:links [{:title \"Clojure Deref\" :href \"https://clojure.org/news/news\"}
         {:title \"Clojureverse\" :href \"https://clojureverse.org/\"}]}
EOF

But do feel free to organize your links however you want. That's what bink is
for.

Teodor
"))
  )

(defn browse [links]
  (let [links-by-title (into {}
                             (for [l links]
                               [(:title l) l]))
        _ (assert (= (count links) (count links-by-title)) "Duplicate titles are not allowed")
        choice-title (fzf (map :title links))
        choice (get links-by-title choice-title)]
    (clojure.java.browse/browse-url (:href choice))))

(defn browse-helpful [{}]
  (let [links (seq (all-links))]
    (if links
      (browse links)
      (help-getting-started))))

(defn help [{}]
  (clojure.java.browse/browse-url "https://play.teod.eu/bink/"))

(defn main [& args]
  (cli/dispatch [{:cmds ["help"] :fn help}
                 {:cmds [] :fn browse-helpful}]
                args
                {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
