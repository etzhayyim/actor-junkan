(require '[clojure.edn :as edn]
         '[clojure.java.io :as io]
         '[kotoba.lang.text :as str])
(def root (.getCanonicalFile (io/file ".")))
(def files (file-seq root))
(def relative #(.toString (.relativize (.toPath root) (.toPath %))))
(doseq [f files :when (and (.isFile f) (str/ends-with? (.getName f) ".edn"))]
  (try (edn/read-string (slurp f))
       (catch Exception e (throw (ex-info "invalid EDN" {:file (relative f)} e)))))
(let [deprecated (for [f files :when (.isFile f) :let [p (relative f)]
                       :when (or (re-find #"\.(go|py|sh)$" p)
                                 (re-find #"(^|/)requirements[^/]*\.txt$" p))] p)
      misplaced (for [f files :when (.isFile f) :let [p (relative f)]
                      :when (and (re-find #"\.(json|jsonld)$" p)
                                 (not (str/starts-with? p "wire/"))
                                 (not= p ".well-known/did.json"))] p)]
  (when (seq deprecated) (throw (ex-info "deprecated artifacts" {:files deprecated})))
  (when (seq misplaced) (throw (ex-info "JSON outside wire" {:files misplaced}))))
(println "audit: ok")
