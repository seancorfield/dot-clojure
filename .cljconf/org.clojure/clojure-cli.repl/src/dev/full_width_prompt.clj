(ns dev.full-width-prompt
  (:require
    [clojure-cli.repl.api :as api])
  (:import
    [java.time LocalTime]
    [java.time.format DateTimeFormatter]))

(def sep-r "▓▒░")
(def sep-l "░▒▓")
(def check "✓")
(def error "✗")
(def mb (* 1024 1024))
(def gb (* 1024 mb))
(def time-fmt (DateTimeFormatter/ofPattern "HH:mm:ss"))

(def color-schemes
  {:retro {:ns {:fg :bright-white :bg :blue}
           :ok {:fg :black :bg :green}
           :err {:fg :bright-white :bg :red}
           :time {:fg :bright-white :bg :bright-black}
           :heap {:fg :black :bg :cyan}
           :clock {:fg :bright-white :bg :magenta}
           :arrow {:fg :bright-green}}
   :dark  {:ns {:fg 252 :bg 238}
           :ok {:fg 108 :bg 236}
           :err {:fg 174 :bg 236}
           :time {:fg 246 :bg 235}
           :heap {:fg 109 :bg 234}
           :clock {:fg 250 :bg 237}
           :arrow {:fg 108}}
   :light {:ns {:fg 236 :bg 252}
           :ok {:fg 65  :bg 254}
           :err {:fg 131 :bg 254}
           :time {:fg 242 :bg 255}
           :heap {:fg 60  :bg 253}
           :clock {:fg 238 :bg 250}
           :arrow {:fg 65}}})

(def scheme (color-schemes :dark))

(defn eval-time-str []
  (when-let [t (:clojure-cli.repl/nanos @api/last-response)]
    (cond
      (< t 1000)(str t "ns")
      (< t 1000000) (str (quot t 1000) "µs")
      :else (str (quot t 1000000) "ms"))))

(defn heap-str
  "Amount of used server heap of total avilable after last eval.
  Requires user supplied heap middleware."
  []
  (when-let [used (:clojure-cli.repl/heap-used @api/last-response)]
    (str (quot used mb) "M/"
         (quot (:clojure-cli.repl/heap-max @api/last-response) gb) "G")))

(defn seg [style text]
  (when text
    {:text (str " " text " ") :style (scheme style)}))

(defn fade-join [segments]
  (mapcat (fn [[{:keys [style] :as seg} nxt]]
            [seg {:text sep-r :style {:fg (:bg style) :bg (:bg (:style nxt))}}])
          (partition 2 1 [nil] segments)))

(defn display-width [segments]
  (let [widths (map #(count (:text %)) segments)]
    (reduce + widths)))

(defn prompt []
  (let [resp @api/last-response
        left (remove nil?
               [(seg :ns @api/current-ns)
                (when resp (if (:ex resp) (seg :err error) (seg :ok check)))
                (seg :time (eval-time-str))
                (seg :heap (heap-str))])
        left-segs (vec (fade-join left))
        fade-in  {:text sep-l :style {:fg (:bg (scheme :clock))}}
        clock    (seg :clock (.format (LocalTime/now) time-fmt))
        fade-out {:text sep-r :style {:fg (:bg (scheme :clock))}}
        pad (max 0 (- (api/terminal-width)
                      (display-width left-segs)
                      (display-width [fade-in clock fade-out])))]
    (into left-segs
          [{:text (apply str (repeat pad " "))}
           fade-in clock fade-out
           {:text "\n"}
           {:text "❯ " :style (assoc (scheme :arrow) :bold true)}])))
