package lexer;
 
/**
 *  This file is automatically generated<br>
 *  it contains the table of mappings from token
 *  constants to their Symbols
*/
public class TokenStore {
   private static java.util.HashMap<TokenType,Symbol> store = new java.util.HashMap<TokenType,Symbol>();

   public static Symbol get( TokenType type ) {
     return store.get( type );
   }

   static {
     store.put(TokenType.Program, Symbol.put("program",TokenType.Program));
     store.put(TokenType.Int, Symbol.put("int",TokenType.Int));
     store.put(TokenType.BOOLean, Symbol.put("boolean",TokenType.BOOLean));
     store.put(TokenType.Char, Symbol.put("char",TokenType.Char));
     store.put(TokenType.CharLit, Symbol.put("<char>",TokenType.CharLit));
     store.put(TokenType.StringType, Symbol.put("string",TokenType.StringType));
     store.put(TokenType.StringLit, Symbol.put("<string>",TokenType.StringLit));
     store.put(TokenType.If, Symbol.put("if",TokenType.If));
     store.put(TokenType.Then, Symbol.put("then",TokenType.Then));
     store.put(TokenType.Else, Symbol.put("else",TokenType.Else));
     store.put(TokenType.Switch, Symbol.put("switch",TokenType.Switch));
     store.put(TokenType.Case, Symbol.put("case",TokenType.Case));
     store.put(TokenType.Colon, Symbol.put(":",TokenType.Colon));
     store.put(TokenType.Default, Symbol.put("default",TokenType.Default));
     store.put(TokenType.While, Symbol.put("while",TokenType.While));
     store.put(TokenType.Function, Symbol.put("function",TokenType.Function));
     store.put(TokenType.Return, Symbol.put("return",TokenType.Return));
     store.put(TokenType.Identifier, Symbol.put("<id>",TokenType.Identifier));
     store.put(TokenType.INTeger, Symbol.put("<int>",TokenType.INTeger));
     store.put(TokenType.LeftBrace, Symbol.put("{",TokenType.LeftBrace));
     store.put(TokenType.RightBrace, Symbol.put("}",TokenType.RightBrace));
     store.put(TokenType.LeftParen, Symbol.put("(",TokenType.LeftParen));
     store.put(TokenType.RightParen, Symbol.put(")",TokenType.RightParen));
     store.put(TokenType.Comma, Symbol.put(",",TokenType.Comma));
     store.put(TokenType.Assign, Symbol.put("=",TokenType.Assign));
     store.put(TokenType.Equal, Symbol.put("==",TokenType.Equal));
     store.put(TokenType.NotEqual, Symbol.put("!=",TokenType.NotEqual));
     store.put(TokenType.Greater, Symbol.put(">",TokenType.Greater));
     store.put(TokenType.GreaterEqual, Symbol.put(">=",TokenType.GreaterEqual));
     store.put(TokenType.Less, Symbol.put("<",TokenType.Less));
     store.put(TokenType.LessEqual, Symbol.put("<=",TokenType.LessEqual));
     store.put(TokenType.Plus, Symbol.put("+",TokenType.Plus));
     store.put(TokenType.Minus, Symbol.put("-",TokenType.Minus));
     store.put(TokenType.Or, Symbol.put("|",TokenType.Or));
     store.put(TokenType.And, Symbol.put("&",TokenType.And));
     store.put(TokenType.Multiply, Symbol.put("*",TokenType.Multiply));
     store.put(TokenType.Divide, Symbol.put("/",TokenType.Divide));
     store.put(TokenType.Comment, Symbol.put("//",TokenType.Comment));
     store.put(TokenType.EndProgram, Symbol.put("XD",TokenType.EndProgram));
   }
}
