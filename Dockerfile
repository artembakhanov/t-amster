FROM alpine/git as korolev

RUN git clone https://gitflic.ru/project/fomkin/korolev.git /korolev

FROM sbtscala/scala-sbt:eclipse-temurin-focal-11.0.22_7_1.9.9_3.4.0 as tamster
COPY --from=korolev /korolev /korolev
WORKDIR /korolev
RUN sbt "++3.3.3; set ThisBuild / version := \"1.7.0-M1.8-SNAPSHOT\"; publishLocal"

COPY ./src /t-amster/src
COPY ./project /t-amster/project
COPY ./build.sbt /t-amster/build.sbt

WORKDIR /t-amster
RUN sbt assembly

FROM adoptopenjdk/openjdk11
COPY --from=tamster /t-amster/target/*.jar /opt/app/app.jar
ENTRYPOINT [ "java" ]



