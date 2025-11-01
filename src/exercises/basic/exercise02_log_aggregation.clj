(ns exercises.basic.exercise02-log-aggregation
  (:require
   [clojure.edn :as edn]
   [clojure.pprint :as pprint]
   [kafka.core :as k]
   [kafka.init :as ki]
   [schema.core :as s]))

;; Exercise: Centralise structured service logs through Kafka and enable fast replay for investigations.

;;; =============================================================================
;;; STEP 1: Schema and Partition Key Strategy
;;; =============================================================================

(s/defschema LogEvent
  {:user-id   s/Str
   :level     s/Keyword
   :message   s/Str
   :service   s/Keyword})

;;; =============================================================================
;;; STEP 2: Produce logs
;;; =============================================================================

(defn send-log!
  [service user-id level message]
  (let [log-event {:service service
                   :user-id user-id
                   :level   level
                   :message message}
        key       (str (name service) ":" user-id)]
    (k/send! ki/string-producer {:topic "services-logging"
                                 :key   key
                                 :value (pr-str log-event)})))

(s/defn random-service :- s/Keyword
  []
  (rand-nth [:service-1 :service-2 :service-3]))

(s/defn random-level :- s/Keyword
  []
  (rand-nth [:debug :warn :info :error]))

(defn send-random-logs
  []
  (dotimes [_ 10]
   (let [message (str "random message: " (rand-int 100))]
               (send-log! (random-service)
                          (inc (rand-int 10))
                          (random-level)
                          message))))

;;; =============================================================================
;;; STEP 3: Consumer with In-Memory Store
;;; =============================================================================

(def log-store (atom []))

(defn record->map [record]
  {:topic     (.topic record)
   :partition (.partition record)
   :offset    (.offset record)
   :key       (.key record)
   :value     (.value record)
   :timestamp (.timestamp record)})

(defn consume-logs!
  "Poll and consume logs once. Call this repeatedly from REPL."
  [consumer]
  (doseq [record (k/poll! consumer 1000)]
    (let [log-event (edn/read-string (.value record))]
      (swap! log-store conj log-event)
      (pprint/pprint "Received a log event: ")
      (pprint/pprint (record->map record))))
  (k/commit! consumer))

;;; =============================================================================
;;; Query Helpers
;;; =============================================================================

(defn logs-by-service [service]
  (filter #(= (:service %) service) @log-store))

(defn logs-by-user [user-id]
  (filter #(= (:user-id %) user-id) @log-store))

(defn logs-by-level [level]
  (filter #(= (:level %) level) @log-store))

(defn stats []
  {:total       (count @log-store)
   :by-service  (frequencies (map :service @log-store))
   :by-level    (frequencies (map :level @log-store))})

;;; =============================================================================
;;; REPL Usage
;;; =============================================================================

(comment
  ;; Create consumer
  (def consumer
    (k/consumer {:group.id "log-consumer"} ["services-logging"]))

  ;; Consume logs (run this multiple times to keep consuming)
  (def should-poll? (atom true))
  (reset! should-poll? false)
  
  (future (while @should-poll? 
            (Thread/sleep 100) 
            (println "Searching for messages...") 
            (consume-logs! consumer)))
  
  ;; Send some random logs
  (send-random-logs)

  ;; Query logs
  ;; (stats)
  ;; (logs-by-service :service-1)
  ;; (logs-by-user "user-1")
  ;; (logs-by-level :error)

  ;; ;; Clear store
  ;; (reset! log-store [])

  ;; Close consumer when done
  (k/close! consumer))
