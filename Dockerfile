# multistage build
FROM maven:3.8-openjdk-17 AS build

# wordkir of container
WORKDIR /app

# copy all project files to container
COPY pom.xml .

# run maven
RUN mvn verify clean --fail-never

COPY . .

RUN mvn package -DskipTests

# lightweight image for runtime
FROM eclipse-temurin:17-jre-jammy AS runtime

RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 spring && \
    mkdir -p /home/spring/app

WORKDIR /home/spring/app

COPY --chown=spring:spring --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]

EXPOSE 8081