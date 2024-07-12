(ns teod.play.cli-test
  (:require
   [clojure.test :refer [deftest is]]
   [tplay.cli :as play.cli]))

(deftest words-test
  (is (= "i am the walrus"
         (play.cli/words "i" "am" "the" "walrus"))))

(deftest lines-test
  (is (= "i
am
the
walrus"
         (play.cli/lines "i" "am" "the" "walrus"))))
