#
# Stage 1: Build stage
#
FROM maven:3.8.4-openjdk-17-slim AS build

# Copy Maven and application source files for dependency resolution
COPY src /home/app/src
COPY pom.xml /home/app

# Build the project and create the executable JAR
RUN mvn -f /home/app/pom.xml -Dmaven.test.skip clean package


#
# Stage 2: Run stage
#
FROM openjdk:17-jdk-slim

# Copy the JAR file from the build stage
COPY --from=build /home/app/target/simple-photo-sharing-platform.jar /app/target/simple-photo-sharing-platform.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/target/simple-photo-sharing-platform.jar"]
