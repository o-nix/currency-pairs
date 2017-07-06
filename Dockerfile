FROM ubuntu:17.04

RUN apt-get update \
 && apt-get install -y openjdk-8-jre

COPY target/currency_pairs-0.0.1-SNAPSHOT.jar /opt/app/

ENTRYPOINT java -jar /opt/app/currency_pairs-0.0.1-SNAPSHOT.jar