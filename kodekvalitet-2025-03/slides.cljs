(ns slides
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [reagent.dom :as rdom]))

(defn quote-str [s] (str "“" s "”"))
(defn explicit-link [href] [:a {:href href} href])

(defn kvalitet-brahet [konklusjon-synlighet]
  [:section
   [:h1 "Kvalitet som " (quote-str "brahet")]

   [:img {:src "kvalitet-ord.png"}]
   [:img {:src "kvalitet-ord-brahet.png"}]

   (comment
     [:p [:strong "quality"] " (noun)"]
     [:p [:em "level of excellence"]]
     [:blockquote "This school is well-known for having teachers of high quality."]
     [:blockquote "Quality of life is usually determined by health, education, and income."])

   [:br] [:br]
   [:p "men hvordan kan vi " [:em "definere"] " god kode?"]
   [:p {:style {:opacity konklusjon-synlighet}}
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    "🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔"
    " (det var vanskelig, gitt)"
    ]])

(defn kvalitet-karakteristikk [konklusjon-synlighet]
  [:section [:h1 "Kvalitet som " (quote-str "distinkt karakteristikk")]

   [:img {:src "kvalitet-ord.png"}]
   [:img {:src "kvalitet-ord-karakteristikk.png"}]

   (comment
     [:p [:em "Something that differentiates a thing or person"]]
     [:blockquote "One of the qualities of pure iron is that it does not rust easily."]
     [:blockquote "While being impulsive can be great for artists, it is not a desirable quality for engineers."]
     [:blockquote "Security, stability, and efficiency are good qualities of an operating system."])

   [:br] [:br]
   [:p "er dette nyttigere?"]
   [:p {:style {:opacity konklusjon-synlighet}}
    "Det er i alle fall konkret!"]])

(defn slides []
  [:<>
   [:section [:h1 "Kodekvalitet på Team Mat"]
    ;; "Hvorfor kodekvalitet er viktig, og hvorfor vi jobber med kodekvalitet"
    ]

   [:section [:h1 "1. Mål?"]]

   [:section [:h1 "1. Mål: Kontinuerlig leveranse"]]

   [:section
    [:img {:src "kontinuerlig.png"}]
    [:p [:a {:href "https://parenteser.mattilsynet.io/hvordan-levere-kontinuerlig/"}
         "https://parenteser.mattilsynet.io/hvordan-levere-kontinuerlig/"]]]

   [:section [:h1 "2. “Kodekvalitet”?"]
    ;; Teodor - 4 min
    [:em "hva snakker vi om når vi snakker om kodekvalitet?"]
    ]

   [:section
    [:em "hvorfor definere kodekvalitet?"]
    [:br]
    [:h1 "Delt forståelse av kodekvalitet på teamet gir fundament for samarbeid i kodebasen"]]

   [:section
    [:em "ok, la oss forsøke å definere kodekvalitet."]]

   (kvalitet-brahet 0)
   (kvalitet-brahet 1)

   (kvalitet-karakteristikk 0)
   (kvalitet-karakteristikk 1)

   [:section [:h1 "2.1 Våre karakteristikker på god kode"]
    ]

   [:section [:h1 "2.1 Våre karakteristikker på god kode"]
    [:p "Vi er enige om at:"]
    ;; med eksempler
    [:ul
     [:li "God kode er så enkel som mulig" #_ "den direkte løsningen er ofte bedre"]
    ]]

   [:section [:h1 "2.1 Våre karakteristikker på god kode"]
    [:p "Vi er enige om at:"]
    ;; med eksempler
    [:ul
     [:li "God kode er så enkel som mulig" #_ "den direkte løsningen er ofte bedre"]
     [:li "God kode blir ferdig: mindre churn"]
    ]]

   [:section [:h1 "2.1 Våre karakteristikker på god kode"]
    [:p "Vi er enige om at:"]
    ;; med eksempler
    [:ul
     [:li "God kode er så enkel som mulig" #_ "den direkte løsningen er ofte bedre"]
     [:li "God kode blir ferdig: mindre churn"]
     [:li "God kode blir bedre over tid"]
]]

   [:section [:h1 "2.1 Våre karakteristikker på god kode"]
    [:p "Vi er enige om at:"]
    ;; med eksempler
    [:ul
     [:li "God kode er så enkel som mulig" #_ "den direkte løsningen er ofte bedre"]
     [:li "God kode blir ferdig: mindre churn"]
     [:li "God kode blir bedre over tid"]
     [:li "God kode bærer intensjon" #_ "svarer på hvorfor"]]]

   [:section [:h1 "2.2 Opplevd kodekvalitet"]
    [:p "selv om vi ikke klarer å si hva god kode " [:em "er i sin essens"]
     ", kan vi si noe om hvordan vi opplever å jobbe med god kode:"]
]

   [:section [:h1 "2.2 Opplevd kodekvalitet"]
    [:p "selv om vi ikke klarer å si hva god kode " [:em "er i sin essens"]
     ", kan vi si noe om hvordan vi opplever å jobbe med god kode:"]
    [:ul
     [:li "det er lett å endre produktet"]
     ]

    ]

   [:section [:h1 "2.2 Opplevd kodekvalitet"]
    [:p "selv om vi ikke klarer å si hva god kode " [:em "er i sin essens"]
     ", kan vi si noe om hvordan vi opplever å jobbe med god kode:"]
    [:ul
     [:li "det er lett å endre produktet"]
     [:li "produktet kan leveres kontinuerlig"]

     ]
]

   [:section [:h1 "2.2 Opplevd kodekvalitet"]
    [:p "selv om vi ikke klarer å si hva god kode " [:em "er i sin essens"]
     ", kan vi si noe om hvordan vi opplever å jobbe med god kode:"]
    [:ul
     [:li "det er lett å endre produktet"]
     [:li "produktet kan leveres kontinuerlig"]
     [:li "koden er trivelig å jobbe med"]
     ]
    [:br] [:br] [:br] [:br]
    [:p "(stjålet fra design (form følger funksjon) og filosofi (fenomenologi))"]]

   [:section [:h1 "2.3 Kodekvalitet 🌀 Praksis"]
    [:ul
     [:li "Hvordan påvirker kodekvalitet vår praksis?"]
     [:li "Hvordan påvirker vår praksis kodekvalitet?"]
     ]
    ]

   [:section [:h1 "2.4 Kodestil og kodekvalitet"]
    [:p "ikke helt samme ting!"]
    [:ul
     [:li "kodestil er den konkrete overflaten, kodekvaliten ligger dypere"]
     [:li "delt kodestil unngår omformatering av kode."]]]

   [:section [:h1 "3. Team Mats praksis"]]

   [:section [:h1 "3.1 Kontinuerlig leveranse" [:br] " "]]
   [:section [:h1 "3.1 Kontinuerlig leveranse" [:br] "(ja, igjen)"]]
   [:section [:h1 "Trunk-based development"]]
   [:section [:h1 "Trunk-based development"]
    [:img {:src "trunk-based-development.png"}]]
   [:section [:img {:src "pull-requests.png"}]]
   [:section [:img {:src "pull-requests-hl.png"}]]
   [:section [:h1 "Små, hyppige commits"]]
   [:section [:img {:src "hyppige-commits-1.png"}]]
   [:section [:img {:src "hyppige-commits-2.png"}]]
   [:section [:img {:src "antall-commits-per-dag-2025.png"}]]
   [:section [:img {:src "linjer-per-commit-90p.png"}]]

   [:section [:h1 "3.2 Parprogrammering"] #_ "Teodor"
    "🤗"]
   [:section [:h1 "Fra en meetup i Mai 2024"]
    [:em "Teodor, Christian og Peter Strömberg parprogrammerer"]]
   [:section [:h1 "Kodekvaliteter 🌀 parprogrammering"]
    ;; CSS-en for tabeller imponerte ikke, jeg går for <pre>.
    [:pre {:style {:text-align "start"}}
     (str/trim "
kvalitet                     | under parprogrammering
-----------------------------+--------------------------------
God kode bærer intensjon     | arena for å spørre hvorfor
God kode blir bedre over tid | husker bedre intensjonen sammen
")]
    ]
   [:section [:h1 "Opplevelsen av parprogrammering"]
    [:ul
     [:li (quote-str "den triveligste delen av jobben")]
     [:li "fantastisk læringsarena"]
     [:li "supert å diskutere problemer i konteksten der man kan fikse de samme problemene"]
     ]]

   [:section [:h1 "3.3 Fullstack"]
    "👨‍💻👩‍💻📲🖥️📦🌐"]

   [:section [:h1 "3.4 Tester"] #_ "Teodor"]
   [:section [:h1 "Fra en meetup i Mai 2024"]
    [:p [:em "bruk tester til å drive implementasjonen, sammen!"]]
    [:p [:em "tester gjør intensjonen eksplisitt - dette er neste steg!"]]
    ]
   [:section [:h1 "Kodekvaliteter 🌀 tester"]
    [:pre {:style {:text-align "start"}}
     (str/trim "
kvalitet                       | i kontekst av tester
-------------------------------+----------------------------------------
God kode er så enkel som mulig | kan forenkle (refaktorere) under tester
God kode bærer intensjon       | testene gir forventet oppførsel
")]
    ]
   [:section [:h1 "Testdreven arbeidsflyt"]
    [:ul
     [:li "Veltestet kode er trygt å komme tilbake til"]
     [:li "Testene gir fin feedback når man bygger ut koden"]
     [:li (quote-str "skriv ny, rød test og send tastaturet til Christian")
      " er en flott måte å gjøre parprogrammering"]]]

   [:section [:h1 "3.5 Rene funksjoner"]]
   [:section [:img {:src "add-tax-1.png"}]]
   [:section [:img {:src "add-tax-2.png"}]]
   [:section [:img {:src "add-tax-3.png"}]]
   [:section [:img {:src "add-tax-4.png"}]]
   [:section [:img {:src "add-to-cart-1.png"}]]
   [:section [:img {:src "add-to-cart-2.png"}]]
   [:section [:img {:src "add-to-cart-3.png"}]]
   [:section [:img {:src "add-to-cart-4.png"}]]
   [:section [:img {:src "dom-2.png"}]]
   [:section [:img {:src "dom-3.png"}]]
   [:section [:img {:src "gressvik.png"}]]
   [:section [:img {:src "fredrikstad.png"}]]
   [:section [:img {:src "tur-tester.png"}]]
   [:section [:img {:src "fcis.png"}]]
   [:section [:img {:src "cloc.png"}]]
   [:section [:img {:src "cloc-hl.png"}]]
   [:section [:img {:src "cloc-fc.png"}]]
   [:section [:img {:src "cloc-fc-hl.png"}]]
   [:section [:img {:src "cloc-fc-pct.png"}]]

   [:section [:em "4 Oppsummering"] #_ "Teodor, 2 min"
    ;; forslag
    [:h1 "kontinuerlig leveranse, parprogrammering og tester"]
    [:p "sikrer"]
    [:h1 "arbeidsglede, god kode og gode produkter"]
    [:p "for oss som jobber på Team mat."]
    [:footer
     [:small {:style {:font-size "20px"}}
      "Presentasjonen er laget med "
      [:a {:href "https://github.com/chr15m/scittle-tiny-slides"
           :target "_BLANK"}
       "Scittle Tiny Slides"]
      ", av Chris McCormick"]]
    ]

   #_#_#_#_
   ;; Forslag til ny konklusjon
   [:section [:h1 "Teodors oppsummering"]
    [:p "kontinuerlig leveranse, parprogrammering og tester sikrer"
     " arbeidsglede, god kode og gode produkter"]
    ]

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
   [:section [:h1 "4.3 Hvilke kvaliteter har god kode?"]
    [:p "(ifølge Team Mat)"]
    [:ul
     [:li "er så enkel som mulig"]
     [:li "blir ferdig: mindre churn"]
     [:li "blir bedre over tid"]
     [:li "bærer intensjon"]
     ]

    ]

   #_
   [:section
    [:h1 "Eksempel på bilde"]
    [:img {:src "https://avatars.githubusercontent.com/u/34045?v=4"}]]
   ])

; *** implementation details *** ;

(defonce state (r/atom nil)) ; re-initialized below

(defn get-slide-count []
  (aget
    (js/document.querySelectorAll "section")
    "length"))

(defn move-slide! [state ev dir-fn]
  (.preventDefault ev)
  (swap! state update :slide dir-fn))

(defn clickable? [ev]
  (let [tag-name (.toLowerCase (aget ev "target" "tagName"))]
    (contains? #{"button" "label" "select"
                 "textarea" "input" "a"
                 "details" "summary"}
               tag-name)))

(defn keydown
  [ev]
  (when (not (clickable? ev))
    (let [k (aget ev "key")]
      (cond
        (contains? #{"ArrowLeft" "ArrowUp" "PageUp"} k)
        (move-slide! state ev dec)
        (contains? #{"ArrowRight" "ArrowDown" "PageDown" "Enter" " "} k)
        (move-slide! state ev inc)
        (contains? #{"Escape" "Home" "h"} k)
        (swap! state assoc :slide 0)
        (contains? #{"End"} k)
        (swap! state assoc :slide (dec (get-slide-count)))))))

(defn component:show-slide [state]
  [:style (str "section:nth-child("
               (inc (mod (:slide @state) (get-slide-count)))
               ") { display: block; }")])

(defn component:touch-ui [state]
  [:div#touch-ui
   {:style {:opacity
            (if (:touch-ui @state) 0 1)}}
   [:div {:on-click #(move-slide! state % dec)} "⟪"]
   [:div {:on-click #(move-slide! state % inc)} "⟫"]])

(defn component:slide-viewer [state]
  [:<>
   [:main {:on-click
           #(when (not (clickable? %))
              (swap! state update :touch-ui not))}
    [slides]]
   [component:show-slide state]
   [component:touch-ui state]])

(rdom/render
  [component:slide-viewer state]
  (.getElementById js/document "app"))

(defonce setup
  (do
    (aset js/window "onkeydown" #(keydown %))
    ; trigger a second render so we get the sections count
    (swap! state assoc :slide 0 :touch-ui true)))
