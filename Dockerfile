FROM eclipse-temurin:11-alpine

WORKDIR /app
COPY target/*-standalone.jar ./

CMD ["java", "-jar", "dirsearch-0.1-SNAPSHOT-standalone.jar"]
