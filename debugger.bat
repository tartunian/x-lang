@echo off
IF [%1]==[] (
  java -cp %cd%\out\production\x-lang interpreter.Interpreter -d simple
)  ELSE  (
  java -cp %cd%\out\production\x-lang interpreter.Interpreter -d %1
)