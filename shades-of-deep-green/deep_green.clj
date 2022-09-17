(ns deep-green
  (:require [mikera.image.core :as i]
            [mikera.image.colours :as c]))

(let [img (i/new-image 1920 1080)]
  (i/fill! img (c/rand-colour))
  (i/show img))
