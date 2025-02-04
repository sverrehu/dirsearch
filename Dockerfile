FROM eclipse-temurin:21.0.6_7-jre-alpine
WORKDIR /app
COPY target/*-standalone.jar ./dirsearch.jar

CMD ["sh", "-c", "exec java -jar dirsearch.jar"]
