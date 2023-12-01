FROM alpine:3.18
COPY ./ .
RUN apk add openjdk17
RUN ./gradlew compileKotlin
ENTRYPOINT ./gradlew tasks