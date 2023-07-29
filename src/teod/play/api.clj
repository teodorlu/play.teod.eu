(ns teod.play.api)

(defn lol []
  (let [n (Math/floor (inc  (rand 10)))]
    (apply str "l"
           (repeat n "ol"))))

;; TODO
