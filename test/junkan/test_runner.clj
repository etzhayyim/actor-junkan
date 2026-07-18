(ns junkan.test-runner
  (:require [clojure.test :as test]
            [junkan.methods.test-analyze]
            [junkan.methods.test-autorun]
            [junkan.methods.test-charter-gates]
            [junkan.methods.test-consumer-culture]
            [junkan.methods.test-country-region-actors]
            [junkan.methods.test-demography]
            [junkan.methods.test-history]
            [junkan.methods.test-junkan-edn]
            [junkan.methods.test-kotoba]
            [junkan.methods.test-query]
            [junkan.methods.test-scorecard]
            [junkan.methods.test-validate]
            [junkan.methods.test-waste-sanitation]
            [junkan.murakumo-test]))

(def suites
  '[junkan.methods.test-analyze junkan.methods.test-autorun
    junkan.methods.test-charter-gates junkan.methods.test-consumer-culture
    junkan.methods.test-country-region-actors junkan.methods.test-demography
    junkan.methods.test-history junkan.methods.test-junkan-edn
    junkan.methods.test-kotoba junkan.methods.test-query
    junkan.methods.test-scorecard junkan.methods.test-validate
    junkan.methods.test-waste-sanitation junkan.murakumo-test])

(defn -main [& _]
  (let [{:keys [fail error]} (apply test/run-tests suites)]
    (shutdown-agents)
    (System/exit (if (zero? (+ fail error)) 0 1))))
