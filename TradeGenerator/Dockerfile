FROM openjdk:21-jdk-slim as builder
WORKDIR app
ADD target/TradeGenerator-0.0.1-SNAPSHOT.jar ./
RUN java -Djarmode=layertools -jar TradeGenerator-0.0.1-SNAPSHOT.jar extract

FROM openjdk:21-jdk-slim

WORKDIR app

COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader/ ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-Djava.security.egd=file:/dev/./urandom", "org.springframework.boot.loader.launch.JarLauncher"]