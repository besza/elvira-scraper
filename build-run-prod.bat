@echo off
mvn package -Dquarkus.profile=prod -DskipTests && java -jar target/mav-jsoup-runner.jar
