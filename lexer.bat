IF [%1]==[] (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian lexer.Lexer sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian lexer.Lexer %1
)