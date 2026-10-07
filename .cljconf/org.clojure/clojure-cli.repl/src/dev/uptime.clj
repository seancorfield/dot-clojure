(ns dev.uptime
  (:require [clojure.string :as str]))

(defn uptime []
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
      (str/join)))
