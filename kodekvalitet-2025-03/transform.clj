(ns transform)

(def the-slides

  [[:section [:h1 "Kodekvalitet på Team Mat"]
    [:p "Agenda:"]
    ;; "Hvorfor kodekvalitet er viktig, og hvorfor vi jobber med kodekvalitet"
    [:footer
     [:small
      [:a {:href "https://github.com/chr15m/scittle-tiny-slides"
           :target "_BLANK"}
       "Presentasjonen er laget med Scittle Tiny Slides"]]]]

   [:section [:h1 "1. Mål: Kontinuerlig leveranse"]
    ;; Christian, 2 min
    ;; Aller først: Hvorfor bryr vi oss om kodekvalitet?
    ]

   [:section [:h1 "2. “Kodekvalitet”?"]
    ;; Teodor - 4 min
    [:em "hva snakker vi om når vi snakker om kodekvalitet?"]
    ]
   [:section [:h1 "2.1 Våre prinsipper for god kode"]
    "et forsøk på å være konkret:"
    ;; med eksempler
    [:ul
     [:li "er så enkel som mulig"
      ;; gitt to måter å løse samme problem, er den mer direkte løsningen bedre
      ]
     [:li "blir ferdig: mindre churn"]
     [:li "blir bedre over tid"]
     [:li "bærer intensjon"
      ;; svarer på /hvorfor/
      ]]]
   [:section [:h1 "2.2 Effekten av god kode"]
    [:ul
     [:li "lett å endre produktet"]
     [:li "produktet kan leveres kontinuerlig"]
     [:li "koden er trivelig å jobbe med"]
     ]]
   [:section [:h1 "2.3 Kodestil og kodekvalitet"]
    [:p "Vi må unngå å skrive om koden mellom forskjellige personlige stiler.
Kodestil er viktig i den grad den lar oss unngå å kaste bort tid på å
omformattere kode."]
    [:p "Unngå: " (quote-str "Dette er Teodor sin kode")]]

   [:section [:h1 "3. Team Mats praksis - 10 min"]]
   [:section [:h1 "3.1 Kontinuerlig leveranse - Christian"]]
   [:section [:h1 "3.2 Parprogrammering - Teodor"]]
   [:section [:h1 "3.3 Fullstack - Christian"]]
   [:section [:h1 "3.4 Tester - Teodor"]]
   [:section [:h1 "3.4 Rene funksjoner - Christian"]]

   [:section [:h1 "4 Oppsummering - Teodor - 2 min"]]
   [:section [:h1 "4.1 Hvorfor er kodekvalitet viktig?"]
    [:ul
     [:li "levere raskt"]
     [:li "ha god feedback-loop"]
     [:li "kunne reagere raskt på produktbehov (\"smidig\")"]
     [:li "bidrar til trivsel på jobb!"]]]
   [:section [:h1 "4.2 Hvordan jobber vi med kodekvalitet?"]
    [:ul
     [:li "parprogrammering"]
     [:li "testing"]
     [:li "kontinuerlig leveranse / små inkrementer"]
     [:li "arkitektur som dytter oss i rett retning"]
     ]
    ]
   [:section [:h1 "4.3 Hva er god kode?"]
    [:ul
     [:li "er så enkel som mulig"]
     [:li "blir ferdig: mindre churn"]
     [:li "blir bedre over tid"]
     [:li "bærer intensjon"]
     ]]

     ])
