(ns dev.timing
  (:require
    [nrepl.transport :as transport]))

(defn middleware [handler]
  (fn [{:keys [op transport] :as msg}]
    (if (= op "eval")
      (let [start (System/nanoTime)]
        (handler (assoc msg :transport
                        (reify transport/Transport
                          (recv [_] (transport/recv transport))
                          (recv [_ timeout] (transport/recv transport timeout))
                          (send [_ resp]
                            (transport/send transport
                                            (cond-> resp
                                              (contains? (:status resp) :done)
                                              (assoc :clojure-cli.repl/nanos (- (System/nanoTime) start)))))))))
      (handler msg))))
