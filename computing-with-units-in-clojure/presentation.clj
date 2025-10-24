;; This file *is* the presentation for Computing with units in Clojure,
;; presented on Macroexpand 2025 Noj, October 17th.
;;
;; If you just want to watch, view the recording. This file is kept *to have a
;; record* for myself.
;;
;; Note:
;;
;; - Custom tweaks have been made to Clay to remove visual clutter from the browser.
;;   Check deps.edn.
;;
;; - The presentation was given from inside Emacs. These key bindings were used:
;;
;;   | C-f | view next slide          |
;;   | C-b | view previous slide      |
;;   | C-ø | view slide before cursor |
;;
;;   controls.el contains an Emacs Lisp starting point for doing this.

(ns presentation
  (:refer-clojure :exclude [* + - /])
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [scicloj.kindly.v4.kind :as kind]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Preparation
(do
  (defmacro defshow
    "def, but also show the result in Clay"
    {:clj-kondo/lint-as 'clojure.core/def}
    [sym & body]
    `(do (def ~sym ~@body) ~sym))
  (def signal-yellow "#ffe300")
  (def bright-teal "#a3ffff")
  (def big-blackyellow {:width "100%" :height "50vw"
                        :background-color "black" :color signal-yellow
                        :display "flex" :align-items "center" :justify-content "center"
                        :flex-direction "column"})
  (defn splat [styles & children]
    (kind/hiccup (into [:div {:style (merge big-blackyellow {:font-size "48px" :font-weight "700"} styles)}]
                       children)))
  (defn bombastically [styles & children]
    (kind/hiccup (into [:div {:style (merge big-blackyellow {:font-size "32px" :font-weight "500"
                                                             :padding "18px"} styles)}]
                       children)))
  (defn proclaim [styles & children]
    (if-not (map? styles)
      (apply proclaim {} styles children)
      (kind/hiccup (into [:div {:style (merge big-blackyellow {:font-size "24px" :font-weight "500"
                                                               :padding "18px"} styles)}]
                         children))))
  (defn svg [filename]
    (kind/html (slurp (io/resource filename))))
  (defn img [src] (kind/hiccup [:img {:style {:max-width "100%"} :src src}]))
  (defn hspace [h] [:div {:style {:height h}}])
  (require 'clojure.pprint)
  (defn pp-str [x] (with-out-str (clojure.pprint/pprint x)))
  (def teal! {:style {:color bright-teal}}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; What is computation?
(splat {:font-weight "900"} "Computing with units and Clojure"
       (hspace "32px")
       [:div {:style {:font-weight "500"
                      ;; :width "100%"
                      :font-size "24px"}}
        [:div "Macroexpand-Noj 2025, October 17th"
         [:br]
         [:a {:href "https://scicloj.github.io/macroexpand-2025"
              :style {:color bright-teal}}
          "scicloj.github.io/macroexpand-2025"]]
        (hspace "12px")
        [:div "Teodor Heggelund"]])

(splat {:font-weight "900" :font-size "108px"} "Computation")
(proclaim "... but what is computation?")
(proclaim "is computation stuff we do with computers?")

(do
  (def computation-def1
    (list [:div "“" [:em "Computation"] " is stuff we do with computers"]
          [:div [:em "Computers"] " are things that compute”"]
          [:div {:style {:height "48px"}}]
          [:div {:style {:font-size "32px" :font-weight "700"
                         :color bright-teal}}
           "This definition is cyclical."]))
  (proclaim {}
            [:div computation-def1]))

(proclaim [:span "... but cyclical definitions are " [:em teal! "bad"] "."])

(proclaim [:span "Can we find a non-cyclical definition of "
           [:span teal! "“computer”"]
           " from the past?"])

(proclaim {:height "inherit"
           :padding "20px"}
          [:div
           [:div
            (kind/hiccup [:div (img "https://upload.wikimedia.org/wikipedia/commons/0/06/Human_computers_-_Dryden.jpg")
                          [:p {:style {:margin-bottom "0"}} [:em "NACA High Speed Flight Station Computer Room (1949)"] " (source: " [:a {:href "https://commons.wikimedia.org/wiki/File:Human_computers_-_Dryden.jpg"} "Wikimedia"]
                           ", via " [:a {:href "https://en.wikipedia.org/wiki/Computer_(occupation)"}
                                     [:em "Computer (Occupation)"]]
                           ", public domain)"]])]]
          (hspace "18px")
          [:div {:style {:color bright-teal}}
           [:em "Physics computations of flight trajectories"]]
          [:div {:style {:color "grey"
                         :font-size "18px"}}
           "mistaking meters for feet is not advised!"])

(proclaim {}
          [:div {:style {:font-size "18px"}} "Working definition:"]
          [:div {:style {:font-size "32px"
                         :color bright-teal}}
           [:em {:style {:font-weight "700"}} "Computation" ] " is knowledge work assisted by rigorous methods"])

(proclaim [:em "... then, in terms of that definition ..."])

(proclaim [:div {:style {:max-width "60vw"}}
           [:div {:style {:font-size "32px"
                          :color bright-teal}}
            "Interactive programming"]
           [:div {:style {:color "grey"}} "is interactive computation"]
           [:div "in order to understand and change behavior of a running program."]])

(proclaim [:div {:style {:max-width "60vw"}}
           [:div {:style {:font-size "32px"
                          :color bright-teal}}
            "exploratory data science"]
           [:div {:style {:color "grey"}} "is interactive computation"]
           [:div "in order to understand the world through datasets"
            ", and communicate that understanding to others"]])

(proclaim "... and")

(proclaim [:div {:style {:max-width "60vw"}}
           [:div {:style {:font-size "32px"
                          :color bright-teal}}
            "interactive calculation"]
           [:div {:style {:color "grey"}} "is interactive computation"]
           [:div "in order to understand and change the flow of forces in a system"
            " in order to estimate capacity."]])

(svg "interactive2.svg")

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Computing with units
;; (hope the demo gods are in a good mood!)

(proclaim [:div
           [:div "in order to"]
           [:div {:style {:margin-left "18px" :color "grey"}}
            [:div "estimate load capacity"]
            [:div "compute flight trajectories"]]
           [:div teal! "it's nice to use numbers with unit."]
           (hspace "32px")]
          [:div {:style {#_#_:font-size "18px"}} "in persuit of that goal, I have the pleasure to present"])

(proclaim {}
          [:div {:style {:font-size "32px"}}
           "Munit"]
          [:div [:em "a data notation for numbers with unit"]]
          (hspace "18px"))







;; (T: remember to slow down.
;;  this is all normal Clojure data.
;;  no magic.)

[1 'm]









["Note the symbol!"
 "Symbols are base units."
 ((juxt identity (partial map type) type)
  [1 'm])]







["Base unit exponent in a map."
 "                                                                                      "
 "These are the same:"
 [1 'm 'm]
 [1 {'m 2}]]









["But clojure.core/+ doesn't care about units."
 (let [expr '(clojure.core/+ [1 'm] [1 'm])]
   (try
     (eval expr)
     (catch Exception e
       ["Evaluating" expr "failed with" (ex-message e)])))
 "if we could only make our own operators ..."]












(proclaim {} "But wait.")



















(proclaim "Clojure doesn't have operators. 🤔🤔🤔")

















(proclaim
 [:div "Only namespaces that map symbols to vars, and those vars are somtimes functions."]
 [:div "All within our grasp."]
 (hspace "32px")
 [:div {:style {:text-align "center"
                :font-size "12px"}}
  (repeat (clojure.core/* 3 3 3 3) "🤔")])











(proclaim "let's use a different +.")





(require '[munit.units :as munit :refer [+ - * /]])








(+ [1 'm] [1 'm])



(- [10 'm] [3 'm])














(proclaim "let's do a more interesting example:"
          (hspace "18px")
          [:div {:style {:color bright-teal
                         :font-size "48px"}}
           [:em "compute the downward force of 200 kg."]]
          (hspace "18px")
          [:div "(at the earth's surface)"])














(do
  (def g "gravitational accelleration at the earth's surface"
    [9.81 'm {'s -2}])
  (* [200 'kg] g))







(proclaim [:em "let's measure that in Newtons."])




;; from https://en.wikipedia.org/wiki/Newton_(unit),
(defshow N '[kg m {s -2}])

(munit/measure-in (* [200 'kg] g)
                  N)





(proclaim [:em "now, measure in kilo-Newtons."])





(do
  ^{:note "I plan to move munit.prefix to munit.si.prefix."}
  (require '[munit.prefix :as prefix])
  (munit/measure-in (* [200 'kg] g)
                    [prefix/k N]))







(proclaim "How much is 17 MN (Mega-Newtons)?"
          (hspace "18px")
          "Let's measure in cars.")

(kind/hiccup
 (list
  [:div "17 MN was a real point load during the design of this hotel."]
  [:div "It was a bit too much, so we made tweaks to distribute loads more evenly."]
  (img "https://parenteser.mattilsynet.io/images/b539436d879e/the-hub.jpg")))











(defshow F [17 prefix/M N])


(defshow t [1000 'kg])

["A Volkswagen ID.4 weighs about 1.8 tonnes."
 "Think  \"1.8 tonnes downward force per car\":"
 (defshow force-per-car
   (/ (* g [1.8 t])
      'car))
 "Funny unit, eh, \"force per car\"?"]


["Divide force by force per car to compute equivalent cars."
 (/ F force-per-car)]

["How many cars?"
 (defshow cars-line
   (-> (/ F force-per-car)
       (munit/measure-in 'car)
       int
       (repeat '🚗)))]

(proclaim [:div "That's a bit hard to read."]
          [:div "Try again."])

(kind/hiccup
 [:div (for [car cars-line]
         (str " " car))])

(proclaim "... group by 10 ...")

(kind/hiccup
 [:div (for [car-group (partition-all 10 cars-line)]
         [:span {:style {:margin "5px"}}
          (str/join "" car-group)])])

(proclaim "... group by 100 ...")

(kind/hiccup
 [:div (for [car-group (partition-all 100 cars-line)]
         [:span {:style {:margin "5px"}}
          (str/join "" car-group)])])








(bombastically {}
               [:div {:style {:font-size "18px"}}
                "Lesson:"]
               [:span {:style {:color "#a3ffff"
                               :font-weight "700"}}
                "Good choice of units drives your intuition."]
               [:div {:style {:font-size "18px"}}
                (hspace "18px")
                [:p "Aadapt to your audience!"]
                [:div "Civil engineers: "
                 (list (munit/measure-in F [prefix/M N]) " MN")
                 " 🤗 "]
                [:div {:style {:margin-left "18px"}}
                 [:div "rather han " (pr-str F) ]
                 [:div ", or worse: " (munit/measure-in F N) " (is this mass or force or light-years or what???)."]]
                (hspace "18px")
                [:div "General audience: "
                 [:div {:style {:font-size "6px"}} (str/join " " cars-line)]]
                (hspace "32px")
                [:p {:style {:text-align "center"
                             :font-weight "700"
                             :font-size "24px"
                             :color "#a3ffff"}}
                 "The right unit makes the quanitity (magnitude and unit) " [:em "visceral"] "."]])













(proclaim [:div "munit supports any unit! (not just SI)"]
          [:div "Currency work is neat with units."])

(defshow dkk->nok-october-15th
  [1.5696 'NOK {'DKK -1}])

(* [109 'DKK]
   dkk->nok-october-15th)









(bombastically {}
               [:div {:style {:font-size "18px"}}
                "Lesson:"]
               [:span {:style {:color "#a3ffff"
                               :font-weight "700"}}
                "A unit system is base units + consistent computation"]
               (hspace "24px")
               [:div {:style {:font-size "24px" :width "100%"}}
                [:div "A unit is a base unit (symbol)"]
                [:div {:style {:margin-left "18px"}}
                 [:div "... or an exponent map from base unit to exponent"]
                 [:div "... or a number"]
                 [:div "... or a multiplication of units and numbers."]]
                (hspace "18px")
                [:p "Derived units are just units."]])

["Newton is just a number with unit!" N]
["three Newtons is just a number with unit!" [3 N]]
["17 Mega-Newtons is also just a number with unit!" [17 prefix/M N]]

(bombastically {}
               [:div {:style {:font-size "18px"}}
                "Lesson:"]
               [:span {:style {:color "#a3ffff"
                               :font-weight "700"}}
                "To create a unit system,"]
               (hspace "24px")
               [:div {:style {:font-size "24px" :width "100%"}}
                [:ol
                 [:li "Pick your base units"]
                 [:li "Compute consistently (munit can do this part for you.)"]]])

["How much do I make on selling 10 apples?"
 (* [10 'apple]
    [0.2 (/ 'EUR 'apple)])
 "(Given 0.2 Euros per apple.)"]


(bombastically {}
               [:span {:style {:color "#a3ffff"
                               :font-weight "700"}}
                "Plain data for numbers with units"]
               (hspace "48px")
               [:div {:style {:font-size "28px"
                              :width "100%"}}
                [:pre {:style {:color "#6eff6e"}}
                 [:span {:style {:color "grey"}}
                  ";; HTTP (Ring)\n"]
                 (pp-str {:status 200
                          :headers {"Content-Type" "text/html"}
                          :body "<em>Happy Friday!</em>"})]
                (hspace "8px")
                [:pre {:style {:color "#6eff6e"}}
                 [:span {:style {:color "grey"}}
                  ";; HTML (Hiccup)\n"]
                 (pp-str [:em "Happy Friday!"])]]
               (hspace "48px"))







(svg "interactive3.svg")

(proclaim {}
          [:h1 "Munit is now publicly available (MIT)."]
          [:p [:a {:href "https://github.com/teodorlu/munit"}
                 "https://github.com/teodorlu/munit"]]
          [:p "Need help? Mention “munit” on #data-science on Clojurians Slack."
           " I'll help when I can!"])

(proclaim {}
          [:h1 "Acknowledgements"]
          [:ul
           [:li "Gerald Sussman for unit and unit system code in "
            [:a {:href "https://groups.csail.mit.edu/mac/users/gjs/6946/refman.txt"}
             "scmutils"] "."]
           [:li "Sam Ritchie for help understanding Sussman's intent, and his and Colin Smith's work on "
            [:a {:href "https://github.com/mentat-collective/emmy"} "Emmy"]
            " (previously sicmutils)."]
           [:li "Anteo AS for " [:a {:href "https://github.com/anteoas/broch"} "Broch"]
            ", a nice unit library"]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DISCUSSION
;;
;; Question about change of units.
;;
;; Answer: munit provides a rebase operation.
;;
;; You must provide a rebase map from replaced base units to replacement quantities.
;; Example that changes the base unit of length from meters to millimeters:
(munit/rebase [1 'm] {'m [1000 'mm]})
;; There is also measure-in:

;; Comment: Would be nice to have a database of "defined" units built in.
;; Frink may be a good source.
;; https://frinklang.org/
;;
;; Answer: interesting.
;; I want to make those units available.
;; I don't want to maintain all frink-defined units as vars in munit.
;; But maybe a `munit.frink` namsepace could read frink unit definitions and give you munit units.
;; And maybe that should live in its own

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TRAILING HELPERS
(comment
  (require 'clojure.repl.deps)
  (clojure.repl.deps/sync-deps)
  )
