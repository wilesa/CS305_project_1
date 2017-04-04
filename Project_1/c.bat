@echo off
javac PhysicalLayer.java
javac LinkLayer.java
javac NetworkLayer.java
javac TransportLayer.java
javac ClientApp.java
javac ServerApp.java
javac Server.java
javac Client.java
ECHO Server starting
java ServerApp
