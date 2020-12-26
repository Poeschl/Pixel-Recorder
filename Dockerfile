FROM azul/zulu-openjdk-alpine:11-jre

WORKDIR /app
ADD build/libs/Pixel-Kump-*.jar /app/pixel-kump.jar

ENTRYPOINT ["java", "-jar", "/app/pixel-kump.jar"]
CMD ["--help"]


