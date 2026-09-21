FROM gcr.io/distroless/java25-debian13:nonroot

WORKDIR /app
COPY target/*-standalone.jar ./dirsearch.jar

CMD ["dirsearch.jar"]
