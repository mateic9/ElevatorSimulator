# Start with a base image containing Java runtime
FROM openjdk:21-jdk

# Add Maintainer Info
LABEL maintainer="andreistandard@gmail.com"

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Set the working directory in Docker container
WORKDIR /app

# Copy the application's jar to the working directory
COPY ./target/demo-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]