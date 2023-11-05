FROM adoptopenjdk/openjdk11:alpine-jre

EXPOSE 8080

COPY target/TemplateSpringBootHello-1.0-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]


