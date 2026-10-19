(ns dev.hooks
  (:require [clojure.pprint :as pprint]))

(defn pprint-values [_default-print]
  (fn [value writer _options]
    (pprint/pprint value writer)))
