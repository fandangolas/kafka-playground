(ns exercises.basic.exercise03-consumer-groups-and-parallelism
  (:require
   [kafka.core :as k]
   [kafka.init :as ki]))

;; Exercise: Understand how multiple consumers work together and enable parallel processing.
;;
;; Goal: Learn about consumer groups, rebalancing, and partition assignment
;;
;; Tasks:
;; - Create a topic with 3 partitions
;; - Start 1 consumer in a group, observe it gets all 3 partitions
;; - Start a 2nd consumer in the same group, observe rebalancing (partitions redistributed)
;; - Start a 3rd consumer in the same group, observe each gets 1 partition
;; - Send messages and observe parallel consumption across consumers
;; - Try starting a 4th consumer - what happens?
;; - Compare with consumers in different groups (they all get the same messages)
;;
;; Key Learnings:
;; - Consumer groups enable load balancing
;; - Max parallelism = number of partitions
;; - Rebalancing happens when consumers join/leave
;; - Different groups are independent (each gets all messages)
