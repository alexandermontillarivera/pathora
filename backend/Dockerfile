FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src
RUN ./mvnw package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN apk add --no-cache nginx su-exec \
    && addgroup -S pathora \
    && adduser -S pathora -G pathora
COPY --from=build --chown=pathora:pathora /app/target/*.jar app.jar
COPY nginx.render.conf /etc/nginx/nginx.conf

ENV JAVA_TOOL_OPTIONS="-Xms128m -Xmx320m -XX:MaxMetaspaceSize=128m -Xss512k -XX:+UseSerialGC -XX:TieredStopAtLevel=1"

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "sed -i \"s/listen 10000;/listen ${PORT:-10000};/\" /etc/nginx/nginx.conf && nginx && exec su-exec pathora /opt/java/openjdk/bin/java -jar /app/app.jar --server.port=8080"]
