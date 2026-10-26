(ns dev.setup
  (:require [dev.portal :as portal]))

(defn install! []
  (println "Clojure" (clojure-version) "\nJVM" (System/getProperty "java.runtime.version"))
  (portal/install!))
