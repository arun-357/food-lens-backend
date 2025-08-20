# Use an official JDK 21 base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy build tool files (Maven or Gradle)
COPY . .

# Build the application (adjust for Maven or Gradle)
RUN ./mvnw clean package -DskipTests  # For Maven
# RUN ./gradlew build -x test  # For Gradle, uncomment if using Gradle

# Expose the port (Render sets PORT dynamically)
EXPOSE $PORT

# Run the JAR file (adjust the JAR name as per your project)
CMD ["java", "-jar", "target/food-lens-backend-0.0.1-SNAPSHOT.jar"]