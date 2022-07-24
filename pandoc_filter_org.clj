;; babashka script to create a nicer table of content than what's provided by Pandoc defaults

;; idea
;;
;;   1. provide toc
;;   2. provide link up

;; impl
;;
;;  1. first loop through all the headers and then collect the headers
;;  2. make a nice TOC
;;  3. put that TOC on top
;;  4. then also put a link "up" on top

;; "why must this be a file"?
;;
;; I'd rather have it in play.clj.
;; But Pandoc filters must be scripts.
;; Hmm, actually, they don't have to be.
;; We can just use the pipe thing.

;; New name:
;;
;;   pandoc ... | play.clj |
