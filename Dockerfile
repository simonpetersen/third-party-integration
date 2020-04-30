FROM openjdk:8
ADD build/libs/integration.jar integration.jar
EXPOSE 443
ENTRYPOINT ["java", "-jar", "integration.jar"]
