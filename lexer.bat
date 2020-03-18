@echo off
IF [%1]==[] (
  java -cp %cd%\out\production\x-lang lexer.Lexer sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\x-lang lexer.Lexer %1
)