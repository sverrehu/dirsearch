FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*-standalone.jar ./dirsearch.jar

CMD ["sh", "-c", "exec java -jar dirsearch.jar"]
