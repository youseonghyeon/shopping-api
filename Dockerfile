FROM --platform=linux/arm64 arm64v8/openjdk:21-jdk-slim
ENV JAVA_OPTS="-XX:UseSVE=0"
VOLUME /tmp
COPY build/libs/shopping-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app.jar"]
