(ns tplay.page)

;; The entity model for pages is currently a bit of a mess.
;;
;; This namespace contains that mess.

(defn published-or-created [page]
  (or (:published page)
      (:created page)))

(defn slug [page]
  (:id page))

(defn link [page]
  (when (slug page)
    (str "/" (slug page) "/")))

(defn id [page]
  (:id page))

(defn title [page]
  (or (:title page) (id page)))
