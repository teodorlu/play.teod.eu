(ns tplay.iki
  (:require
   [datascript.core :as d]
   [tplay.cli :as cli]))

(def datascript-schema
  {:page/slug {:db/unique :db.unique/identity}
   :page/uuid {:db/unique :db.unique/identity}
   :page/authors {:db/cardinality :db.cardinality/many
                  :db/valueType   :db.type/ref}})

(defn datascript-db []
  (let [rels (->> (cli/files->relations {})
                  vals
                  (sort-by :page/slug))
        conn (d/create-conn datascript-schema)]
    (d/transact! conn rels)
    @conn))

;; need to declare `apply2` before referring to it from `eval2`, otherwise it won't work!

(declare apply2)

(defn eval2 [form]
  (cond (= '() form)
        ()

        (= '+ form)
        +

        (= '- form)
        -

        (= '* form)
        *

        (number? form)
        form

        (seq? form)
        (apply2 (eval2 (first form))
                (map eval2 (rest form)))))

(defn apply2 [& forms]
  (let [explicit-args (butlast forms)
        variadic-args (last forms)
        operator (first explicit-args)
        args (concat (rest explicit-args) variadic-args)]
    ;; need to cheat here, I don't know how to implement this part of apply.
    (apply operator args)))

(eval2 '(+ 1 2 3))
;; => 6

(apply + '(1 2 3))
;; => 6

