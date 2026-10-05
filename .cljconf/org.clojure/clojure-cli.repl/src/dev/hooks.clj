(ns dev.hooks
  (:require [clojure.pprint :as pprint]))

(defn pprint-values [_default-print]
  (fn [value writer _options]
    (pprint/pprint value writer)))

;; from my old dev repl setup (not a hook but...):
(in-ns 'user)
(require 'clojure.string) ; to satisfy clj-kondo :)
#_{:clj-kondo/ignore [:unused-private-var]}
(defn- uptime []
  (-> (java.lang.management.ManagementFactory/getRuntimeMXBean)
      (.getUptime)
      (java.time.Duration/ofMillis)
      (as-> t (map #(% t) [#(.toHours       ^java.time.Duration %)
                           #(.toMinutesPart ^java.time.Duration %)
                           #(.toSecondsPart ^java.time.Duration %)])
        (let [[h & ms] t]
          (map vector
               (into ((juxt #(long (/ % 24)) #(mod % 24)) h) ms)
               [" days, " " hours, " " minutes, " " seconds"])))
      (->> (filter (comp pos? first))
           (map #(apply str %)))
      (clojure.string/join)))
