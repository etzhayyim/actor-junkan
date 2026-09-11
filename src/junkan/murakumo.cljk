(ns junkan.murakumo
  "Pure cljc actor boundary generated from manifest migration scaffold."
  (:require [kotoba.lang.text :as str]))

(def actor-did
  "did:web:junkan.etzhayyim.com")

(def common-gates
  [:council-charter-attestation
   :no-platform-held-key-baseline
   :no-probing-baseline
   :murakumo-only-inference-baseline
   :did-primary-baseline
   :append-only-gate-baseline
   :kotoba-only-substrate-baseline])

(defn collection
  [name]
  (str "com.etzhayyim.junkan." name))

(def cell-specs {
  :junkan_ingest {:legacy-cell "junkan-ingest"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_ingest")]
     :required-gates common-gates
     :trigger "manifest cell junkan_ingest"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_stock_estimator {:legacy-cell "junkan-stock-estimator"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_stock_estimator")]
     :required-gates common-gates
     :trigger "manifest cell junkan_stock_estimator"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_flow_inference {:legacy-cell "junkan-flow-inference"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_flow_inference")]
     :required-gates common-gates
     :trigger "manifest cell junkan_flow_inference"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_cld_builder {:legacy-cell "junkan-cld-builder"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_cld_builder")]
     :required-gates common-gates
     :trigger "manifest cell junkan_cld_builder"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_loop_classifier {:legacy-cell "junkan-loop-classifier"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_loop_classifier")]
     :required-gates common-gates
     :trigger "manifest cell junkan_loop_classifier"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_leverage_finder {:legacy-cell "junkan-leverage-finder"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_leverage_finder")]
     :required-gates common-gates
     :trigger "manifest cell junkan_leverage_finder"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_finding_emitter {:legacy-cell "junkan-finding-emitter"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_finding_emitter")]
     :required-gates common-gates
     :trigger "manifest cell junkan_finding_emitter"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
  :junkan_silen_review {:legacy-cell "junkan-silen-review"
     :phase :event
     :murakumo-node "reuben"
     :collections [(collection "junkan_silen_review")]
     :required-gates common-gates
     :trigger "manifest cell junkan_silen_review"
     :ceiling "Manifest-driven migration scaffold; explicit execution stays in runtime methods"}
})

(defn safe-rkey
  [s]
  (let [clean (-> (str s)
                  (str/replace #"^did:web:" "")
                  (str/replace #"[^A-Za-z0-9._~-]" "-"))]
    (if (str/blank? clean) "unknown" clean)))

(defn gate-value
  [attestations gate]
  (or (get attestations gate)
      (get attestations (name gate))
      (when (set? attestations) (attestations gate))
      (when (set? attestations) (attestations (name gate)))))

(defn missing-gates
  [spec attestations]
  (->> (:required-gates spec)
       (remove #(boolean (gate-value attestations %)))
       vec))

(defn put-record-effect
  [collection rkey record]
  {:op :mst/put-record
   :actor actor-did
   :collection collection
   :rkey rkey
   :record record})

(defn records-for
  [spec {:keys [records record computed-at request-id]
         :as input}]
  (let [input-records (cond
                        (map? records) records
                        (some? record) {0 record}
                        :else {})
        base {:actorDid actor-did
              :computedAt computed-at
              :legacyCell (:legacy-cell spec)
              :phase (:phase spec)
              :requestId request-id
              :actorBoundary "cljc-migration-scaffold"
              :scaffold true
              :constitutionalStatus "attested-plan"}]
    (map-indexed
     (fn [idx coll]
       (let [record* (merge {:$type coll}
                            base
                            (or (get input-records coll)
                                (get input-records idx)
                                {}))
             rkey (safe-rkey (or (:rkey record*)
                                 (get record* "rkey")
                                 (:tid record*)
                                 request-id
                                 (str (:legacy-cell spec) "-" idx)))]
         {:collection coll
          :record record*
          :rkey rkey}))
     (:collections spec))))

(defn cell-plan
  [cell-key {:keys [attestations] :as input}]
  (let [spec (get cell-specs cell-key)]
    (when-not spec
      (throw (ex-info "unknown cell" {:cell cell-key})))
    (let [missing (missing-gates spec attestations)]
      (merge
       {:cell cell-key
        :legacy-cell (:legacy-cell spec)
        :actor actor-did
        :phase (:phase spec)
        :murakumo-node (:murakumo-node spec)
        :trigger (:trigger spec)
        :ceiling (:ceiling spec)
        :required-gates (:required-gates spec)
        :missing-gates missing}
       (if (seq missing)
         {:status :blocked
          :effects []}
         (let [planned-records (records-for spec input)]
           {:status :ready
            :records (vec planned-records)
            :effects (mapv (fn [{:keys [collection record rkey]}]
                             (put-record-effect collection rkey record))
                           planned-records)}))))))

(defn all-cell-plans
  [input]
  (into {}
        (map (fn [cell-key] [cell-key (cell-plan cell-key input)]))
        (keys cell-specs)))
