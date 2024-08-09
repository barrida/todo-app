# Stage 1: Build the application. The amazoncorretto image is used to build the application.
FROM maven:3.8.4-amazoncorretto-17 AS build
# Set the working directory in the container
RUN mkdir -p /workspace
WORKDIR /workspace
# Copy the pom.xml and the project files to the container
COPY pom.xml /workspace
COPY src /workspace/src
# Build the application using Maven.
RUN mvn clean package

# Stage 2: The amazoncorretto image is used to run the application, keeping the final image smaller.
FROM amazoncorretto:17

# Copy the built JAR file from the previous stage to the container
COPY --from=build /workspace/target/*.jar todo-app.jar
# Set the command to run the application
CMD ["java", "-jar", "todo-app.jar"]
