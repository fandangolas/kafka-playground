(ns exercises.basic.exercise05-message-ordering-and-guarantees
  (:require
   [kafka.core :as k]
   [kafka.init :as ki]))

;; Exercise: Understand message ordering guarantees in Kafka.
;;
;; Goal: Learn when and how Kafka guarantees message order
;;
;; Tasks:
;; - Create a topic with 3 partitions
;; - Send 10 messages with the SAME key, observe they all go to the same partition
;; - Consume and verify they are in order (by offset)
;; - Send 10 messages with DIFFERENT keys, observe they go to different partitions
;; - Consume and observe: order is NOT guaranteed across partitions
;; - Send messages with no key (null), observe distribution
;; - Produce messages with explicit partition assignment
;; - Test ordering within a partition vs across partitions
;;
;; Key Learnings:
;; - Kafka guarantees order within a partition, NOT across partitions
;; - Messages with the same key always go to the same partition (ensures ordering for that key)
;; - If you need total ordering, use a single partition (limits throughput)
;; - Partition keys enable ordering per entity (e.g., per user, per device)
;; - Null keys distribute round-robin or sticky (no ordering guarantee)
