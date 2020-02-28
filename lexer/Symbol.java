package lexer;

/**
 *  The Symbol class is used to store all user strings along with
 *  an indication of the kind of strings they are; e.g. the id "abc" will
 *  store the "abc" in name and Sym.Tokens.Identifier in kind
*/
public class Symbol {
  private String name;
  // token kind of symbol
  private TokenType type;

  private Symbol( String n, TokenType type ) {
    name = n;
    this.type = type;
  }

  // symbols contains all strings in the source program
  private static java.util.HashMap<String,Symbol> symbols = new java.util.HashMap<String,Symbol>();

  public static boolean isKeyword( String s ) {
    return symbols.containsKey( s );
  }

  public static Symbol getSymbolForKeywordString( String keyword ) {
    return symbols.get( keyword );
  }

  public String toString() {
    return name;
  }

  public TokenType getType() {
    return type;
  }


  /**
   * Return the unique symbol associated with a string.
   * Repeated calls to <tt>symbol("abc")</tt> will return the same Symbol.
   */
  public static Symbol put(String newTokenString, TokenType type ) {
    Symbol s = symbols.get( newTokenString );
    if( s == null ) {
      if( type == TokenType.BogusToken ) {
        // bogus string so don't enter into symbols
        return null;
      }
      // System.out.println( "new symbol: " + u + " Kind: " + kind );
      s = new Symbol( newTokenString, type );
      symbols.put( newTokenString, s );
    }

    return s;
  }
}