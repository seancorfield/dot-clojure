(ns dev
  "Invoked via load-file from ~/.clojure/deps.edn, this
  file looks at what tooling you have available on your
  classpath and starts a REPL.")

(defn- ->long
  "Attempt to parse a string as a Long and return nil if it fails."
  [s]
  (try
    (and s (Long/parseLong s))
    (catch Throwable _)))

(defn- install-reveal-extras
  "Returns a Reveal view object that tracks each tap>'d value and
  displays its metadata and class type, and its value in a table."
  []
  (try
    (let [last-tap (atom nil)
          rx-stream-as-is (requiring-resolve 'vlaaad.reveal.ext/stream-as-is)
          rx-obs-view     @(requiring-resolve 'vlaaad.reveal.ext/observable-view)
          rx-value-view   @(requiring-resolve 'vlaaad.reveal.ext/value-view)
          rx-table-view   @(requiring-resolve 'vlaaad.reveal.ext/table-view)
          rx-as           (requiring-resolve 'vlaaad.reveal.ext/as)
          rx-raw-string   (requiring-resolve 'vlaaad.reveal.ext/raw-string)]
      (add-tap #(reset! last-tap %))
      (rx-stream-as-is
       (rx-as
        {:fx/type rx-obs-view
         :ref last-tap
         :fn (fn [x]
               (let [x' (if (var? x) (deref x) x)]
                 {:fx/type :v-box
                  :children
                  [{:fx/type rx-value-view
                    :v-box/vgrow :always
                    :value (assoc (meta x)
                                  :_meta (meta x')
                                  :_class (class x'))}
                   (if (or (nil? x') (string? x') (not (seqable? x')))
                     {:fx/type rx-table-view
                      :items [x']
                      :v-box/vgrow :always
                      :columns [{:fn identity :header 'value}]}
                     (let [head (first x')]
                       {:fx/type rx-table-view
                        :items x'
                        :v-box/vgrow :always
                        :columns (cond
                                   (map? head) (for [k (keys head)] {:header k :fn #(get % k)})
                                   (map-entry? head) [{:header 'key :fn key} {:header 'val :fn val}]
                                   (indexed? head) (for [i (range (count head))] {:header i :fn #(nth % i)})
                                   :else [{:header 'item :fn identity}])}))]}))}
        (rx-raw-string "right-click > view" {:fill :object}))))
    (catch Throwable t
      (println "Unable to install Reveal extras!")
      (println (ex-message t)))))

(defn- start-repl
  "Ensures we have a DynamicClassLoader, in case we want to use
  add-libs from the add-lib3 branch of clojure.tools.deps.alpha (to
  load new libraries at runtime).

  If Jedi Time is on the classpath, require it (so that Java Time
  objects will support datafy/nav).

  Attempts to start a Socket REPL server. The port is selected from:
  * SOCKET_REPL_PORT environment variable if present, else
  * socket-repl-port JVM property if present, else
  * .socket-repl-port file if present, else
  * defaults to 50505
  Writes the selected port back to .socket-repl-port for next time.

  Then pick a REPL as follows:
  * if Cognitect's REBL is on the classpath then start that, else
  * if Reveal is on the classpath then start that, else
  * if Rebel Readline is on the classpath then start that, else
  * start a plain ol' Clojure REPL.

  If Reveal is picked, add some custom code to automatically
  display tap>'d values with metadata and in table view."
  []
  ;; set up the DCL:
  (try
    (let [cl (.getContextClassLoader (Thread/currentThread))]
      (.setContextClassLoader (Thread/currentThread) (clojure.lang.DynamicClassLoader. cl)))
    (catch Throwable t
      (println "Unable to establish a DynamicClassLoader!")
      (println (ex-message t))))

  ;; jedi-time?
  (try
    (require 'jedi-time.core)
    (println "Java Time is Datafiable...")
    (catch Throwable _))

  (let [s-port (or (->long (System/getenv "SOCKET_REPL_PORT"))
                   (->long (System/getProperty "socket-repl-port"))
                   (->long (try (slurp ".socket-repl-port") (catch Throwable _)))
                   50505)]
    (println "Selected port" s-port "for the Socket REPL...")
    (spit ".socket-repl-port" (str s-port))
    (try
      ((requiring-resolve 'clojure.core.server/start-server)
       {:port s-port :name (str "REPL-" s-port)
        :accept 'clojure.core.server/repl})
      (catch Throwable t
        (println "Unable to start the Socket REPL on port" s-port)
        (println (ex-message t)))))

  (let [[repl-name repl-fn]
        (or (try ["Cognitect REBL" (requiring-resolve 'cognitect.rebl/-main)]
                 (catch Throwable _))
            (try ["Reveal"
                  (when-let [reveal (requiring-resolve 'vlaaad.reveal/repl)]
                    ;; a five second delay should be sufficient:
                    (future (Thread/sleep 6000)
                            (tap> (install-reveal-extras)))
                    reveal)]
                 (catch Throwable _))
            (try ["Rebel Readline" (requiring-resolve 'rebel-readline.main/-main)]
                 (catch Throwable _))
            ["clojure.main" (resolve 'clojure.main/main)])]
    (println "Starting" repl-name "as the REPL...")
    (repl-fn)))

(start-repl)
