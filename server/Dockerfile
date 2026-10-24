# Bước 1: build file jar bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
# Secret File application-local.properties trên Render nằm ở gốc build context
COPY application-local.properties src/main/resources/
RUN mvn -q -DskipTests package

# Bước 2: chỉ giữ JRE + file jar để chạy
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
