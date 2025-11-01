(ns exercises.basic.exercise04-offset-management-and-replay
  (:require
   [kafka.core :as k]
   [kafka.init :as ki]))

;; Exercise: Master offset management to control where consumers read from.
;;
;; Goal: Understand offsets, commits, and how to replay messages
;;
;; Tasks:
;; - Produce 20 messages to a topic
;; - Create a consumer and consume first 10 messages
;; - Manually commit offsets
;; - Restart the consumer, observe it continues from offset 10
;; - Reset consumer to earliest (replay all messages from beginning)
;; - Reset consumer to latest (skip to end, only read new messages)
;; - Seek to a specific offset (e.g., offset 5) and consume from there
;; - Experiment with auto.offset.reset: "earliest" vs "latest" vs "none"
;; - Try with auto-commit enabled vs disabled
;;
;; Key Learnings:
;; - Offsets track position in a partition
;; - Commits persist offsets to Kafka
;; - Can replay messages by resetting offsets
;; - auto.offset.reset controls behavior for new consumer groups
;; - Manual commits give more control over processing guarantees
