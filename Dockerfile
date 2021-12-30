FROM openjdk:15-jdk-slim

WORKDIR /app
ADD build/libs/Pixel-Recorder-*.jar /app/pixel-recorder.jar

ENTRYPOINT ["java", "-jar", "/app/pixel-recorder.jar"]
CMD ["--help"]


