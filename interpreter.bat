@echo off
IF [%1]==[] (
  java -cp %cd%\out\production\x-lang interpreter.Interpreter sample_files\simple.x.cod
)  ELSE  (
  java -cp %cd%\out\production\x-lang interpreter.Interpreter %1
)