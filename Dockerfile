FROM azul/zulu-openjdk-alpine:11-jre

WORKDIR /app
ADD build/libs/pixelflut-kump-1.0-SNAPSHOT-all.jar /app/kump.jar

ENTRYPOINT ["java", "-jar", "/app/kump.jar"]


