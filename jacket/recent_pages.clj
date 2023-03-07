(ns recent-pages
  (:require [clojure.edn :as edn]))

(let [index (edn/read-string (slurp "../index/big.edn"))]
  (prn (take 3 index)))
