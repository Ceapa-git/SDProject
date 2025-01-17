FROM maven:3.8.3-openjdk-17 AS builder

COPY ./src/ /root/src
COPY ./pom.xml /root/

WORKDIR /root
RUN mvn package -DskipTests
RUN java -Djarmode=layertools -jar /root/target/device_management-0.0.1-SNAPSHOT.jar list
RUN java -Djarmode=layertools -jar /root/target/device_management-0.0.1-SNAPSHOT.jar extract
RUN ls -l /root

FROM openjdk:17-jdk-slim-buster

COPY --from=builder /root/dependencies/ ./
COPY --from=builder /root/snapshot-dependencies/ ./
COPY --from=builder /root/target/device_management-0.0.1-SNAPSHOT.jar /app/device_management-0.0.1-SNAPSHOT.jar

COPY --from=builder /root/spring-boot-loader/ ./
COPY --from=builder /root/application/ ./
ENTRYPOINT ["java", "-Xms512m", "-Xmx512m", "-XX:+UseG1GC", "-jar", "/app/device_management-0.0.1-SNAPSHOT.jar"]
