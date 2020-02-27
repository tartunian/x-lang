IF [%1]==[] (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian compiler.Compiler sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian compiler.Compiler %1
)