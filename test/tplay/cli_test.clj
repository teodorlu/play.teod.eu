(ns tplay.cli-test
  (:require
   [clojure.test :refer [deftest is]]
   [tplay.cli]))

(deftest words-test
  (is (= "i am the walrus"
         (tplay.cli/words "i" "am" "the" "walrus"))))

(deftest lines-test
  (is (= "i
am
the
walrus"
         (tplay.cli/lines "i" "am" "the" "walrus"))))
