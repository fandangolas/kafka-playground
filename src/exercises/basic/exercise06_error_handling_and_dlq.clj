(ns exercises.basic.exercise06-error-handling-and-dlq
  (:require
   [kafka.core :as k]
   [kafka.init :as ki]))

;; Exercise: Handle processing errors and implement a Dead Letter Queue pattern.
;;
;; Goal: Learn error handling strategies for Kafka consumers
;;
;; Tasks:
;; - Create two topics: "orders" (main) and "orders-dlq" (dead letter queue)
;; - Produce messages to "orders" topic
;; - Create a consumer that simulates processing (some messages "fail" randomly)
;; - For failed messages: send to DLQ topic with error metadata
;; - Implement retry logic: retry N times before sending to DLQ
;; - Add error details to DLQ messages (original message, error reason, timestamp, retry count)
;; - Create a DLQ consumer to inspect failed messages
;; - Experiment: what happens if DLQ send fails?
;;
;; Key Learnings:
;; - Dead Letter Queue pattern prevents message loss on processing errors
;; - DLQ messages should include diagnostic information
;; - Retry logic balances reliability vs. throughput
;; - Errors should not block the entire consumer
;; - DLQ can be processed separately (manual review, automated retry, etc.)
;; - Important to monitor DLQ depth (high depth = systemic issue)
