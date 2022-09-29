(ns buttons
  (:require [clojure.edn :as edn]
            [ajax.core :refer [GET]]))

(defn goto-random-page []
  (let [handler (fn [big-index-str]
                  (let [big-index (edn/read-string big-index-str)]
                    (prn (rand-nth big-index))))]
    (GET "/index/big.edn" {:handler handler})))

(set! (.-goto_random_page js/window) goto-random-page)
