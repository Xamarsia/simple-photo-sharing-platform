FROM debian:12-slim AS base
RUN apt-get update -y
RUN apt-get upgrade -y
RUN apt-get install openjdk-17-jdk -y
RUN apt-get install maven -y
RUN apt-get install git -y

EXPOSE 8080
