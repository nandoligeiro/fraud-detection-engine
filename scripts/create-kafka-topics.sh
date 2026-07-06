#!/usr/bin/env bash
set -euo pipefail

CONTAINER_NAME="${KAFKA_CONTAINER_NAME:-fraud-kafka}"
BROKER="${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}"

create_topic() {
  local topic="$1"
  local partitions="$2"

  docker exec "${CONTAINER_NAME}" kafka-topics \
    --bootstrap-server "${BROKER}" \
    --create \
    --if-not-exists \
    --topic "${topic}" \
    --partitions "${partitions}" \
    --replication-factor 1
}

create_topic transaction-events 12
create_topic fraud-alerts 12
create_topic rule-updates 3
create_topic fraud-dlq 3

docker exec "${CONTAINER_NAME}" kafka-topics --bootstrap-server "${BROKER}" --list
