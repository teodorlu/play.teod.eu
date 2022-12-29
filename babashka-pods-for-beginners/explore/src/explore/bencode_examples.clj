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

(defn examples [f & exs]
  (for [e exs]
    [e (f e)]))

(examples bencode/encode
          {"x" 1}
          {"name" "tom"}
          ["n" "tom"]
          [1 2 3]
          [1 2]
          )
;; => ([{"x" 1} "d1:xi1ee"]
;;     [{"name" "tom"} "d4:name3:tome"]
;;     [["n" "tom"] "l1:n3:tome"]
;;     [[1 2 3] "li1ei2ei3ee"]
;;     [[1 2] "li1ei2ee"])
