FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/classes/api-futebol-api-0.0.1-SNAPSHOT.jar app.jar
# target/classes/api-futebol-api-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "api-futebol.jar"]