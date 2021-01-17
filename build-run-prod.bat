@echo off
mvn -P!bmw compile package -Dquarkus.profile=prod -DskipTests && java -jar target/mav-jsoup-runner.jar
