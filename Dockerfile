FROM openjdk:8
ADD build/libs/integration.jar integration.jar
ENTRYPOINT ["java", "-jar", "integration.jar"]
