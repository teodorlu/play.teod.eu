;; # Fight Doomscrolling with Babashka
;;
;; Endlessly consuming dopamin packets from companies that sell your attention supposedly rots your brain.
;; What can you do instead?

{:nextjournal.clerk/visibility {:code :hide :result :hide}}

(ns fight-doomscrolling-with-babashka
  (:require
   [lookup.core :as lookup]
   [hickory.core :as hickory]
   [tplay.db :as db]
   [datascript.core :as d]
   [babashka.fs :as fs]
   [nextjournal.clerk :as clerk]))

(do
  (def db (tplay.db/db (tplay.db/load-pages "..")))
  (d/entity db [:page/slug "rich-hickey"])
  (d/entity db [:page/slug "bret-victor"]))

(defn page->href [page]
  (when-let [slug (:page/slug page)]
    (str "/" slug "/")))

(defn page->link [page]
  [:a {:href (page->href page)
       :data-ignore-anchor-click true}
   (:page/title page)])

(def bret-victor (d/entity db [:page/slug "bret-victor"]))

;; ## Discovering Bret Victor's references

{:nextjournal.clerk/visibility {:code :hide :result :show}}
(clerk/html [:p {:style {:margin 0}}
             (page->link bret-victor)
             " has a terriffic list of references at "
             [:a {:href "https://worrydream.com/refs/"} "worrydream.com/refs/"]
             ". Reading a few of those might be worth our time."])

;; In Clojure, we can use `clj-commons/hickory` to parse HTML.
;; Hickory can also query over the HTML with `hickory.select`, but today we'll use `cjohansen/lookup`.

{:nextjournal.clerk/visibility {:code :show :result :show}}

(def refs-hiccup
  (-> "https://worrydream.com/refs/"
      slurp hickory/parse hickory/as-hiccup))

;; We can then find all anchor tags with Lookup.

(lookup/select :a refs-hiccup)

;; Clerk can render the links directly:

(do (defn resolve-bret-ref [anchor]
      (update-in anchor [1 :href] #(str "https://worrydream.com/refs/" %)))
    (clerk/html [:ul {:style {:margin-bottom 0}}
                 (->> (lookup/select :a refs-hiccup)
                      (take 3)
                      (map (fn [a] [:li (resolve-bret-ref a)])))]))

;; If you want to see the whole list, please read Bret Victor's original!
;; It's quite nice.

;; ## Pick a place to start

;; Starting with a random reference let's us get to the content faster.

(clerk/html (resolve-bret-ref (rand-nth (lookup/select :a refs-hiccup))))

;; I like starting at random when I want to avoid the friction of picking.
;;
;; For me, the point of reading these references is to be exposed to new ideas.
;; Having to decide on whether I want to read something based on the title alone requires me to understand the title, which I may not.
;; And if I indeed do not want to read the text, I can always reroll.

;; ## The Babashka script

;; But where does [Babashka] come in?
;;
;; To actually replace doomscrolling with an alternative habit, the alternative habit needs to be _near_.
;; Today, we'll use a CLI script for this.
;; Except for how to open a URL with Clojure, you've already seen all the code.
;;
;; [Babashka]: https://babashka.org/

(clerk/code (slurp "bretroulette.bb"))

;; Want to try?
;; After installing Babashka,
;;
;; 1. Copy the code above into `bretroulette.bb` on your system,
;;
;; 2. Make the script executable,
;;
;;        chmod +x ./bretroulette.bb
;;
;; 3. Run the script,
;;
;;        ./bretroulette.bb
;;
;; 4. Sit back and give that PDF some time.

;; ## Hickory now runs on Babashka - thanks to the inclusion of Jsoup!
;;
;; Hickory didn't run on Babashka before, but it does now!
;; This is because Hickory depends on Jsoup, a Java library for parsing HTML, and Jsoup was added into the Babashka image as of [Babashka version 1.12.195].
;;
;; [Babashka version 1.12.195]: https://github.com/babashka/babashka/blob/master/CHANGELOG.md#112195-2024-11-12

^{:nextjournal.clerk/visibility {:code :hide :result :hide}}
(comment
  ((requiring-resolve 'nextjournal.clerk/serve!) {:browse true})
  ((requiring-resolve 'clojure.repl.deps/sync-deps))
  (clerk/build! {:paths [(fs/file-name *file*)] :out-path "."})
  (clerk/clear-cache!)
  ,)
