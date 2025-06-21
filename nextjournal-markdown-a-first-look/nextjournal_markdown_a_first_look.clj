;; # Nextjournal Markdown: A First Look

;; [Nextjournal Markdown](https://github.com/nextjournal/markdown) is a new Markdown parser for Clojure.
;; It has some appealing properties:
;; - A pure data intermediate representation (like Hiccup and Ring)
;; - Supports Babashka, and ships with Babashka 🔥
;; - Parses CommonMark, the closest we've got to an official Markdown spec.

;; To explore, I set myself the following task:
;; 1. Demonstrate finding some markdown and parsing it with babashka fs and nextjournal markdown
;; 2. Then create some HTML files
;; 3. Then render.

(ns nextjournal-markdown-a-first-look
  (:require [babashka.fs :as fs]
            [nextjournal.clerk :as clerk]
            [nextjournal.markdown :as md]))

^{::clerk/auto-expand-results? true}
(md/parse "# Markdown, OHOI! ⛵")

;; I'm seeing:
;; - A toplevel map with metadata
;; - A tree in :content that looks like Hiccup when I squint.

;; Let's try make some HTML (Hiccup) out of this.
(try (-> "# Markdown, OHOI! ⛵" md/parse md/->hiccup)
     (catch Exception e
       (clerk/fragment (clerk/md "Something went wrong!")
                       e)))

;; Why?
;; Because ->hiccup takes a raw string, not the data IR.
(-> "# Markdown, OHOI! ⛵" md/->hiccup)

;; Much better.
;; We can even render the HTML!
(-> "# Markdown, OHOI! ⛵" md/->hiccup clerk/html)

(fs/glob ".." "**/*.md")

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(comment
 ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
 ((requiring-resolve 'clojure.repl.deps/sync-deps))
 (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
 (clerk/clear-cache!))
