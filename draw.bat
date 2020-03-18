@echo off
IF [%1]==[] (
  java -cp %cd%\out\production\x-lang parser.DrawOffsetAST sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\x-lang parser.DrawOffsetAST %1
)