IF [%1]==[] (
  java -cp %cd%\out\production\x-lang parser.DrawParseTree sample_files\simple.x
)  ELSE  (
  java -cp %cd%\out\production\x-lang parser.DrawParseTree %1
)