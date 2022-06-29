FROM eclipse-temurin:11-alpine
ARG APP_VERSION=0.1-SNAPSHOT
ENV APP_VERSION=${APP_VERSION}
WORKDIR /app
COPY target/*-standalone.jar ./

CMD ["sh", "-c", "exec java -jar dirsearch-${APP_VERSION}-standalone.jar"]
