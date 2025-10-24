(use-package clojure-mode
  :bind (:map clojure-mode-map
              ("C-ø" . clay-make-last-sexp)
              ("C-f" . teod/next-slide)
              ("C-b" . teod/previous-slide)))

(defun teod/next-slide ()
  (interactive)
  (paredit-forward)
  (clay-make-last-sexp))

(defun teod/previous-slide ()
  (interactive)
  (paredit-backward)
  (clay-make-last-sexp))
