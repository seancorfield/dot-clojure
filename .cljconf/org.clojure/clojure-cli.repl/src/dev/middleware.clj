(ns dev.middleware
  (:require [nrepl.middleware :refer [set-descriptor!]]
            [nrepl.middleware.caught :as caught]
            [nrepl.middleware.print :as print]))

(def portal-mw
  "Returns a vector of Portal middleware to be composed, if available."
  (try ; happens to be in the correct order for composition:
    (deref (requiring-resolve 'portal.nrepl/middleware))
    (catch Exception _)))

(defn portal
  "If Portal is on the classpath, wrap the handler with Portal middleware.
   In addition, re-enable REPL evaluations to be routed through Portal."
  [handler]
  (if portal-mw
    (let [wrapped (reduce (fn [h m] ((resolve m) h))
                          handler
                          portal-mw)]
      (fn [msg]
        (wrapped (cond-> msg
                   ;; if we have code but no file, assume it's from the REPL:
                   (and (some? (:code msg))
                        (nil?  (:file msg)))
                   (assoc :file "REPL")))))
    handler))

(when portal-mw
  (set-descriptor! #'portal
                   {:requires #{"clone"
                                #'print/wrap-print
                                #'caught/wrap-caught}
                    :expects #{"eval" "load-file"}
                    :handles {}}))

(def rephrase-mw
  "Returns the rephrase middleware if available."
  (try
    (requiring-resolve 'org.corfield.rephrase.nrepl/wrap-rephrase)
    (catch Exception _)))

(defn rephrase
  "If rephrase is on the classpath, wrap the handler with its middleware."
  [handler]
  (if rephrase-mw
    (let [wrapped (rephrase-mw handler)]
      (fn [msg]
        (wrapped msg)))
    handler))

(when rephrase-mw
  (set-descriptor! #'rephrase
                   {:requires #{#'caught/wrap-caught}
                    :expects #{"eval"}}))

#_
(def cider
  (try
    (deref (requiring-resolve 'cider.nrepl/cider-middleware))
    (catch Exception _ identity)))

#_
(defn cider [handler]
  (if-let [wrapper-list
           (try ; in wrong order to compose correctly
             (deref (requiring-resolve 'cider.nrepl/cider-middleware))
             (catch Exception _))]
    (let [wrapped (reduce (fn [h m] ((resolve m) h))
                          handler
                          wrapper-list)]
      (fn [msg]
        (wrapped msg)))
    handler))
