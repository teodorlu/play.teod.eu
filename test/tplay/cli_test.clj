(ns tplay.cli-test
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [clojure.walk :refer [postwalk]]
   [tplay.cli :refer [nprint]]))

(deftest words-test
  (is (= "i am the walrus"
         (tplay.cli/words "i" "am" "the" "walrus"))))

(deftest lines-test
  (is (= "i
am
the
walrus"
         (tplay.cli/lines "i" "am" "the" "walrus"))))

(deftype RandomShit [y])

(deftest nprint-test
  (is (= "1" (nprint 1)))
  (is (= "[:nprint/unsupported tplay.cli_test.RandomShit]" (nprint (RandomShit. 42))))
  (is (= "\"hei\"" (nprint "hei")))
  (is (= ":x" (nprint :x)))
  (is (= ":goodstuff/cake" (nprint :goodstuff/cake)))
  (is (= "goodstuff/cake" (nprint 'goodstuff/cake)))
  (is (= "[1 2 3]" (nprint [1 2 3])))
  (is (= "(1 2 3)" (nprint '(1 2 3))))

  (testing "Sets and maps don't retain their order, and can be tested via roundtripping"
    (is (= #{1 2 3}
           (-> #{1 2 3}
               nprint edn/read-string)))
    (is (= '{:a 1 b 2 "c" :lol/haha}
           (-> '{:a 1 b 2 "c" :lol/haha}
               nprint edn/read-string)))
    )

  #_
  (testing "wraps very long strings"
    (is
     (str/includes? (nprint ["a veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long string"
                             "an anooooooooooooooooooooooooooooooooooooooooooooother quite long one."])
                    "\n")))


  )
