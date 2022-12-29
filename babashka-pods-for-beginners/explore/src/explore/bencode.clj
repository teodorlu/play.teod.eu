(ns explore.bencode
  (:require [bencode.core])
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
  (-> s
      (.getBytes "UTF-8")
      ByteArrayInputStream.
      PushbackInputStream.
      bencode.core/read-bencode))

(decode "i99e")
