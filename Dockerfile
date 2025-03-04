# multistage build
FROM maven:3.8-openjdk-17 AS build

# wordkir of container
WORKDIR /app

# copy all project files to container
COPY . .

# run maven
RUN mvn clean && mvn install -DskipTests

# lightweight image for runtime
FROM eclipse-temurin:17-jdk-alpine AS runtime

RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 spring && \
    mkdir -p /home/spring/app

WORKDIR /home/spring/app

COPY --chown=spring:spring --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]