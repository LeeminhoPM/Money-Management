FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/moneymanagement-0.0.1-SNAPSHOT.jar moneymanament-v1.0.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymanament-v1.0.jar"]