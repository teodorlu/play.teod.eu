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
    (for [file (fs/glob (fs/expand-home "~/.config/bink/provider") "*.edn")]
      {:path (str file)})))

(defn all-links []
  (mapcat (fn [{:keys [path]}]
            (:links (edn/read-string (slurp path))))
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

(defn browse-url-sync
  "Browse to a URL, but block until the browser yields control"
  [url]
  (clojure.java.browse/browse-url url))

(defn firefox-nohup
  "Open an URL with firefox, but don't wait."
  [url]
  (babashka.process/process ['nohup 'firefox url]))

(defn browse [links]
  (let [links-by-title (into {}
                             (for [l links]
                               [(:title l) l]))
        _ (assert (= (count links) (count links-by-title)) "Link titles must be unique")
        choice-title (fzf (map :title links))
        choice (get links-by-title choice-title)]
    #_
    (browse-url-sync (:href choice))
    ;; I'd like to use clojure.java.browse/browse-url for its portaibility.
    ;; But it blocks the thread, and doesn't terminate until the browser quits.
    ;; That leaves us a hanging terminal.
    ;; We don't want that!
    ;;
    ;; We could optionally shell out to `nohup bb -e (clojure.java.browse/browse-url "url"`)
    ;; ... but then we're back to having to do lots of manual escaping.
    ;;
    ;; bleh
    (firefox-nohup (:href choice))))

(defn browse-helpful [{}]
  (let [links (seq (all-links))]
    (if links
      (browse links)
      (help-getting-started))))

(defn rationale [{}]
  (clojure.java.browse/browse-url "https://play.teod.eu/bink/"))

(declare dispatch-table)

(defn lines [& ls]
  (str/join "\n" (map str ls)))

(defn help [{}]
  (println (lines "Bink: Bookmarks are data"
                  ""
                  "Available subcommands:"
                  (apply lines (for [{:keys [cmds]} dispatch-table]
                                 (str "  " (str/join " " cmds)))))))

(defn show-providers [{}]
  (doseq [p (providers)]
    (println (:path p))))

(def dispatch-table
  [{:cmds ["rationale"] :fn rationale}
   {:cmds ["help"] :fn help}
   {:cmds ["browse"] :fn browse-helpful}
   {:cmds ["providers"] :fn show-providers}
   {:cmds [] :fn browse-helpful}])

(defn main [& args]
  (cli/dispatch dispatch-table
                args
                {}))

(when (= *file* (System/getProperty "babashka.file"))
  (apply main *command-line-args*))

nil
