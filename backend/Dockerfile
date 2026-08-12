# ==========================================
# Author: [Your Name]
# Project: SLT Expense Tracker Backend
# Description: Simple Dockerfile for Spring Boot App
# ==========================================

FROM eclipse-temurin:17-jdk-jammy
LABEL maintainer="[Your Name] <[Your Email]>"

WORKDIR /app

# Copy the entire project
COPY . .

# Make the wrapper executable and build the application
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Run the built jar file
CMD ["sh", "-c", "java -jar target/*.jar"]
