# Kafka Playground

A hands-on learning project for mastering Apache Kafka concepts through progressively challenging exercises.

## Purpose

This project provides a structured path to deeply understand Kafka through practical, REPL-driven exercises in Clojure. Each exercise focuses on a specific concept with clear goals and observable outcomes.

## Exercises

- **Basic** - Core Kafka fundamentals
  - Topic Partitioning - Understand message distribution across partitions
  - Log Aggregation - Producer/consumer basics with in-memory storage
  - Consumer Groups & Parallelism - Load balancing with multiple consumers
  - Offset Management & Replay - Control message reading and replay
  - Message Ordering & Guarantees - Ordering within and across partitions
  - Error Handling & DLQ - Dead letter queues and retry patterns

- **Intermediate** - Production patterns and operational concerns
  - _(Coming soon)_

- **Advanced** - Complex scenarios and optimizations
  - _(Coming soon)_

## Getting Started

1. Start Kafka with Docker:
   ```bash
   docker-compose up -d
   ```

2. Connect to the REPL and navigate to an exercise namespace:
   ```clojure
   (require '[exercises.basic.exercise01-topic-partitioning :as ex1])
   ```

3. Follow the exercise instructions in each file's comments

4. Evaluate expressions step-by-step in the REPL to observe Kafka behavior

## Philosophy

- **REPL-driven**: Evaluate expressions interactively to see immediate results
- **Observable**: Each exercise produces visible output to understand behavior
- **Incremental**: Concepts build on previous exercises
- **Practical**: Focus on real-world patterns, not toy examples

## Prerequisites

- Docker & Docker Compose
- Clojure CLI tools
- Basic understanding of Clojure syntax

## Topics Covered

- Topics, partitions, and replication
- Producers and consumers
- Consumer groups and rebalancing
- Offsets and message replay
- Serialization and schemas
- Message ordering guarantees
- Error handling patterns
- Performance considerations

---

**Note:** This is a learning project. Exercises are designed for understanding, not production use.
