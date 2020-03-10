IF [%1]==[] (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian parser.Parser sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\assignment-2-lexer-spring-2020-tartunian parser.Parser %1
)