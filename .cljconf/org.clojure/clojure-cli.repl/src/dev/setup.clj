(ns dev.setup
  (:require [dev.portal :as portal]))

(defn install! []
  (portal/install!))

;; ensure the usual affordances are available in the user namespace:

(in-ns 'user)

(require 'clojure.main)

(apply require clojure.main/repl-requires)
