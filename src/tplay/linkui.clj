(ns tplay.linkui
  "Some functions to call with babashka -x to create UIs"
  (:require [babashka.process :as p]
            [clojure.string :as str]
            [tplay.api :as tplay]))

(def relations (tplay/files->relations2))

(def title-> (->> relations
                  (map (juxt :title identity))
                  (into (sorted-map))))
#_(->> title-> vals shuffle (take 5))

(defn ^:export page-titles [_]
  (doseq [title (keys title->)]
    (println title)))

(defn ->link [{:keys [title uuid]}]
  (str "[[id:" uuid "][" title "]]"))

(def title->link (comp ->link title->))

(defn ^:export title-to-link [{:keys [title]}]
  (println (title->link title)))

(defn fzf-select [options]
  (some-> (p/shell {:in (str/join "\n" (map str options))
                    :out :string}
                   "fzf")
          :out
          str/split-lines
          first))

;; bb -x tplay.linkui/fzf
(defn ^:export fzf
  [_]
  (println
   (-> (keys title->)
       fzf-select
       title->link)))

(comment

  ;; Emacs UI for inserting links (requires s.el)
  ;; Must be reader-commented to prevent Clojure syntax errors.
  #_
  (defun tplay-link ()
    "Insert link to a different page"
    (interactive)
    (let* ((default-directory "~/repo/teodorlu/play.teod.eu")
           (pages (s-with "bb -x tplay.linkui/page-titles"
                          shell-command-to-string
                          s-trim
                          s-lines))
           (page-title (completing-read "Pick a line" pages))
           (link (s-with (concat "bb -x tplay.linkui/title-to-link" " :title " (shell-quote-argument page-title))
                         shell-command-to-string
                         s-trim)))
      (insert link)))
  )
