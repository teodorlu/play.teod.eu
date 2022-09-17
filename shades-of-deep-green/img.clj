(ns img
  (:require [mikera.image.core :as image]
            [mikera.image.colours :as color]))

(def random-color color/rand-colour)
(def new image/new-image)
(def fill! image/fill!)
(def show image/show)
