;; # clerk/explain — what if I can discover Clerk functionality interactively?

(ns clerk-explain-idea
  (:require [nextjournal.clerk :as clerk]
            [nextjournal.clerk.viewer]))

(def explanations
  {::clerk/auto-expand-results?
   (let [rows (take 15 (repeatedly (fn []
                                     {:name (str
                                             (rand-nth ["Oscar" "Karen" "Vlad" "Rebecca" "Conrad"]) " "
                                             (rand-nth ["Miller" "Stasčnyk" "Ronin" "Meyer" "Black"]))
                                      :role (rand-nth [:admin :operator :manager :programmer :designer])
                                      :dice (shuffle (range 1 7))})))]
     (clerk/fragment
      "By default, data structures start collapsed, and can be expanded by hand."
      rows
      "But you can use auto-expand-results? to make data starte expanded."
      ;; I hoped to apply `::clerk/auto-exand-results? true` to rows, but that
      ;; doesn't work. I don't know how to set "viewer opts" outside of toplevel
      ;; metadata.
      ^{::clerk/auto-expand-results? true} rows))})

(defn explain* [kw-or-sym]
  (or
   (get explanations kw-or-sym)
   (clerk/html [:p "Sorry, can't explain " [:code kw-or-sym] " :(("])))

;; begin

(clerk/present
 {::clerk/auto-expand-results? true
  :nextjournal/value ["hi" "there"]
  :nextjournal/viewer nextjournal.clerk.viewer/vector-viewer})

;; end

(defmacro explain [x]
  `(explain* (quote ~x)))

(explain ::clerk/auto-expand-results?)

(comment
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
  (clerk/clear-cache!))
