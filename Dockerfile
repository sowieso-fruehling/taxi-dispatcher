FROM maven:3.5.3-jdk-8-alpine as builder
COPY . /
RUN ./mvnw package

FROM maven:3.5.3-jdk-8-alpine as runner
COPY --from=builder /target/freenow_server_applicant_test-1.0.0-SNAPSHOT.jar /deployment/app.jar
WORKDIR /deployment
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]