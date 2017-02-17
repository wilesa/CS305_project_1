@echo off
javac NetworkLayer.java
javac TransportLayer.java
javac ClientApp.java
javac ServerApp.java
ECHO Server starting
java ServerApp