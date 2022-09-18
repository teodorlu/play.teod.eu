(ns img
  (:require [mikera.image.core :as image]
            [mikera.image.colours :as color]))

(def fill! image/fill!)
(def new image/new-image)
(def random-color color/rand-colour)
(def show image/show)

(defn rgb->color [[r g b]]
  (color/rgb r g b))
