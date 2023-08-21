FROM openjdk:17
LABEL authors="p.simeonov"
EXPOSE 8082
COPY rest/target/tinqin-bff.jar tinqin-bff.jar

ENTRYPOINT ["java", "-jar", "/tinqin-bff.jar"]