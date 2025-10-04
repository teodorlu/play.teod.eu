(ns tplay.linkui
  "Some functions to call with babashka -x to create UIs"
  (:require [tplay.api :as tplay]))

(def relations (tplay/files->relations2))
(def title-> (->> relations
                  (map (juxt :title identity))
                  (into (sorted-map))))

(defn ^:export page-titles [_]
  (doseq [title (keys title->)]
    (println title)))

(defn ->link [{:keys [title uuid]}]
  (str "[[" title "][id:" uuid "]]"))

(defn ^:export title-to-link [{:keys [title]}]
  (println (-> title title-> ->link)))

(comment
  (->> title-> vals shuffle (take 5))

  ;; Use from Emacs Lisp (requires s.el):
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
