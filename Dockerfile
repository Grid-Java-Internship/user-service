# multistage build
FROM maven:3.8-openjdk-17 AS build

# wordkir of container
WORKDIR /app

# Build arguments for GitHub credentials
ARG GITHUB_USER_ARG
ARG GITHUB_TOKEN_ARG

# Set environment variables for Maven to use
ENV GITHUB_USERNAME=${GITHUB_USER_ARG}
ENV GITHUB_TOKEN=${GITHUB_TOKEN_ARG}

# Copy custom Maven settings
COPY maven-settings.xml /root/.m2/settings.xml

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