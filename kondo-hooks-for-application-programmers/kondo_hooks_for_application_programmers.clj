^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(ns kondo-hooks-for-application-programmers)

;; # CLJ-Kondo hooks for application programmers

;; Macros are a powerful tool that can cause confusion and tears.

;; Static analysis with CLJ-Kondo can help tame that confusion and avoid those tears.
;; Let's learn about just that.

;; ## Kondo already knows certain macros

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

;; More importantly, the macro will give an "unresolved symbol" warning with
;; CLJ-Kondo-powered tooling:

(deflaughing laugh-all-you-want (+ 1 2))

(clerk/html [:img {:src "https://play.teod.eu/kondo-hooks-for-application-programmers/kondo-warning.png"}])




;; Already, I've noticed two problems:
;; 1. CLJ-Kondo gives me an "unresolved symbol" warning for `laugh-all-you-want`
;; 2. Clerk's dependency analysis breaks down, because it doesn't guess that redefining
;;    `deflaughing` in the background needs to invalidate the capture-out of the macroexpand.



{:nextjournal.clerk/visibility {:code :show :result :show}}

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
