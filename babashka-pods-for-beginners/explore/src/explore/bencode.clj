(ns explore.bencode
  (:require [bencode.core]
            [clojure.walk])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream PushbackInputStream]))

(defn encode [x]
  (-> (doto (ByteArrayOutputStream.)
        (bencode.core/write-bencode x))
      .toString))

(comment
  (let [x 123]
    (.toString (bencode.core/write-bencode (ByteArrayOutputStream.) x))))

(comment
  (encode {:foo "bar"})
  ;; => "d3:foo3:bare"

  (= (encode {:foo "bar"})
     (encode {"foo" "bar"}))
  ;; => true

  (encode "teodor")
  ;; => "6:teodor"

  (encode 99)
  ;; => "i99e"

  (encode 1.2)
  ;; => error - integers only!
  :rcf
  )

(defn decode [s]
  (->> (.getBytes s "UTF-8")
       ByteArrayInputStream.
       PushbackInputStream.
       bencode.core/read-bencode
       (clojure.walk/postwalk (fn [form]
                                (if (bytes? form)
                                  (String. form "UTF-8")
                                  form)))))

(comment
  (decode "i99e")
  ;; => 99

  (-> [1 2 3]
      encode
      decode)
  ;; => [1 2 3]

  (-> {:name "teodor"}
      encode
      decode)
  ;; => {"name" [116, 101, 111, 100, 111, 114]}

  (-> {:name "teodor"}
      encode
      decode
      (get "name"))
  ;; => "teodor"

  (let [s "teodor"
        bytes (.getBytes s "UTF-8")]
    (String. bytes "UTF-8"))
  ;; => "teodor"

  :rcf)
