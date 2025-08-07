FROM eclipse-temurin:21.0.8_9-jre-alpine
WORKDIR /app
COPY target/*-standalone.jar ./dirsearch.jar

CMD ["sh", "-c", "exec java -jar dirsearch.jar"]
