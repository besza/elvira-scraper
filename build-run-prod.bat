@echo off
mvn compile package -Dquarkus.profile=prod -DskipTests && java -jar target/mav-jsoup-runner.jar
