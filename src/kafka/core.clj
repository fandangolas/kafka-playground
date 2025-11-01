(ns kafka.core
  "Thin Clojure wrapper around the Kafka Java clients so exercises can stay focused on
   Kafka behaviour rather than ceremony."
  (:require [schema.core :as s])
  (:import
   [java.lang AutoCloseable]
   (java.time Duration)
   (java.util Properties)
   (org.apache.kafka.clients.admin AdminClient NewTopic)
   (org.apache.kafka.clients.consumer ConsumerRecord KafkaConsumer)
   (org.apache.kafka.clients.producer KafkaProducer ProducerRecord RecordMetadata)
   (org.apache.kafka.common.header Header)
   (org.apache.kafka.common.serialization StringDeserializer StringSerializer)))

(def default-bootstrap
  "Default connection options. Override per exercise as needed."
  {:bootstrap.servers "localhost:9092"})

(def ConfigMap
  {s/Any s/Any})

(def TopicSpec
  {:name s/Str
   (s/optional-key :partitions) s/Int
   (s/optional-key :replication-factor) s/Int
   (s/optional-key :config) (s/maybe ConfigMap)})

(def HeaderValue
  (s/cond-pre bytes? string? s/Any))

(def MessageRecord
  {:topic s/Str
   (s/optional-key :key) s/Any
   (s/optional-key :value) s/Any
   (s/optional-key :partition) s/Int
   (s/optional-key :headers) (s/maybe {s/Keyword HeaderValue})})

(s/defn map->properties :- Properties
  "Convert a Clojure map of config values into a java.util.Properties instance."
  [m :- ConfigMap]
  (let [props (Properties.)]
    (doseq [[k v] m]
      (.put props
            (if (keyword? k) (name k) (str k))
            (if (keyword? v) (name v) v)))
    props))

(s/defn admin-client :- AdminClient
  "Create an AdminClient from the provided config map."
  [config :- ConfigMap]
  (AdminClient/create (map->properties (merge default-bootstrap config))))

(s/defn create-topics! :- nil
  "Create the supplied topics (sequence of {:name \"topic\" :partitions 1 :replication-factor 1})."
  [admin :- AdminClient
   topics :- [TopicSpec]]
  (let [topic-objs (map (fn [{:keys [name partitions replication-factor config]
                              :or {partitions 1 replication-factor 1}}]
                          (doto (NewTopic. name (int partitions) (short replication-factor))
                            (.configs (into {} (for [[ck cv] config]
                                                 [(name ck) (str cv)])))))
                        topics)]
    (.. admin (createTopics topic-objs) (all) (get))
    nil))

(s/defn delete-topics! :- nil
  "Delete the given topic names."
  [admin :- AdminClient
   topic-names :- [s/Str]]
  (.. admin (deleteTopics topic-names) (all) (get))
  nil)

(s/defn producer :- KafkaProducer
  "Instantiate a KafkaProducer. Supply serializers with keys :key.serializer and :value.serializer.
   Defaults to String serializers when not provided."
  [config :- ConfigMap]
  (let [config (merge {:key.serializer   StringSerializer
                       :value.serializer StringSerializer}
                      default-bootstrap
                      config)]
    (KafkaProducer. (map->properties config))))

(s/defn consumer :- KafkaConsumer
  "Instantiate a KafkaConsumer and subscribe it to the provided topics. Defaults to String deserializers."
  [config :- ConfigMap
   topics :- [s/Str]]
  (let [config (merge {:key.deserializer   StringDeserializer
                       :value.deserializer StringDeserializer
                       :group.id           "kafka-playground-consumer"
                       :auto.offset.reset  "earliest"}
                      default-bootstrap
                      config)
        consumer (KafkaConsumer. (map->properties config))]
    (.subscribe consumer topics)
    consumer))

(s/defn send! :- RecordMetadata
  "Send a single record using the provided KafkaProducer. Accepts a map with keys :topic, :key, :value,
   optional :partition, and optional :headers (map of string->bytes). Returns the RecordMetadata."
  [producer :- KafkaProducer
   {:keys [topic key value partition headers]} :- MessageRecord]
  (let [record (if headers
                 (let [header-array (into-array Header
                                                (for [[hk hv] headers]
                                                  (org.apache.kafka.common.header.internals.RecordHeader.
                                                   (name hk)
                                                   (cond
                                                     (bytes? hv) hv
                                                     (string? hv) (.getBytes hv)
                                                     :else hv))))]
                   (ProducerRecord. topic (when partition (int partition)) key value header-array))
                 (ProducerRecord. topic (when partition (int partition)) key value))]
    (.get (.send producer record))))

(s/defn poll! :- [ConsumerRecord]
  "Poll the consumer for records, returning a seq of ConsumerRecord instances. Defaults to 1s timeout."
  ([consumer :- KafkaConsumer]
   (poll! consumer nil))
  ([consumer :- KafkaConsumer
    timeout-ms :- (s/maybe s/Int)]
   (let [timeout (Duration/ofMillis (long (or timeout-ms 1000)))
         records (.poll consumer timeout)]
     (iterator-seq (.iterator records)))))

(s/defn commit! :- nil
  "Commit the current offsets synchronously."
  [consumer :- KafkaConsumer]
  (.commitSync consumer)
  nil)

(s/defn close! :- nil
  "Close any Closeable Kafka client (admin, producer, consumer)."
  [client :- AutoCloseable]
  (.close client)
  nil)
