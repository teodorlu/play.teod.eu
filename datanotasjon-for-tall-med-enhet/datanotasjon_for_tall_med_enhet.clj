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
;;
;; - +, -, * og / er vanlige funksjoner, ikke urørlige infiks-operatorer.
;;   Det betyr at kan lage våre egne funksjoner for aritmetikk, og bruke våre i stedet for Clojure sine.
;; - Og vi har interaktiv programmering!
;;   Da kan enhetene hjelpe oss hvert steg på veien.
;;
;; La oss ta personbilene på nytt - denne gangen med enheter.

(ns datanotasjon-for-tall-med-enhet
  (:refer-clojure :exclude [* / + -])
  (:require [munit.prefix :refer [k M]]
            [munit.si :refer [kg m s]]
            [munit.units :refer [* / measure-in]]))

(defmacro defshow
  "definer, så returner det som ble definert

  (bittelitt hjelp for å lage eksempler til å dele med andre)"
  {:clj-kondo/lint-as 'clojure.core/def}
  [sym & body]
  `(do (def ~sym ~@body) ~sym))

(defshow t (* 1000 kg))
;; => [1000 kg]

t
;; => [1000 kg]



(def personbil-masse "Ett tonn per personbil"
  [1 t {'personbiler -1}])

k
;; => 1000
M
;; => 1000000

(def N [kg m {s -2}])
(def kN [k N])
(def MN [M N])

(def g "omtrentlig tyndeaksellerasjon i Norge"
  [9.8 m {s -2}])

(defshow personbil-tyngekraft (* personbil-masse g))
;; => [9800.0 kg m {s -2}]

(def stor-søylelast [17 MN])
(defshow ekvivalente-personbiler (/ stor-søylelast personbil-tyngekraft))
;; => [1734.6938775510205 personbiler]

;; Med g som 9.8 i stedet for 10.0, fikk vi litt flere personbiler - som
;; forventet, litt mindre last per personbil.

;; Men hva betyr det egentlig at enhetene "går opp"?
;; Når du skal svare på et sprøsmål med fysikk, bør du vite hvilken enhet resultatet skal ha.
;; "Antall personbiler" måles ikke i Newton eller meter, antall er enhetsløst.
;;
;; Først, la oss komprimere regnestykket om ekvivalente personbiler.

(/ [17 MN]
   (* [1 t] g))

;; Flott, fremdeles enhetsløst.

;; Hva om vi glemmer å multiplisere med personbil-vekten med tyngdeaksellerasjonen?

(/ [17 MN]
   [1 t])

;; Det ble et tall med enhet!
;; For å være sikker på at et tall er i enheten du forventer, har vi `measure-in`.
;; Putt inn tall og forventet enhet, få ut størrelsen gitt den enheten, eller kast feil.

(measure-in (/ [17 MN]
               (* [1 t] g))
            1)

;; `1` er enhetsløst.

;; Hva om vi putter inn antallet personbiler som ble regnet feil?

(defmacro expect-ex
  "En liten hjelpe-makro for å få se feildata i stedet for å krasje HTML-bygget"
  [& body]
  `(try ~@body (catch Exception e# [(ex-message e#) (ex-data e#)])))

(expect-ex
 (measure-in (/ [17 MN]
                [1 t])
             1))

;; ## Appendix: hjelpekode til å publisere på nett.

(comment
  (require '[nextjournal.clerk :as clerk]
           '[babashka.fs :as fs])

  ;; Clerk build
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)], :out-path "."})
  (clerk/clear-cache!))

(comment
  ;; Clay build steps:
  ;;
  ;;  - M-x clay-start
  ;;  - M-x clay-start
  ;;  - Run REPL code below.
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
