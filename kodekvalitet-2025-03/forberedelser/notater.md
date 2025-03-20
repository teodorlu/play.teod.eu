# Kodekvalitet med Team Mat - 1min
"Hvorfor er det viktig og hvordan jobber vi med det"
## Mål - 3min
Aller først: Hvorfor bryr vi oss om kodekvalitet?
### Kontinuerlig leveranse
Daglig oppdatere prod
Bugfikser på timen
Nye småfeatures: dager
Nye arbeidsflater: uker
Referanser: "Slik leverer du kontinuerlig", DORA, osv
## Kodekvalitet - 5min
### Er kodestil kodekvalitet?
Vi må unngå å skrive om koden mellom forskjellige personlige stiler.
Kodestil er viktig i den grad den lar oss unngå å kaste bort tid på å
omformattere kode.
"Dette er Teodor sin kode"
### Så hva er kodekvalitet?
God kode:
- er så enkel som mulig
- blir ferdig: mindre churn
- blir bedre over tid
- bærer intensjon
Effekten av god kode:
- lett å endre produktet
- produktet kan leveres kontinuerlig
Hva med kodestil?
## Team Mats praksis - 9min
### Mesteparten av vår kodebase er funksjoner som tar inn data og returnerer data
Legge opp til en arkitektur som gir oss så mye reftrans som mulig.
Mulig poeng om lesbarhet
Tall:
- Mengden (LOC) pure kode vs mengden impure kode (illustrert med bilde av
  filtreet, ring ut pure kode)
Eksempel:
- Christian lager et eksempel på pure vs ikke (feks med DOM-node/String)
- Et eksempel fra vår kode (en kommando)
### Alle er fullstack
Delt eierskap
Alle har noen å samarbeide med
Alle jobber i hele kodebasen: får forståelse for alle systemene vi har
Over tid fører denne arbeidsformen til:
Alle har hatt en oppgave der de har fått bryne seg på NATS. Alle kan Datomic.
Alle har brukt designsystemet. Alle vet hvordan de får en knapp i UI-et til å
endre noe i databasen.
Tall:
- Antall fullstack-utviklere: 6 av 6 mulige
Eksempel:
Noe anekdotisk om hvordan en eller flere jobba med noe en uke vs neste
### Parprogrammering
Delt forståelse av kvalitet
Assimilering av kodestil
Innebygget rubber-ducking
Ingen viktige avgjørelser tas alene
Mer effektiv opplæring i kodebasen
Ukentlig syklus av "par"
Tall:
- Antall commits med > 1 committer (overall/dag)
### Tester
Feedback mens man jobber
Sikkerhetsnett for ting som er levert
Tydeliggjør hva forventet oppførsel er
Tillit til at automatisk prodsetting er ok
Tall:
- Antall tester
- Antall byggfeil i Github Actions
### Kontinuerlig leveranse?
Trunk-based development
Commits: små, hyppige, deploybare
Artige tall som illustrerer
- Antall commits per dag
- Antall filer/linjer per commits
Mål: putt noe i prod hver dag SELVOM du ikke er ferdig-
Fordi det letter samarbeid!
## Oppsummering - 2min
### Hvorfor er kodekvalitet viktig?
Levere raskt, ha god feeback-loop, kunne snu oss raskt
### Hvordan jobber vi med kodekvalitet?
Gjennom parprogrammering, testing, kontinuerlig leveranse/jobbe i små
inkrementer, og med en arkitektur som dytter oss i ønsket retning.
