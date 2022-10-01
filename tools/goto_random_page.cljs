(ns goto-random-page
  (:require [clojure.edn :as edn]
            [ajax.core :refer [GET]]))

(defn goto-random-page []
  (let [handler (fn [big-index-str]
                  (let [big-index (edn/read-string big-index-str)
                        item (rand-nth big-index)]
                    (prn item)
                    ;; window.location.href = "/" + item.slug + "/"
                    (set! (.-href js/window.location) (str "/" (:slug item) "/"))))]
    (GET "/index/big.edn" {:handler handler})))

(set! (.-goto_random_page js/window) goto-random-page)
