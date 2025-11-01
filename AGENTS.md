# Agent Memory - Kafka Playground Project

**Last Updated:** 2025-10-30

## Project Overview

A hands-on Kafka learning project using Clojure and REPL-driven exercises. The goal is to deeply understand Kafka concepts through progressively challenging exercises (Basic → Intermediate → Advanced).

## Project Structure

```
kafka-playground/
├── src/
│   ├── kafka/
│   │   ├── core.clj          # Kafka wrapper library (topics, producer, consumer)
│   │   └── init.clj          # Infrastructure initialization (admin, producer)
│   └── exercises/
│       └── basic/
│           ├── exercise01_topic_partitioning.clj
│           ├── exercise02_log_aggregation.clj
│           ├── exercise03_consumer_groups_and_parallelism.clj
│           ├── exercise04_offset_management_and_replay.clj
│           ├── exercise05_message_ordering_and_guarantees.clj
│           └── exercise06_error_handling_and_dlq.clj
├── docker-compose.yml        # Kafka broker setup
├── deps.edn                  # Clojure dependencies
└── README.md                 # Project documentation
```

## Design Principles

1. **REPL-Driven**: All exercises designed for interactive evaluation
2. **Simple & Observable**: Avoid complex loops/futures/atoms - keep code straightforward
3. **Step-by-Step**: Each exercise builds on previous concepts
4. **Practical Focus**: Real-world patterns, not toy examples
5. **Manual Control**: Explicit function calls rather than automatic background processes

## Completed Work

### ✅ Basic Exercises (Structure Complete)

1. **Exercise 1: Topic Partitioning** - DONE
   - Demonstrates partition distribution with/without keys
   - Shows sticky partitioning and key hashing

2. **Exercise 2: Log Aggregation** - DONE
   - Simple producer/consumer with EDN serialization
   - In-memory storage with query helpers
   - Partition key strategy (service:user-id)

3. **Exercise 3: Consumer Groups & Parallelism** - READY TO SOLVE
   - Learn rebalancing and parallel consumption
   - Observe partition assignment across multiple consumers

4. **Exercise 4: Offset Management & Replay** - READY TO SOLVE
   - Manual commits, seeking, replay
   - auto.offset.reset behavior

5. **Exercise 5: Message Ordering & Guarantees** - READY TO SOLVE
   - Within-partition vs across-partition ordering
   - Key-based ordering guarantees

6. **Exercise 6: Error Handling & DLQ** - READY TO SOLVE
   - Dead Letter Queue pattern
   - Retry logic and error metadata

### 🚧 Intermediate Exercises - NOT STARTED
_(Coming soon)_

### 🚧 Advanced Exercises - NOT STARTED
_(Coming soon)_

## Key Learnings & Decisions

### Kafka Wrapper Design
- `kafka.core` provides thin wrappers around Java clients
- `kafka.init` creates shared resources (admin-client, string-producer)
- All functions take explicit parameters (no hidden global state)

### Common Patterns
```clojure
;; Producer pattern
(k/send! ki/string-producer {:topic "..." :key "..." :value (pr-str data)})

;; Consumer pattern
(def consumer (k/consumer {:group.id "..."} ["topic"]))
(k/poll! consumer 1000)  ;; Call manually from REPL
(k/commit! consumer)

;; Record access
(.topic record)
(.partition record)
(.offset record)
(.key record)
(.value record)
```

### Serialization Strategy
- Use EDN (Clojure's native format) with `pr-str` / `edn/read-string`
- Simple and human-readable
- No schema registry needed for basic exercises

### What We Learned to Avoid
- ❌ Complex futures with control atoms (hard to debug)
- ❌ Automatic background loops (user loses control)
- ❌ Inline `def` in functions (code smell)
- ❌ `recur` inside `try/catch` (compilation error)
- ❌ Timestamps with `#time/instant` (serialization complexity)

### What Works Well
- ✅ Simple function calls from REPL
- ✅ Manual polling with `consume-logs!` function
- ✅ Query helpers for inspecting stored data
- ✅ Clear separation: library code vs exercise code
- ✅ Comments in code showing expected output

## Development Workflow

1. User works through exercises in REPL
2. Evaluates expressions step-by-step
3. Observes output to understand Kafka behavior
4. Uses helper functions to query/inspect state

## Technical Notes

### Docker Setup
- Single Kafka broker (KRaft mode, no Zookeeper)
- Port 9092 exposed
- Container name: `kafka-playground-broker`

### Useful Commands
```bash
# Check broker logs
docker logs kafka-playground-broker

# Read messages from topic (console consumer)
docker exec kafka-playground-broker kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic <topic-name> \
  --from-beginning \
  --max-messages 5
```

### Common Issues & Solutions

**Issue:** Consumer times out (heartbeat expiration)
**Solution:** Poll regularly. Consumer must call `poll()` to send heartbeats.

**Issue:** Messages not deserializing
**Solution:** Ensure EDN format is used consistently. Check for tagged literals.

**Issue:** Consumer rebalancing continuously
**Solution:** Don't create multiple consumers with same group.id accidentally (use `defonce` or check if exists).

## Next Steps

1. User will solve exercises 3-6 with guidance
2. Define intermediate exercises based on learnings
3. Define advanced exercises
4. Consider adding:
   - Transactions
   - Exactly-once semantics
   - Stream processing
   - Performance tuning

## Context for Future Sessions

- User prefers step-by-step guidance, not complete solutions
- Keep code simple - this is a learning project
- All exercises should be observable and produce clear output
- Avoid complexity - if it needs atoms/futures, simplify the approach
- User values clean, idiomatic Clojure

---

**Note to AI Assistants:** This file tracks project state, decisions, and learnings. Update it when significant changes or insights occur. Use it to maintain context across sessions.
