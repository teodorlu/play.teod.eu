(ns tplay.rss-test
  (:require [clojure.test :refer [deftest is]]
            [tplay.rss :as rss]))

(deftest doc->item
  (is (= (rss/doc->item {:slug "effort-asymmetry",
                         :file "drafts/effort-asymmetry.org",
                         :page/uuid "ae2b3bcd-87d2-4594-bf70-213afa5ebf68",
                         :created "2026-01-10",
                         :readiness :ready-for-comments,
                         :title "Effort asymmetry on the web",
                         :lang :en,
                         :page/slug "effort-asymmetry",
                         :author-url "https://teod.eu",
                         :uuid "ae2b3bcd-87d2-4594-bf70-213afa5ebf68"})
         [:item
          [:title "Effort asymmetry on the web"]
          [:author "Teodor Heggelund (https://teod.eu)"]
          [:link "https://play.teod.eu/effort-asymmetry/"]
          [:guid "ae2b3bcd-87d2-4594-bf70-213afa5ebf68"]
          [:pubDate "2026-01-10"]
          ])))

;;    <item>
;;      <title>Easy (Horizontal Scrollbar) Fixes for Your Blog CSS</title>
;;      <author>rss@aartaka.me (Artyom Bologov)</author>
;;      <link>https://aartaka.me/easy-fixes/</link>
;;      <guid>https://aartaka.me/easy-fixes/</guid>
;;      <pubDate>Mon, 5 Jan 2026 21:40:00 +0400</pubDate>
;;      <description>
;;        &lt;a href=https://aartaka.me/easy-fixes&gt;Read on the website: &lt;/a&gt;
;;        There are narrow screen CSS problems I often email people because of. These three fixes should be enough for most.
;;      </description>
;;    </item>
