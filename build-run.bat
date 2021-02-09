@echo off
mvn compile package -Dquarkus.profile=dev -DskipTests && java -Djavax.net.ssl.trustStore=mav-start.jks -Djavax.net.ssl.trustStorePassword=password -Djavax.net.ssl.trustStoreType=JKS -jar target/mav-jsoup-runner.jar
