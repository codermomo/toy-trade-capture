FROM curlimages/curl

ADD bootstrap.sh /bootstrap/
ADD schema-registry/trades.json /bootstrap/schema-registry/
ADD kafka-sink-connector/postgres-sink-task.json /bootstrap/kafka-sink-connector/

USER root
RUN chmod +x /bootstrap/bootstrap.sh

ENTRYPOINT ["/bootstrap/bootstrap.sh"]