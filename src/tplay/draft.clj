(ns tplay.draft
  (:require [babashka.fs :as fs]))

(defn find-org-title [s]
  (some-> (re-find #"(?m)^#\+TITLE:\s*(.+)$" s)
          second))

(defn find-slug [file]
  (fs/strip-ext (fs/file-name file)))

(defn assure [msg f & args]
  (when-not (apply f args)
    (throw (ex-info (str "Problem: " msg)
                    {:msg msg
                     :expr (cons f args)}))))

(defn meta-from-draft [file]
  (assure "Draft exists" #'fs/exists? file)
  (let [title (-> file slurp find-org-title)
        slug (find-slug file)]
    {:file file
     :title title
     :slug slug}))

(defn default-meta []
  {:author-url "https://teod.eu"
   :created (str (java.time.LocalDate/now))
   :lang :en
   :readiness :ready-for-comments
   :uuid (str (random-uuid))})

(defn effectuate-undraft [file {:as meta :keys [slug title]}]
  (assure "Required argument"
          #'and file slug title)
  (when (not (fs/exists? slug))
    (fs/create-dir slug))
  (spit (fs/file slug "play.edn") (prn-str meta))
  (fs/copy file (fs/file slug "index.org"))
  (fs/delete file))

(comment
  (mapv str (fs/list-dir "drafts"))
  ;; => ["drafts/consider-inlining.org"
  ;;     "drafts/progressively-enhancing-explanations.org"]

  (do (def file "drafts/consider-inlining.org")
      (effectuate-undraft file (merge (default-meta)
                                      (meta-from-draft file))))

  (fs/delete-tree "consider-inlining")


  (fs/copy "drafts/consider-inlining.org" "2.org")
  (fs/delete "2.org")

  )
