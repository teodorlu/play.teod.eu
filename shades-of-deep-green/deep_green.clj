(ns deep-green
  (:require [mikera.image.core :as i]
            [mikera.image.colours :as c]
            [img]))

(let [img (img/new 1920 1080)
      ;; color1 [0 200 0]
      color2 [0 0.7 0.5]
      ]
  (img/fill! img (img/rgb->color color2))
  (img/show img))
