(ns teod.play.api)

(defn today-str []
  (.format (java.time.format.DateTimeFormatter/ofPattern "yyyy-MM-dd")
           (java.time.LocalDateTime/now)))

(defn lol []
  (let [n (Math/floor (inc  (rand 10)))]
    (apply str "l"
           (repeat n "ol"))))

;; TODO
