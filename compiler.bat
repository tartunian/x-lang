@echo off
IF [%1]==[] (
  java -cp %cd%\out\production\x-lang compiler.Compiler sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\x-lang compiler.Compiler %1
)