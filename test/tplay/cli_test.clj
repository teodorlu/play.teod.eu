(ns tplay.cli-test
  (:require
   [clojure.edn :as edn]
   [clojure.string :as str]
   [clojure.test :refer [deftest is testing]]
   [clojure.walk :refer [postwalk]]
   [tplay.cli :refer [nprint nprint-indent]]))

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

  (testing "primitives"
    (is (= "1" (nprint 1)))
    (is (= "[:nprint/unsupported tplay.cli_test.RandomShit]" (nprint (RandomShit. 42))))
    (is (= "\"hei\"" (nprint "hei")))
    (is (= ":x" (nprint :x)))
    (is (= ":goodstuff/cake" (nprint :goodstuff/cake)))
    (is (= "goodstuff/cake" (nprint 'goodstuff/cake))))

  (testing "ordered collections"
    (is (= "[1 2 3]" (nprint [1 2 3])))
    (is (= "(1 2 3)" (nprint '(1 2 3)))))

  (testing "sets"
    (is (= #{1 2 3}
           (-> #{1 2 3}
               nprint edn/read-string))))

  (testing "maps"
    (tplay.cli/nprint-coll {"x" "x"} {})

    (is (= "{\"x\" \"x\"}"
           (nprint {"x" "x"})))
    (is (= '{:a 1 b 2 "c" :lol/haha}
           (-> '{:a 1 b 2 "c" :lol/haha}
               nprint edn/read-string))))

  (testing "indents"
    (is (= "[1\n 2]" (nprint-indent ["1" "2"] 0)))

    (is
     (str/includes? (nprint ["a very long string"
                             "an another quite long one."])
                    "\n")))

  )
