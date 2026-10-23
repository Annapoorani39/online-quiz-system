FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Resolve dependencies first (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY lib ./lib
COPY Java ./Java

# Build the fat jar (skip tests for speed)
RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the assembled jar from the build stage (any jar in target)
COPY --from=build /app/target/*.jar online-quiz-system.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","online-quiz-system.jar"]
