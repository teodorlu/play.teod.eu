;; # Keeping relevant information at hand
;;
;; After a year working on a die-hard FP/Clojure team, my view on how to get familiar with code has changed.
;; At first, I tried reading the code, then /getting/ the behavior.
;; I've now moved away from this view, instead focusing on understanding how information flows through my system, and how to retrieve relevant information.
;; Knowing how to get relevant information at hand, and build systems where information is within my reach has turned out to be more effective: I have to read less code and write less code to get work done.

(ns information-at-hand)

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(comment
  (require '[nextjournal.clerk :as clerk]
           '[babashka.fs :as fs])
  (clerk/serve! {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
  (clerk/clear-cache!))
