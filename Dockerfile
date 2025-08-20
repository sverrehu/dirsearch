FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app
COPY target/*-standalone.jar ./dirsearch.jar

CMD ["sh", "-c", "exec java -jar dirsearch.jar"]
