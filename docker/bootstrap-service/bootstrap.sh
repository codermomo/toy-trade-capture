#!/bin/sh

echo "Running bootstrap.sh script ⏳"

if [ "${BOOTSTRAP_SCHEMA_REGISTRY:-true}" = "true" ] || [ "${BOOTSTRAP_SCHEMA_REGISTRY:-true}" = "True" ] ; then
  echo "Submitting schema to schema registry"
  curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data @/bootstrap/schema-registry/trades.json ${SCHEMA_REGISTRY_URL}
  sleep 1
fi

if [ "${BOOTSTRAP_KAFKA_SINK_CONNECTOR:-true}" = "true" ] || [ "${BOOTSTRAP_KAFKA_SINK_CONNECTOR:-true}" = "True" ] ; then
  echo "Submitting task to kafka sink"
  curl -X POST -H "Content-Type: application/json" --data @/bootstrap/kafka-sink-connector/postgres-sink-task.json ${KAFKA_SINK_CONNECTOR_URL}
fi

echo "Finished running bootstrap.sh script ⌛"