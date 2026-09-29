(ns dev.middleware
  (:require [nrepl.middleware :refer [set-descriptor!]]
            [nrepl.middleware.caught :as caught]
            [nrepl.middleware.print :as print]))

#_
(def portal
  (try
    (deref (requiring-resolve 'portal.nrepl/middleware))
    (catch Exception _ identity)))

(defn portal [handler]
  (if-let [wrapper-list
           (try ; this just happens to be in the right order for composition:
             (deref (requiring-resolve 'portal.nrepl/middleware))
             (catch Exception _))]
    ;; unclear why this doesn't work - wrap-portal doesn't work either:
    (let [wrapped (reduce (fn [h m] ((resolve m) h))
                          handler
                          wrapper-list)]
      (fn [msg]
        (wrapped msg)))
    handler))

(set-descriptor! #'portal
                 {:requires #{"clone"
                              #'print/wrap-print
                              #'caught/wrap-caught}
                  :expects #{"eval" "load-file"}
                  :handles {}})

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
