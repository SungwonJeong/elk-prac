FROM openjdk:17

ARG JAR_FILE=build/libs/elk-prac.jar

WORKDIR /opt/app

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]