(ns exercises.basic.exercise01-topic-partitioning
  (:require
   [kafka.core :as k]))

;; Goal: Understand how Kafka distributes messages across partitions.
;; Tasks:
;; - Create a topic user-events with 3 partitions
;; - Produce 20 messages with different strategies:
;;   - Without keys (10 messages)
;;   - With keys: user-1, user-2, user-3 (10 messages)
;; - Consume all messages and observe:
;;   - Which partition did each message land in?
;;   - Do messages with the same key always go to the same partition?
;;   - What's the distribution for keyless messages?
;;
;; Learnings: Partitioning strategy, key hashing, message routing

(comment
  (def kafka-client
    (k/admin-client {}))

  (k/create-topics! kafka-client [{:name "user-events" :partitions 3}])

  (def producer (k/producer {}))

  (k/send! producer {:topic "user-events"
                     :value (pr-str {:message "test-message-0"})})


  (def consumer (k/consumer {:group.id "user-events-processor"} ["user-events"]))

  (def consumer-loop
    (future
      (while true
        (doseq [record (k/poll! consumer 1000)]
          (println "received from"
                   {:topic     (.topic record)
                    :partition (.partition record)
                    :offset    (.offset record)
                    :key       (.key record)
                    :value     (.value record)}))
        (k/commit! consumer))))



  ;Without keys (10 messages)
  (dotimes
   [n 10]
    (k/send! producer {:topic "user-events"
                       :value (pr-str {:message (str "hello-" n)})}))

  ; Result: with partition-key as nil, Kafka randomly
  ; chooses a partition and send every message to the same one (sticky partition strategy).
  ; 
  ; received from {:topic user-events, :partition 2, :offset 43, :key nil, :value {:message "hello-0"}}
  ; received from {:topic user-events, :partition 2, :offset 44, :key nil, :value {:message "hello-1"}}
  ; received from {:topic user-events, :partition 2, :offset 45, :key nil, :value {:message "hello-2"}}
  ; received from {:topic user-events, :partition 2, :offset 46, :key nil, :value {:message "hello-3"}}
  ; received from {:topic user-events, :partition 2, :offset 47, :key nil, :value {:message "hello-4"}}
  ; received from {:topic user-events, :partition 2, :offset 48, :key nil, :value {:message "hello-5"}}
  ; received from {:topic user-events, :partition 2, :offset 49, :key nil, :value {:message "hello-6"}}
  ; received from {:topic user-events, :partition 2, :offset 50, :key nil, :value {:message "hello-7"}}
  ; received from {:topic user-events, :partition 2, :offset 51, :key nil, :value {:message "hello-8"}}
  ; received from {:topic user-events, :partition 2, :offset 52, :key nil, :value {:message "hello-9"}}

  ;With keys: user-1, user-2, user-3 (10 messages)
  (dotimes
   [n 10]
    (k/send! producer {:key   (str "user-" n)
                       :topic "user-events"
                       :value (pr-str {:message (str "hello-" (+ n 10))})}))
  
  ; Result: with partition-key as the user-id,
  ; we distribute the messages across the partitions using the DefaultPartitioner.
  ; received from {:topic user-events, :partition 2, :offset 36, :key nil,    :value {:message "hello-19"}}
  ; received from {:topic user-events, :partition 1, :offset 3,  :key user-0, :value {:message "hello-10"}}
  ; received from {:topic user-events, :partition 2, :offset 37, :key user-1, :value {:message "hello-11"}}
  ; received from {:topic user-events, :partition 2, :offset 38, :key user-2, :value {:message "hello-12"}}
  ; received from {:topic user-events, :partition 2, :offset 39, :key user-3, :value {:message "hello-13"}}
  ; received from {:topic user-events, :partition 1, :offset 4,  :key user-4, :value {:message "hello-14"}}
  ; received from {:topic user-events, :partition 1, :offset 5,  :key user-5, :value {:message "hello-15"}}
  ; received from {:topic user-events, :partition 2, :offset 40, :key user-6, :value {:message "hello-16"}}
  ; received from {:topic user-events, :partition 2, :offset 41, :key user-7, :value {:message "hello-17"}}
  ; received from {:topic user-events, :partition 0, :offset 0,  :key user-8, :value {:message "hello-18"}}
  ; received from {:topic user-events, :partition 2, :offset 42, :key user-9, :value {:message "hello-19"}}

  ;When you’re done:
  (future-cancel consumer-loop)
  (k/close! consumer))
    
