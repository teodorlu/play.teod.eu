(ns tplay.page
  "Entity model for pages

  For use _specifically_ to patch over the fact that I've been inconsistent with
  the data.")

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
