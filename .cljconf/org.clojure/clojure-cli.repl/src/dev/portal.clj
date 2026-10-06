(ns dev.portal
  "Called to patch logging libraries so they `tap>` logs in a format 
   Portal understands, if Portal is on the classpath."
  (:require [clojure.repl :refer [demunge]]
            [clojure.string :as str]))

(defn- tap>log [^StackTraceElement frame level throwable message]
  (let [class-name (symbol (demunge (.getClassName frame)))]
    ;; only called for enabled log levels:
    (tap>
     (with-meta
       {:form     '()
        :level    level
        :result   (or throwable message)
        :ns       (symbol (or (namespace class-name)
                              ;; fully-qualified classname - strip class:
                              (str/replace (name class-name) #"\.[^\.]*$" "")))
        :file     (.getFileName frame)
        :line     (.getLineNumber frame)
        :column   0
        :time     (java.util.Date.)
        :runtime  :clj}
       {::logging true}))))

(defn- ctl-log*adapter [log-star]
  (let [log*-fn (deref log-star)]
    (fn [logger level throwable message]
      (try
        (let [frame (nth (.getStackTrace (Throwable. "")) 2)]
          (tap>log frame level throwable message))
        (catch Throwable _))
      (log*-fn logger level throwable message))))

(defn- logging4j2*adapter [log-star]
  (let [log*-fn (deref log-star)]
    (fn [logger level & more]
      (let [[throwable message-parts]
            ;; drop marker if present; find throwable and everything else:
            (cond (instance? Throwable (first more))
                  [(first more) (rest more)]
                  (instance? Throwable (second more))
                  [(second more) (rest (rest more))]
                  :else
                  [nil more])
            message (str/join " " message-parts)]
        (try
          (let [frame (nth (.getStackTrace (Throwable. "")) 3)]
            (tap>log frame (-> level str str/lower-case keyword)
                     throwable message))
          (catch Throwable _))
        (apply log*-fn logger level more)))))

(defn install! []
  ;; if Portal and clojure.tools.logging are both present,
  ;; cause all (successful) logging to also be tap>'d:
  (try
    ;; if we have Portal on the classpath...
    (require 'portal.console)
    ;; ...then install a tap> ahead of tools.logging:
    (let [log-star (requiring-resolve 'clojure.tools.logging/log*)]
      (alter-var-root
       log-star
       (constantly (ctl-log*adapter log-star))))
    (println "clojure.tools.logging will be tap>'d...")
    (catch Throwable _))

  ;; if Portal and logging4j2 are both present,
  ;; cause all (successful) logging to also be tap>'d:
  (try
    ;; if we have Portal on the classpath...
    (require 'portal.console)
    ;; ...then install a tap> ahead of logging4j2:
    (let [log-star (requiring-resolve 'org.corfield.logging4j2.impl/log*)]
      (alter-var-root
       log-star
       (constantly (logging4j2*adapter log-star))))
    (println "org.corfield.logging4j2 will be tap>'d...")
    (catch Throwable _)))
