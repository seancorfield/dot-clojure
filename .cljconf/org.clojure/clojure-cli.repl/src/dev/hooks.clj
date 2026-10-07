(ns dev.hooks
  (:require [clojure.pprint :as pprint]
            [dev.portal :as portal]))

(defn pprint-values [_default-print]
  (fn [value writer _options]
    (pprint/pprint value writer)))

;; this should move to the "init fn" if/when that is supported:
(portal/install!)
