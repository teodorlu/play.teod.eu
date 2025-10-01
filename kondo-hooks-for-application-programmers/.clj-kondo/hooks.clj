(ns hooks
  (:require [clj-kondo.hooks-api :as hooks]))

(defn defgrinning-hook [{:keys [node]}]
  (let [[_defgrinning sym & body] (:children node)]
    (when-not sym
      (throw (ex-info "No sym provided, you silly person!" {:sym sym :body body})))
    (when (= sym (symbol "☢"))
      (throw (ex-info "Radioactivity disallowed, it does not cause grinning!"
                      {:sym sym :body body})))
    {:node (hooks/list-node (list*
                             (hooks/token-node 'def)
                             (hooks/token-node sym)
                             body))}))
