FROM debian:latest

RUN apt-get update -y
RUN apt-get upgrade -y
RUN apt-get install openjdk-17-jdk -y
RUN apt-get update -y
RUN apt-get install maven -y

RUN apt-get update -y
RUN apt-get upgrade -y

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src
CMD ["./mvnw", "spring-boot:run"]

ENTRYPOINT ["java", "-jar", "/simple-photo-sharing-platform.jar"]