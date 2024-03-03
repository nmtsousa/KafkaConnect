# Introduction

This project shows of a Kafka Connect wrapper docker image. The goal is to
show how easy it is to make your own docker image with your kafka connect
plugins.

## Building

It is simple to build, just make sure you have Java 17, Maven and Docker
installed. Then run:

```shell
mvn clean package jib:dockerBuild
```

## Running

Start the containers and do not forget to specify the needed Kafka Connect
properties.

```
docker run --rm -ti --env-file envfile kafkaconnect
```

The contents of the *envfile* should be something like:

```shell
# Kafka Connect Settings
CONNECT_KEY_CONVERTER=org.apache.kafka.connect.json.JsonConverter
CONNECT_VALUE_CONVERTER=org.apache.kafka.connect.json.JsonConverter
CONNECT_GROUP_ID=kafka-connect
CONNECT_OFFSET_STORAGE_TOPIC=kafka-connect-offset-storage-topic
CONNECT_CONFIG_STORAGE_TOPIC=kafka-connect-config-storage-topic
CONNECT_STATUS_STORAGE_TOPIC=kafka-connect-status-storage-topic
CONNECT_BOOTSTRAP_CONTROLLERS=kafka-cluster:9092
# ...
```