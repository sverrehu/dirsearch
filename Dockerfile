FROM eclipse-temurin:11-alpine

WORKDIR /app
COPY target/*-standalone.jar ./

CMD ["java", "-jar", "dirsearch-1.0-SNAPSHOT-standalone.jar"]
