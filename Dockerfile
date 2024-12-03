FROM bellsoft/liberica-runtime-container:jre-21-musl
RUN apk add --no-cache tzdata
ENV TZ=Asia/Kolkata

COPY target/spring-boot-reactive-service-1.0.0.jar /app.jar
COPY src/main/resources/application.yaml /application.yaml
ENTRYPOINT exec java -server -Xmx256m -Xss1024k -Djava.security.egd=file:/dev/./urandom -jar /app.jar
