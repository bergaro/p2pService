FROM openjdk:8-jdk-alpine
EXPOSE 5500
ADD build/libs/p2p-0.0.1.jar p2p.jar
ENTRYPOINT ["java", "-jar", "/p2p.jar"]
