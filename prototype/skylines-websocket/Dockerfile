FROM maven:3.8.6-eclipse-temurin-8-alpine AS build
WORKDIR /usr/src/skylines-websocket
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn install

FROM scratch AS target
COPY --from=build /usr/src/skylines-websocket/target/*.war .
