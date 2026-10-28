# Build context là thư mục gốc repo vì server cần thêm project common
# Bước 1: build file jar bằng Maven
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Cài jar lớp thực thể dùng chung vào kho Maven của image
COPY common/pom.xml common/
COPY common/src common/src
RUN mvn -q -f common/pom.xml install -DskipTests
COPY server/pom.xml server/
RUN mvn -q -f server/pom.xml dependency:go-offline
COPY server/src server/src
# Secret File application-local.properties trên Render nằm ở gốc build context
COPY application-local.properties server/src/main/resources/
RUN mvn -q -f server/pom.xml -DskipTests package

# Bước 2: chỉ giữ JRE + file jar để chạy
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/server/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
