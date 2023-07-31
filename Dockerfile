FROM amazoncorretto:20

WORKDIR /app

COPY . .

RUN ["./mvnw", "clean", "install"]

ENTRYPOINT ["java", "-jar", "target/first-mvc-app.jar"]