FROM maven:alpine as build
ENV HOME=/Code/out
RUN mkdir -p $HOME
WORKDIR $HOME

ADD build/pom.xml $HOME
ADD mr-231/pom.xml $HOME/mr-231/pom.xml
ADD mr-231-3/pom.xml $HOME/mr-231-3/pom.xml
ADD startApp/pom.xml $HOME/startApp/pom.xml

RUN mvn -pl mr-231 verify --fail-never
ADD mr-231 $HOME/mr-231
RUN mvn -pl mr-231 install
RUN mvn -pl mr-231-3 verify --fail-never
ADD mr-231-3 $HOME/mr-231-3
RUN mvn -pl mr-231-3 install
RUN mvn -pl startApp verify --fail-never
ADD startApp $HOME/startApp
RUN mvn -pl startApp install


FROM eclipse-temurin:17-jre-jammy
# Copy the executable from the "package" stage.
COPY --from=build Code/out/startApp/target/startApp-1.0-SNAPSHOT-jar-with-dependencies.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ]
