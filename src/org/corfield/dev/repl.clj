;; copyright (c) 2018-2025 sean corfield, all rights reserved

 (ns org.corfield.dev.repl
   "Invoke org.corfield.dev.repl/-main to start a REPL based on
  what tooling you have available on your classpath."
   (:require [clojure.repl :refer [demunge]]
             [clojure.string :as str]))

 (when-not (resolve 'requiring-resolve)
   (throw (ex-info ":dev/repl and repl.clj require at least Clojure 1.10"
                   *clojure-version*)))

 (defn- socket-repl-port
   "Return truthy if it looks like a Socket REPL Server is wanted,
  else return nil. The truthy value is the port number to use.

  Checks the following for port numbers:
  * SOCKET_REPL_PORT env var - a value of none suppresses it
  * socket-repl-port property - a value of none suppresses it
  * .socket-repl-port file - assumed to contain a port number"
   []
   (let [s-port (or (System/getenv "SOCKET_REPL_PORT")
                    (System/getProperty "socket-repl-port")
                    (try (slurp ".socket-repl-port") (catch Throwable _)))]
     (when-not (= "none" s-port)
       (try
         (Long/parseLong s-port)
         (catch Throwable _)))))

 (defn- tap>log [frame level throwable message]
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
        {:dev.repl/logging true}))))

 (defn- ctl-log*adapter [log-star]
   (let [log*-fn (deref log-star)]
     (fn [logger level throwable message]
       (try
         (let [^StackTraceElement frame (nth (.getStackTrace (Throwable. "")) 2)]
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
           (let [^StackTraceElement frame (nth (.getStackTrace (Throwable. "")) 3)]
             (tap>log frame (-> level str str/lower-case keyword)
                      throwable message))
           (catch Throwable _))
         (apply log*-fn logger level more)))))

(defn -main
  "If Jedi Time is on the classpath, require it (so that Java Time
  objects will support datafy/nav).

  If Datomic Dev Datafy is on the classpath, require it (so that
  Datomic objects will support datafy/nav).

  Start a Socket REPL server, if requested. The port is selected from:
  * SOCKET_REPL_PORT environment variable if present, else
  * socket-repl-port JVM property if present, else
  * .socket-repl-port file if present
  Writes the selected port back to .socket-repl-port for next time.

  Use a value of none to suppress the Socket Server startup.

  Then pick a REPL as follows:
  * if Figwheel Main is on the classpath then start that, else
  * if Rebel Readline is on the classpath then start that, else
  * start a plain ol' Clojure REPL."
  [& args]
  ;; jedi-time?
  (try
    (require 'jedi-time.core)
    (println "Java Time is Datafiable...")
    (catch Throwable _))

  ;; datomic/dev.datafy?
  (try
    ((requiring-resolve 'datomic.dev.datafy/datafy!))
    (println "Datomic Datafiers Enabled...")
    (catch Throwable _))

  ;; socket repl handling:
  (when-let [s-port (socket-repl-port)]
    ;; if there is already a 'repl' Socket REPL open, don't open another:
    (when-not (get (deref (requiring-resolve 'clojure.core.server/servers)) "repl")
      (try
        (let [server-name (str "REPL-" s-port)]
          ((requiring-resolve 'clojure.core.server/start-server)
           {:port s-port :name server-name
            :accept 'clojure.core.server/repl})
          (let [s-port' (.getLocalPort
                         (get-in @(requiring-resolve 'clojure.core.server/servers)
                                 [server-name :socket]))]
            (println "Selected port" s-port' "for the Socket REPL...")
            ;; write the actual port we selected (for Chlorine/Clover to read):
            (spit ".socket-repl-port" (str s-port'))))
        (catch Throwable t
          (println "Unable to start the Socket REPL on port" s-port)
          (println (ex-message t))))))

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
    (catch Throwable _))

  ;; select and start a main REPL:
  (let [;; figure out what middleware we might want to supply to nREPL:
        middleware
        (into []
              (filter #(try (requiring-resolve (second %)) true (catch Throwable _)))
              [["Portal"   'portal.nrepl/wrap-portal]
               ["Notebook" 'portal.nrepl/wrap-notebook]
               ["CIDER"    'cider.nrepl/cider-middleware]])
        mw-args
        (when (seq middleware)
          ["--middleware" (str (mapv second middleware))])
        [repl-name repl-fn]
        (or (try ; Figwheel?
              (let [figgy (requiring-resolve 'figwheel.main/-main)]
                ["Figwheel Main" #(figgy "-b" "dev" "-r")])
              (catch Throwable _))
            (try ; Rebel Readline?
              (let [rebel-main (requiring-resolve 'rebel-readline.main/-main)]
                (try
                  (require 'nrepl.cmdline)
                  ;; both Rebel Readline and nREPL are on the classpath!
                  [(str "Rebel Readline + nREPL Server"
                        (when (seq middleware)
                          (str " with " (str/join ", " (map first middleware)))))
                   (fn []
                     ;; see https://github.com/practicalli/clojure-cli-config/blob/03c91cfd0638d880c32e6be09937e69ea8559cd2/deps.edn#L158-L167
                     (apply (resolve 'clojure.main/main)
                            "-e" "(apply require clojure.main/repl-requires)"
                            "-m" "nrepl.cmdline"
                            (into (or mw-args [])
                                  ["--interactive"
                                   "-f" "rebel-readline.main/-main"])))]
                  (catch Throwable _
                    ;; only Rebel Readline is on the classpath:
                    ["Rebel Readline" rebel-main])))
              (catch Throwable _))
            (try ; nREPL?
              [(str "nREPL Server"
                    (when (seq middleware)
                      (str " with " (str/join ", " (map first middleware)))))
               (let [nrepl (requiring-resolve 'nrepl.cmdline/-main)]
                 (fn []
                   (apply nrepl mw-args)))]
              (catch Throwable _))
            ;; fallback to plain REPL:
            ["clojure.main" (resolve 'clojure.main/main)])]
    (println "Starting" repl-name "as the REPL...")
    (repl-fn)
    ;; ensure a smooth exit after the REPL is closed
    (System/exit 0)))

(in-ns 'user)
(defn uptime []
  (-> (java.lang.management.ManagementFactory/getRuntimeMXBean)
      (.getUptime)
      (java.time.Duration/ofMillis)
      (as-> t (map #(% t) [#(.toHours %) #(.toMinutesPart %) #(.toSecondsPart %)])
        (let [[h & ms] t]
          (map vector
               (into ((juxt #(long (/ % 24)) #(mod % 24)) h) ms)
               [" days, " " hours, " " minutes, " " seconds"])))
      (->> (filter (comp pos? first))
           (map #(apply str %)))
      (clojure.string/join)))
