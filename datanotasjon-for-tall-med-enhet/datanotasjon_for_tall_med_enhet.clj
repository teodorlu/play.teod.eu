;; # Datanotasjon for tall med enhet



;; Når byggingeniører spør seg selv om de stoler på en beregning, ser ikke ingeniøren på uttrykk etter hverandre og ser om alt stemmer.
;; Intuisjon spiller en mye større rolle.
;; Hva ville dette tallet blitt hvis vi gjorde en haug med forenklinger?
;; Når alle fornklingene er "i trygg retning", såkalte *konservative* forenklinger, vet vi at vi har en endre grense på kapasiteten til bygget.

;; Håndregningene man gjør er totalt avhengig av enheten på tallene man ser på.
;; En person veier cirka 100 N (Newton).
;; Typiske reelle laster måles i kN (kilo-Newton).
;; MN (mega-Newton) er *mye*.
;; Jeg regnet på en punktlast på 17 MN en gang.
;; Fordi jeg var bekymret for at vi kom til å knuse fjellet vi sto på, delte vi den i to som hver var på under 10 MN.

;; Men hvor mye er 17 MN egentlig?
;; En fin måte å se for seg laster er å regne over i personbiler!
;; En liten personbil veier litt over et tonn.
;; *Vekten* til et tonn på jordens overflate er cirka massen ganger 9.8.
;; Det runder vi av til 10 akkurat her, og vi finner at en personbil veier cirka 10 kN.
;; De 17 MN jeg synes var litt mye tilsvarte altså cirka 1700 personbiler.
;; Det er et passe høyt tårn!

;; Nåvel.
;; Jeg gjorde alt dette i hodet.
;; Hvordan skulle jeg visst at enhetene gikk opp?
;; Ett svar er penn og papir, pluss masse trening.
;; Men jeg synes vi bør ha verktøy som helper oss med sånt.
;; Og for oss utviklere, er enhetssystemer ekstra gøyalt - fordi det går *fort* å lage en liten verktøykasse for det.
;; Akkurat Clojure er svært godt egnet:
;; - infiks-operatorer som + og / er ikke spesielle ting vi ikke får røre
;; - og vi har interaktiv programmering!

;; La oss ta personbilene på nytt - denne gangen med enheter.

(ns datanotasjon-for-tall-med-enhet
  (:refer-clojure :exclude [* / + -])
  (:require [babashka.fs :as fs]
            [clojure.string :as str]
            [munit.prefix :refer [k M]]
            [munit.si :refer [kg m s]]
            [munit.units :refer [* / measure-in]]
            [nextjournal.clerk :as clerk]))

(def t (* 1000 kg))
(def personbil-masse [1 t])

(def N [kg m {s -2}])
(def kN [k N])
(def MN [M N])

(def ^{:doc "omtrentlig tyndeaksellerasjon i Norge"}
  g [9.8 m {s -2}])

(def personbil-tyngekraft (* personbil-masse g))
(def stor-søylelast [17 MN])
(def ekvivalente-personbiler (/ stor-søylelast personbil-tyngekraft))

ekvivalente-personbiler

;; Med g som 9.8 i stedet for 10.0, fikk vi litt flere personbiler - som forventet, litt mindre last per personbil.

(require '[scicloj.clay.v2.api :as clay])

clay/make!

scicloj.clay.v2.api/make!


(comment
  ;; Clerk build
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
  (clerk/clear-cache!))

(comment
  ;; Clay build
  (def base-name (fs/strip-ext (fs/file-name *file*)))
  (def clay-html-file (fs/file "docs" (str base-name ".html")))
  (def clay-extras (fs/file "docs" (str base-name "_files")))

  (do (fs/delete-if-exists "index.html")
      (fs/copy clay-html-file "index.html")

      (fs/delete-tree (str base-name "_files"))
      (fs/copy-tree clay-extras (str base-name "_files")))

  (fs/exists? clay-html-file)
  (fs/exists? clay-extras)

  )
