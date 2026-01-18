(ns tplay.rss
  "I don't need no makefile
  I don't need no task
  I just need a REPL eval
  For now

  🎵 doin' things that don't scale 🎵
  🎵 for texture! 🎵

  What brought us here?
  I stumbled over https://aartaka.me/pidgin.html by Artyom Bologov yesterday.
  I liked what I saw so much that I had to send an email!
  Later the same evening, I got a super-pleasant reply.
  He had read my poetry 😂
  And asked for an RSS feeed.

  I *have* been RSS curious, so why not.

  But I have a problem.
  I have piles of drafts live on the web, that's not meant for anybody.
  Additionally, I mix Enlish and Norwegian.
  My solution: tailored RSS feeds.
  I'll start with english-ready-for-comments.rss.xml"
  (:require
   [clojure.data.xml :as xml]
   [clojure.edn :as edn])
  (:import
   (java.time Instant)))

(def author-url->author
  {"https://teod.eu" "Teodor Heggelund (https://teod.eu)"})

(defn doc->item [{:as doc
                  :keys [title author-url slug uuid
                         created]}]
  [:item
   [:title title]
   [:author (get author-url->author author-url author-url)]
   [:link (str "https://play.teod.eu/" slug "/")]
   [:guid uuid]
   [:pubDate created]])

(defn english-ready-for-comments [docs]
  ;; <!-- inspired by Artyom Bologov (https://aartaka.me/rss.xml). Thank you! -->
  {:file "english-ready-for-comments.rss.xml"
   :sexp
   [:rss {:version "2.0"}
    (into
     [:channel
      [:title "Towards an iterated game | English"]
      [:link "https://play.teod.eu/"]
      [:guid "https://play.teod.eu/english-ready-for-comments"]
      [:pubDate (str (Instant/now))]
      [:description "Bring ideas to life. Discuss, sharpen, play. Minimize distance between intent and reality."]
      [:docs "https://www.rssboard.org/rss-specification"]]
     (map doc->item docs))]})

(def english? (comp #{:en} :lang))
(def ready-for-comments? (comp #{:ready-for-comments} :readiness))

(defn find-english-ready-for-coments [index]
  (->> index
       (filter
               #(and (english? %) (ready-for-comments? %)))
       (sort-by :created)
       (reverse)))

(defn persist! [{:keys [file sexp]}]
  (spit file (xml/indent-str (xml/sexp-as-element sexp))))

(comment
  (def the-index (edn/read-string (slurp "index/big.edn")))
  (-> the-index
      find-english-ready-for-coments    ; something is off
      english-ready-for-comments        ; these two names confuse each other!
      persist!)

  )
