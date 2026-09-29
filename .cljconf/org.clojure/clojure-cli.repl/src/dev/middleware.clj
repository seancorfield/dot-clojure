(ns dev.middleware
  (:require [nrepl.middleware :refer [set-descriptor!]]
            [nrepl.middleware.caught :as caught]
            [nrepl.middleware.print :as print]))

(defn portal [handler]
  (if-let [wrapper
           (try
             (requiring-resolve 'portal.nrepl/wrap-portal)
             (catch Exception _))]
    ;; unclear why this doesn't work - wrap-portal doesn't work either:
    (let [wrapped (wrapper handler)]
      (fn [msg]
        (wrapped msg)))
    handler))

(set-descriptor! #'portal
                 {:requires #{"clone"
                              #'print/wrap-print
                              #'caught/wrap-caught}
                  :expects #{"eval" "load-file"}
                  :handles {}})
