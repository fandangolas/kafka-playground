(ns kafka.init 
  (:require
   [kafka.core :as k]))


(defonce admin-client
  (k/admin-client {}))

(defonce string-producer
  (k/producer k/default-bootstrap))

;; (k/create-topics! admin-client [{:name "user-events" :partitions 3}
;;                                 {:name "services-logging" :partitions 5}])

(defn shutdown! []
  (k/close! string-producer)
  (k/close! admin-client))