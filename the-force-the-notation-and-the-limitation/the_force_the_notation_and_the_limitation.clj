(ns the-force-the-notation-and-the-limitation
  (:refer-clojure :exclude [* / + -])
  (:require [munit.prefix :refer [k M]]
            [munit.si :refer [kg m s]]
            [munit.units :refer [* /]]
            [scicloj.kindly.v4.kind :as kind]))

(do
  ;; setup
  (set! *print-namespace-maps* false)
  (defn img [src] (kind/hiccup [:img {:style {:max-width "80vw"} :src src}]))
  (defmacro defshow
    "def, but also show the result in Clay"
    {:clj-kondo/lint-as 'clojure.core/def}
    [sym & body]
    `(do (def ~sym ~@body) ~sym)))

(kind/hiccup
 [:div
  [:hr]
  [:h1 "The force, the notation and the limitation"]
  [:em "a tale of unit systems in three parts"]
  [:hr]
  [:ul
   [:li "Part 1: What is 17 MN, really?"]
   [:li "Part 2: A data notation for numbers with units."]
   [:li "Part 3: Layered operators?"]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PART 1
(kind/hiccup [:div [:h2 "The force: What is 17 MN, really?"]
              [:em "is it much?"]])

(img "https://parenteser.mattilsynet.io/images/b539436d879e/the-hub.jpg")

(def N [kg m {s -2}])

(defshow MN (* M N))

(defshow F (* 17 MN))

;; How many Volkswagen ID.4s must we stack to get a weight of 17 MN?
;; Let's find out.

(def t (* 1000 kg))

(defshow vw-id4-mass (* 1.8 t {:cars/vw-id.4 -1}))
;; "1.8 tonnes per volgswagen id 4"

;; First, we need to translate 1.8 tonnes (mass) into weight (newtons).

(def g "Approximate free-fall accelleration on earth's surface"
  [9.81 m {s -2}])

(defshow vw-id4-weight (* vw-id4-mass g))

;; Now, divide!
(defshow id4s (/ [17 MN] vw-id4-weight))

;; In other words - we must stack 962 Volkswagen ID.4s to get the same load.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PART 2
(kind/hiccup [:div [:h2 "The notation: Numbers with units as data"]
              [:em "numbers, symbols, keywords, maps and vectors"]])

;; For HTML:
[:p "The force is " [:strong "17 MN"] "!"]

;; For HTTP:
{:request-method :get
 :uri "/the-force"}
{:status 200
 :headers {"Content-Type" "text/plain"}
 :body "17 MN"}

;; For numbers with units:
(def munit-examples
  [

   ;; plain numbers are unitless
   17 18 3.14 77

   ;; symbols and keywords are base units
   'm 's 'kg :cars/vw-id.4

   ;; vectors imply multiplication
   [17 MN] [40075.017 k m]

   ;; maps specify base unit exponents
   [9.81 'kg 'm {'s -2}]

   ])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PART 3
(kind/hiccup [:div [:h2 "The limitation: who owns the operator?"]
              [:em "... or can we have layered operators?"]])

(def EUR 'EUR)
(def NOK 'NOK)

;; Currencies is a good match for units, right?
(def NOK-per-EUR-2025-07-25
  [11.8995 {NOK 1 EUR -1}])

;; 43 EUR to NOK
(* [43 'EUR] NOK-per-EUR-2025-07-25)

;; ... buuut no! Not really!
;;
;; I found I preferred Tablecloth datasets, array programming is just too
;; useful.
((requiring-resolve 'clojure.java.browse/browse-url) "http://localhost:1972/")

;; so ... are we stuck? *, /, +, - must be controlled by *either* munit or
;; Tablecloth?

;; Not really.
;;
;; In Software Design for Flexibility (2021), Chris Hanson and
;; Gerald Jay Sussman propose *layered arithmetic*.
;;
;; Sneak peak:
(img "https://play.teod.eu/the-force-the-notation-and-the-limitation/sdf2.jpg")
(img "https://play.teod.eu/the-force-the-notation-and-the-limitation/sdf1.jpg")

;; Thank you :)

(comment
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  )
