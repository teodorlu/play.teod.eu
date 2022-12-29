(ns explore.bencode-examples
  (:require [explore.bencode :as bencode]))

(bencode/encode {:foo "bar"})
;; => "d3:foo3:bare"

(= (bencode/encode {:foo "bar"})
   (bencode/encode {"foo" "bar"}))
;; => true

(-> {:foo "bar"}
    bencode/encode
    bencode/decode)
;; => {"foo" "bar"}

(bencode/encode "teodor")
;; => "6:teodor"

(bencode/encode 99)
;; => "i99e"

(bencode/encode 1.2)
;; => error - integers only!

(bencode/decode "i99e")
;; => 99
