# x-lang
The purpose of this project is to learn about the compilation process of programming languages by implementing one in Java.
## Components
 - [x] Lexer
 - [ ] Parser - Due 11:59 PM Tuesday, March 31, 2020
	 - [x] Data types
	 - [ ] Grammar
	 - [ ] Drawing
		 - [ ] OffsetVisitor
		 - [ ] DrawVisitor

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


> Written with [StackEdit](https://stackedit.io/).
