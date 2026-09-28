(ns dev.hooks
  (:require [clojure.pprint :as pprint]))

(defn pprint-values [_default-print]
  (fn [value writer _options]
    (pprint/pprint value writer)))

(defn caught-rephrase [default-caught]
  (if-let [rephrase-caught
           (try
             (requiring-resolve 'org.corfield.rephrase/repl-caught)
             (catch Exception _ (println _)))]
    rephrase-caught
    default-caught))
