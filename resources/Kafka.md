## Kafka Migration Notes

### Kafka vs ActiveMQ
ActiveMQ is a traditional message broker aiming to ensure safe and reliable data exchange between applications. It 
implements the JMS (Java Message Service) API, which supports message destination models of queues (P2P) and topics 
(PubSub) and newer protocols, such as AMQP, MQTT, and STOMP. ActiveMQ is best suited for processing small amount of 
data.

Kafka is a distributed event streaming platform capable of high-throughput, scalable, and highly available delivery with
permanent storage of messages. Event streaming means capturing data from event sources (like databases, applications) in
the form of events, storing and manipulating these event streams, and routing them to different destination 
technologies. Kafka only supports topic-based message destination model. It is best suited for streaming high load of 
data, but without transport guarantee. To ensure reliability, messages are persisted with a custom retention period to 
enable reliable recovery, in case of losses in transportation.

#### Tradeoffs
Pros: High throughput, high availability and fault tolerance, high scalability, message persistence and data replication
that enable data recovery.

Cons: Reliability transportation needs to be implemented externally, high complexity of technology.


### Kafka Components
1. Kafka brokers and controllers (server): Brokers are responsible for the storage of Kafka messages, serving as a data 
plane. Controllers are the control plane of Kafka brokers.
2. Kafka producer and consumer: Producers and consumers are two types of clients that push and pull messages to Kafka 
topics respectively.
3. Schema Registry: Storing schemas of messages of a given topics, the schema registry stores the data contract agreed 
by the upstream and downstream Kafka clients and ensure consistency.
4. Kafka Connectors: Source connectors and sink connectors are two types of Kafka Connectors responsible for the ingress
and egress of data to and from Kafka topics. They are built upon on the producer and consumer API.


### Kafka Server
The server adopts the Kafka Raft protocol, removing Kafka's dependency on ZooKeeper for metadata management. As 
mentioned above, there are two server roles available, broker and controller.

Brokers are the data plane which handles producer and consumer client requests and perform data replication. Kafka 
messages are stored under a predetermined Kafka topic, where each topic is divided into a predetermined number of 
partitions, with a predetermined number of replicas per partition. Brokers also communicate with controllers to fetch a 
copy of the latest metadata in cache.

Controllers are the control plane that manages metadata, such as the metadata about the leader and ISR (In-Sync 
Replicas) of a particular partition of a particular topic. Both controllers and brokers store the metadata in cache 
memory to ensure efficiency. Metadata are replicated among controllers in a similar method of data replication among 
brokers, but utilizing quorum instead of ISR for leader election and commiting records. Furthermore, since cache memory 
is limited, a snapshot of metadata is created and persisted to disks when the cache is full.

Read this [tutorial](https://developer.confluent.io/courses/architecture/control-plane/) to understand the high-level 
architecture and algorithms used in brokers and controllers.

In the context of docker, each container can either be a broker or a controller, or both, specified in 
`KAFKA_CFG_PROCESS_ROLES`. Each containers need to define the hostname and port it is listening to along with the 
security protocol in `KAFKA_CFG_LISTENERS` and `KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP`. They are also required to 
specify the list of quorum controllers in `KAFKA_CFG_CONTROLLER_QUORUM_VOTERS`. Brokers need to advertise their 
listeners for producer and consumer clients in `KAFKA_CFG_ADVERTISED_LISTENERS` as well as state the listener it uses 
for inter-broker communication (e.g. data replication) in `KAFKA_CFG_INTER_BROKER_LISTENER_NAME`. Controllers need to 
state the listeners it used in `KAFKA_CFG_CONTROLLER_LISTENER_NAMES`. 

It is worth to note the following: The broker is open for connection via listeners in `KAFKA_CFG_LISTENERS`. Yet, when a
client first communicates with a broker, the broker will send back the corresponding advertised listener in
`KAFKA_CFG_ADVERTISED_LISTENERS`, associated by the listener name. After that, the client will attempt to communicate 
with the broker via the advertised listener. For example, a Kafka broker Docker container is bind with a host port, 
accessible via `localhost:7777` in the host. To enable traffic from the host, it should also advertise a listener with 
`localhost:7777`, such that subsequent communication from the host is made possible. If it advertises a listener 
with `kafka0:<container_port>`, where `kafka0` is the hostname of the container, the client will attempt to communicate 
to `kafka0`, but it will be unable to resolve the hostname `kafka0`, because it is internal to the Docker network, 
leading to a connection error. Likewise, a separate listener is required for traffic internal to the Docker network.

#### Useful commands
- Read all records under a specific Kafka topic (within the container):
```
kafka-console-consumer.sh --bootstrap-server kafka0:9092 --topic trades --from-beginning
```

References:
- [Introduction to KRaft](https://developer.confluent.io/learn/kraft/)
- [Kafka Internals: Controller Plane](https://developer.confluent.io/courses/architecture/control-plane/)
- [Docker image used](https://hub.docker.com/r/bitnami/kafka)
- [Official Docker image configuration reference](https://docs.confluent.io/platform/current/installation/docker/config-reference.html)
- [Sample KRaft Docker cluster configuration in Medium](https://gsfl3101.medium.com/kafka-raft-kraft-cluster-configuration-from-dev-to-prod-part-1-8a844fabf804)


### Kafka Schema Registry
The schema registry is a standalone process that accepts HTTP requests to receive schemas for Kafka topics. Due to the 
configuration of the Dockerfile, it is hard to bootstrap a new schema registry instance with pre-loaded schemas. Thus, 
it is recommended to send HTTP requests to submit new schemas.

- [Overview](https://docs.confluent.io/platform/current/schema-registry/index.html)
- [Docker image](https://hub.docker.com/r/confluentinc/cp-schema-registry)
- [Setup example with Kafka broker on top of Zookeeper](https://jskim1991.medium.com/docker-docker-compose-example-for-kafka-zookeeper-and-schema-registry-c516422532e7)
- [Setup example with Kafka broker on top of Kraft](https://blog.yowko.com/docker-compose-avro-schema-registry/)
- [Example of using Avro serializer in a producer in Java](https://www.confluent.io/blog/schema-registry-avro-in-spring-boot-application-tutorial/)
- [Example of using JSON serializer in a producer in Java](https://docs.confluent.io/platform/current/schema-registry/fundamentals/serdes-develop/serdes-json.html)
- [Confluent Schema Registry Configuration](https://docs.confluent.io/platform/current/installation/docker/config-reference.html#sr-long-configuration)
- [High level best practices](https://www.confluent.io/blog/best-practices-for-confluent-schema-registry/)

#### Useful commands
- Submit a schema via a HTTP request:
```
curl -X POST -H "Content-Type: application/vnd.schemaregistry.v1+json" --data @/schemas/trades.json http://schema-registry:8072/subjects/trades-value/versions
```


### Kafka Connect
Kafka Connect is a standalone process for connecting upstream data sources and downstream data stores with Kafka. Each 
instance is a worker that accepts HTTP requests for running source/sink Connect tasks. Connecting to different systems 
requires different connector plugins. For one, a JDBC sink connector plugin needs to be installed for reading data from 
Kafka and ingesting to databases via JDBC, and a sink task needs to be submitted to perform the egress task.

- [Docker image](https://hub.docker.com/r/confluentinc/cp-kafka-connect)
- [Running a Docker container](https://developer.confluent.io/courses/kafka-connect/docker-containers/)
- [REST APIs for distributed mode Connect workers (e.g. adding connectors)](https://docs.confluent.io/platform/current/connect/references/restapi.html)
- [JDBC connector default configuration parameters when adding connectors](https://docs.confluent.io/kafka-connectors/jdbc/current/sink-connector/sink_config_options.html)
- [Useful transformers for fields](https://docs.confluent.io/platform/current/connect/transforms/overview.html)
- [Docker compose configuration to submit tasks when bootstrap](https://rmoff.net/2018/12/15/docker-tips-and-tricks-with-kafka-connect-ksqldb-and-kafka/)

#### Useful commands
- Submit a sink connector task:
```
curl -X POST -H "Content-Type: application/json" --data @bootstrap/kafka-sink-connector/postgres-sink.json http://localhost:8083/connectors
```
- Get information about a sink connector:
```
curl -X GET http://localhost:8083/connectors/postgres-sink-connector
```
- Get status of task:
```
curl -X GET http://localhost:8083/connectors/postgres-sink-connector/tasks/0/status
```
- Update configuration/ restart tasks of a given connector:
```
curl -X PUT -H "Content-Type: application/json" --data @bootstrap/kafka-sink-connector/tmp.json http://localhost:8083/connectors/postgres-sink-connector/config
```
- Restart a failed task:
```
curl -X POST http://localhost:8083/connectors/postgres-sink-connector/tasks/0/restart
```
- Delete a Connector:
```
curl -X DELETE http://localhost:8083/connectors/postgres-sink-connector
```