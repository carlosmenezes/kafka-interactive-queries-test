FROM openjdk:8-jdk-slim

RUN mkdir /interactive-queries
COPY build/libs/kafka-interactive-queries-test-1.0-SNAPSHOT.jar /interactive-queries/interactive-queries.jar

WORKDIR /interactive-queries

CMD java                    \
  -Djava.security.egd=file:/dev/./urandom        \
  -jar \
  interactive-queries.jar