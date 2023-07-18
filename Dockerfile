FROM amazoncorretto:20

EXPOSE 7070

WORKDIR /app

COPY ./target/*.jar .

ENTRYPOINT ["java", "-jar", "first-mvc-app.jar"]