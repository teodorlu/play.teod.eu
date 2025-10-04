;; # Hypermedia-driven interfaces in Babashka
;;
;; Still feeling a sliver of inspiration from Asbjørn Ulsberg's talk [Hypermedia: The First 2000 Years] on Javazone 2025, I decided to put some lessons to use.
;; I have this little [Babashka-powered slideshow viewer] I like to use when I want the simplest possible basis for a presentation.
;; What if I just built support for editing and reordering slides straight into the viewer?
;;
;; [Hypermedia: The First 2000 Years]: https://vimeo.com/1115578715
;; [Babashka-powered slideshow viewer]: https://github.com/teodorlu/bbslideshow/

(ns hypermedia-driven-interfaces-in-babashka)

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(comment
 ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
 ((requiring-resolve 'clojure.repl.deps/sync-deps))
 (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
 (clerk/clear-cache!))
