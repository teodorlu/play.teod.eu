(ns tplay.templater
  "Originally matnyttig.templater, from github.com/Mattilsynet/matnyttig

  En verktøykasse for filbaserte maler

  En BUNDLE er et map fra filsti til filinnhold (som string).

  Templater lar deg laste bundles, og skrive bundles tilbake til disk. Ved
  skriving til disk, får du en rapport tilbake, feks:

      ([:created \"newpage.clj\"] [:updated \"the_pages.clj\"])"
  (:require [babashka.fs :as fs]
            [clojure.walk :as walk]
            [clojure.string :as str]))

(defn guard [pred f]
  #(cond-> % (pred %) f))

;; -----------------------------------------------------------------------------
;;           TRANSFORM BUNDLE
;; -----------------------------------------------------------------------------

(defn interpolate-data [bundle formatters]
  (walk/postwalk (guard #(and (vector? %)
                              (contains? formatters (first %)))
                        (fn [[k & args]]
                          (apply (formatters k) args)))
                 bundle))

(defn interpolate-strings [bundle replacements]
  (walk/postwalk (guard string?
                        (fn [s]
                          (reduce (fn [s [match replacement]]
                                    (str/replace s match replacement))
                                  s
                                  replacements)))
                 bundle))

;; -----------------------------------------------------------------------------
;;           FROM DISK
;; -----------------------------------------------------------------------------

(defn folder->bundle
  ([root] (folder->bundle root root))
  ([file-root bundle-root]
   (into (sorted-map)
         (comp (remove fs/directory?)
               (filter fs/exists?)
               (map (juxt #(str (fs/relativize bundle-root %))
                          slurp)))
         (file-seq (fs/file file-root)))))

(defn folder->fileset
  ([root] (folder->fileset root root))
  ([file-root bundle-root]
   (into (sorted-set)
         (comp (remove fs/directory?)
               (filter fs/exists?)
               (map #(str (fs/relativize bundle-root %))))
         (file-seq (fs/file file-root)))))

(comment
  (folder->bundle "templates/new-page")
  (folder->fileset "templates/new-page")

  )

(defn file->bundle
  ([file-path]
   (file->bundle file-path (fs/parent file-path)))
  ([file-path bundle-root]
   {(str (fs/relativize bundle-root file-path))
    (slurp file-path)}))

(defn files->bundle [file-paths bundle-root]
  (into (sorted-map)
        (map (fn [path]
               [(str (fs/relativize bundle-root path))
                (slurp path)]))
        file-paths))

;; -----------------------------------------------------------------------------
;;           TO DISK
;; -----------------------------------------------------------------------------

(defn upsert-file [file-path content]
  (fs/create-dirs (fs/parent file-path))
  (if (fs/exists? file-path)
    (if (= content (slurp file-path))
      [:unchanged (str file-path)]
      (do (spit file-path content)
          [:updated (str file-path)]))
    (do (spit (str file-path) content)
        [:created (str file-path)])))

(defn write [bundle root]
  (when-not (fs/directory? root)
    (throw (ex-info "Cannot write bundle when root does not exist"
                    {:root root})))
  (doall (map (fn [[path content]]
                (upsert-file (fs/file root path) content))
              bundle)))

(defn empty-directory? [f]
  (and (fs/directory? f)
       (empty? (fs/list-dir f))))

(defn purge-empty-upwards
  "Delete dir if empty, and all empty parents of f"
  [dir]
  (let [log (atom [])]
    (loop [current-dir dir]
      (when (empty-directory? current-dir)
        (fs/delete current-dir)
        (swap! log conj [:deleted-directory (str current-dir)])
        (recur (fs/parent current-dir))))
    @log))

(defn delete-file [f]
  (let [did-delete? (fs/delete-if-exists f)]
    [(if did-delete? :deleted :already-deleted) (str f)]))

(defn purge-fileset [fileset]
  (let [delete-report (doall (map delete-file fileset))
        purge-report (doall (->> fileset
                                 (map fs/parent)
                                 (mapcat purge-empty-upwards)))]
    (concat delete-report purge-report)))

(defn purge-bundle [bundle]
  (purge-fileset (keys bundle)))

;; -----------------------------------------------------------------------------
;;           RENAME HELPERS
;; -----------------------------------------------------------------------------

(def norsk->ascii
  {"æ" "ae"
   "ø" "o"
   "å" "a"
   "Æ" "Ae"
   "Ø" "O"
   "Å" "A"})

(defn replace-all [s m]
  (reduce (fn [s [fra til]]
            (str/replace s fra til))
          s
          m))

(def søme-name->some-name #(replace-all % norsk->ascii))
(def søme-name->some_name #(replace-all % (assoc norsk->ascii "-" "_")))
(def søme-name->SømeName #(->> (str/split % #"-") (map str/capitalize) (str/join)))
(def søme-name->SømeSPACEName #(->> (str/split % #"-") (map str/capitalize) (str/join " ")))

(defn create-rename-map [& name-mappings]
  (assert (and (< 0 (count name-mappings))
               (= 0 (mod (count name-mappings) 2)))
          "Name mappings must be one or more pairs of names")

  (loop [[old-name new-name & more-names] name-mappings
         rename-map (sorted-map)]
    (let [rename-map (->> [;; Ved overlappende nøkler gjelder siste i lista
                           identity
                           søme-name->SømeName
                           søme-name->SømeSPACEName
                           søme-name->some-name
                           ;; Ved tvetydig input, velg classpath-kompatibel identifikator
                           søme-name->some_name]
                          (map (juxt #(% old-name) #(% new-name)))
                          (into rename-map))]
      (if more-names
        (recur more-names rename-map)
        rename-map))))


;; -----------------------------------------------------------------------------
;;           NAMESPACE HELPERS
;; -----------------------------------------------------------------------------

(defn compare-first [a b]
  (compare (first a) (first b)))

(defn format-require [sym]
  (let [namespace (or (namespace sym) sym)]
    (-> namespace symbol vector)))

(defn concat-requires [namespaces & extra-requires]
  (->> (map format-require namespaces)
       (concat extra-requires)
       (into (sorted-set-by compare-first))))

(defn create-requires [namespaces & extra-requires]
  (->> (apply concat-requires namespaces extra-requires)
       (str/join "\n            ")))
