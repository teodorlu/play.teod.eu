(ns tplay.nprint-test
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [tplay.nprint :as nprint :refer [nprint nprint-indent]]))

(deftype RandomShit [y])

(deftest nprint-test

  (testing "primitives"
    (is (= (pr-str "hei\t, du\n! \"lol\".")
           (nprint "hei\t, du\n! \"lol\".")))
    (is (= "1" (nprint 1)))
    (is (= (pr-str [:nprint/unsupported `RandomShit]) (nprint (RandomShit. 42))))
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
    (nprint/nprint-coll {"x" "x"} {})

    (is (= "{\"x\" \"x\"}"
           (nprint {"x" "x"})))
    (is (= '{:a 1 b 2 "c" :lol/haha}
           (-> '{:a 1 b 2 "c" :lol/haha}
               nprint edn/read-string))))

  (testing "print-indent always indents"
    (is (= "[1\n 2]" (nprint-indent ["1" "2"] 0))))

  (testing "nprint starts indenting when lines would exceed 40 characters"
    (is
     (str/includes? (nprint ["the headbands were green"
                             "brighly, radioactively, green"
                             "not something you'd bring to the forest."])
                    "\n ")))

  ;; TODO more collection types
  ;; TODO nested collections

  )
