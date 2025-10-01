^{:nextjournal.clerk/visibility {:code :fold :result :hide}}
(ns kondo-hooks-for-application-programmers
  {:nextjournal.clerk/toc true
   :clj-kondo/config
   '{:hooks
     {:analyze-call
      {kondo-hooks-for-application-programmers/defgrinning
       kondo-hooks-for-application-programmers/defgrinning-hook}}}}
  (:require [clojure.string :as str]))

;; # CLJ-Kondo hooks for application programmers

;; Macros are a powerful tool that can cause confusion and tears.

;; Static analysis with CLJ-Kondo can help tame that confusion and avoid those tears.
;; Let's learn about just that.

;; ## Kondo knows certain macros

;; Take the `for` macro:

(for [n (range 10)]
  (- n))

{:nextjournal.clerk/visibility {:code :fold :result :hide}}

(do
  (require '[clojure.repl]
           '[nextjournal.clerk :as clerk])
  (defn doc* [sym]
    `(clerk/html [:pre (with-out-str (clojure.repl/doc ~sym))]))
  (defmacro doc [code]
    (doc* code)))

{:nextjournal.clerk/visibility {:code :show :result :show}}

;; Your REPL knows the `for` macro.

(doc for)

;; What about this sillyness?

(defmacro i-laugh-at-your-two-parameters
  "Laugh at two parameters, hopelessly ignored."
  [x y] "lol")

(doc i-laugh-at-your-two-parameters)

;; `doc` works. Let's take a slightly bigger example.

(defmacro deflaughing
  [sym & body]
  (println "I laugh at your petty" sym
           ", created from the woefully inadequate" body)
  `(def ~sym (do ~@body)))

(defmacro capture-out [& body]
  `(clerk/html [:pre (with-out-str ~@body)]))

;; Under macroexpansion, this macro will laugh in your face.

(capture-out (macroexpand '(deflaughing the-number-after-one-and-two (+ 1 2))))

;; *But Kondo does not understand your macro!*

(deflaughing laugh-all-you-want (+ 1 2))

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(defn img [src]
  (clerk/html [:img {:src (str "https://play.teod.eu/kondo-hooks-for-application-programmers/" src)}]))

;; "unresolved symbol", it says!
^{:nextjournal.clerk/visibility {:code :hide :result :show}}
(img "kondo-does-not-understand-it.png")

;; ## Kondo analyze hooks
;;
;; Hooks!
;; The source of truth for their usage is [doc/hooks.md] in the Kondo repo.
;; Lets try those hooks.
;;
;; [doc/hooks.md]: https://github.com/clj-kondo/clj-kondo/blob/master/doc/hooks.md

;; Hooks work on a custom type tailored to represent Clojure code.
;; Plain symbols and Clojure data structures because it doesn't keep track of location data: which column, which line.


^{:nextjournal.clerk/visibility {:code :show :result :hide}}
(require '[clj-kondo.hooks-api :as hooks])
(def node
  (hooks/list-node [(hooks/string-node "hi")
                    (hooks/string-node ", there!")]))

(type node)

;; I'm guessing that node can be str-ed back to Clojure code ...

(str node)

;; bingo.
;; Now we can get our delicious Clojure data structures back.

(read-string (str node))

;; Okay, nice.
;; How do we build support for our new macro?
;; The API docs give us a clue:
;;
;; > It is not important that the code is rewritten exactly according to the
;; > macroexpansion. What counts is that the transformation rewrites into code
;; > that clj-kondo can understand.
;;
;; Nice!
;; Our task is to rewrite the code into code that Kondo *can* understand.
;;
;; Let's try expand `(deflaughing x (+ 1 2))` into `(def x (+ 1 y))`.

;; first a macro.

(defmacro defgrinning [sym & body]
  (println (str sym "😁"))
  `(def ~sym (do ~@body)))

(with-out-str (macroexpand '(defgrinning x 33)))

;; Then a hook!

(defn defgrinning-hook [{:keys [node]}]
  (let [[_defgrinning sym & body] (:children node)]
    (when-not sym
      (throw (ex-info "No sym provided, you silly person!" {:sym sym :body body})))
    (when-not (= sym (symbol "☢"))
      (throw (ex-info "Radioactivity disallowed, it does not cause grinning!"
                      {:sym sym :body body})))

    {:node (hooks/list-node (list*
                             (hooks/token-node 'def)
                             (hooks/token-node sym)
                             body))}))

;; I'm going to cheat a bit here, because I've already "installed" this hook in
;; my namespace declaration.

^{:nextjournal.clerk/visibility {:code :fold :result :show} :nextjournal.clerk/no-cache true}
(clerk/caption "Beware, a listing of code, outside of top-down flow"
               (clerk/code
                (->> (slurp *file*)
                     (str/split-lines)
                     (take 9)
                     (str/join "\n"))))

(defgrinning x "it's fine ...")





^{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html [:div {:style {:height "30vh"}}])

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
 ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
 ((requiring-resolve 'clojure.repl.deps/sync-deps))
 (clerk/build! {:paths [((requiring-resolve 'babashka.fs/file-name) *file*)], :out-path "."})
 (clerk/clear-cache!)
 ((requiring-resolve 'babashka.process/shell) "open .")
 )
