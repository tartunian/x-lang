@echo off
cinterpreter.bat
javac -d %cd%\out\production\x-lang interpreter\debugger\*.java
javac -d %cd%\out\production\x-lang interpreter\bytecode\debuggercodes\*.java