(ns dev.heap
  (:require
    [clojure-cli.repl.middleware :as mw]))

(def middleware
  (mw/assoc-response
    (fn []
      (let [rt (Runtime/getRuntime)]
        {:clojure-cli.repl/heap-used (- (.totalMemory rt) (.freeMemory rt))
         :clojure-cli.repl/heap-max (.maxMemory rt)}))))
