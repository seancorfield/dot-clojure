(ns dev.keybindings
  (:require [clojure-cli.repl.api :as api]))

(defn install [reader]
  (api/bind-key reader (api/key-sequence "C-t")
                (api/widget
                  (fn [] (.write (.getBuffer reader) "tap->")))))
