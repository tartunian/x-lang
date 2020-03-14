# x-lang
The purpose of this project is to learn about the compilation process of programming languages by implementing one in Java.
## Completed Components
 - [x] Lexer
 - [ ] Parser
 - [ ] Constrainer
 - [ ] Compiler
 - [ ] Interpreter
## Tokens
 - And &
 - Assign =
 - BOOLean boolean
 - Case case
 - Char char
 - CharLit <char>
 - Colon :
 - Comma ,
 - Comment //
 - Default default
 - Divide /
 - Else else
 - EndProgram XD
 - Equal ==
 - Function function
 - Greater >
 - GreaterEqual >=
 - INTeger <int>
 - Identifier <id>
 - If if
 - Int int
 - LeftBrace {
 - LeftParen (
 - Less <
 - LessEqual <=
 - Minus -
 - Multiply *
 - NotEqual !=
 - Or |
 - Plus +
 - Program program
 - Return return
 - RightBrace }
 - RightParen )
 - StringLit <string>
 - StringType string
 - Switch switch
 - Then then
 - Unless unless
 - While while
## Grammar
|Production|Description|
|--|--|
| TYPE->'char' | Char Token |
| TYPE->'string' | StringType Token |
| F&#8594;\<char> | Char literals |
| F&#8594;\<string> | String literals |
| S&#8594;'if' E 'then' BLOCK | if statement (without else) |
| S&#8594;'unless' E 'then' BLOCK | unless statement |
| SwitchBlock&#8594;'{' CaseStatement+ DefaultStatement? '}' | switch statement block (one or more case statements followed by none or one default statement) |
| CaseStatement&#8594;'case' \<int> ':' S | case statement with only one statement |
| DefaultStatement&#8594;'default' ':' S | default statement with only one statement |
| E&#8594;SE '>' SE | Greater token |
| E&#8594;SE '>=' SE | GreaterEqual token |
## Compilation
Batch files are provided for compiling and executing most components. For example, to compile the lexer, run `clexer` in the project root. To run the lexer, run `lexer {source_file}`. The commands which accept a source file as input use `sample_files\simple.x` as a default source file.
> Written with [StackEdit](https://stackedit.io/).
