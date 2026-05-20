(ns tplay.next)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem statement
;;
;; tplay represents many different forms of texts:
;; - Texts I've written and shared
;; - Texts I've written but not shared
;; - Journals
;; - References to other people
;; - References to other texts
;;
;; All can be linked.
;;
;; I need to disambiguate between the different types, but also not introduce
;; overhead for my own rendering.

(comment
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Dataflow (draft)
  ;;
  ;; Represent things as neatly as I'm able to in raw form on disk, and expand
  ;; the model a bit on the way into the in-memory Datomic.

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; Data model
  ;;
  ;; So maybe just :link/text?
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem statement
;;
;; I cannot auto-reload individual pages with fat morph, as Pandoc produces the
;; <head> elements.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem statement
;;
;; I cannot tune the styling to my liking, as Pandoc produces the CSS for my
;; pages.

(comment
  ;; My use of Pandoc is causing problems. I not only use it as a source markup,
  ;; but also as a provider of styles and <head> metadata. I could rip it off.
  ;; Delete its generated CSS. Not use its generated <head> elements.
  ;;
  ;; Remove before add.
  )

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Problem statement
;;
;; I don't have total control of attributes in use. Each page has an EDN file
;; with some stuff in it. But are the keys qualified? Unqualified? What is the
;; semantics for each key?

(comment
  ;; I designed and built play.teod.eu before I'd used Datomic in anger. So I
  ;; lacked experience with modeling information in Clojure.
  )
