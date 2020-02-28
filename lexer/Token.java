package lexer;

/** <pre>
 *  The Token class records the information for a token:
 *  1. The Symbol that describes the characters in the token
 *  2. The starting column in the source file of the token and
 *  3. The ending column in the source file of the token
 *  </pre>
*/
public class Token {
  private int leftPosition, rightPosition;
  private int lineNumber;
  private Symbol symbol;

  /**
   *  Create a new Token based on the given Symbol
   *  @param leftPosition is the source file column where the Token begins
   *  @param rightPosition is the source file column where the Token ends
   */
  public Token( int lineNumber, int leftPosition, int rightPosition, Symbol sym ) {
    this.lineNumber = lineNumber;
    this.leftPosition = leftPosition;
    this.rightPosition = rightPosition;
    this.symbol = sym;
  }

  public Symbol getSymbol() {
    return symbol;
  }

  public String toString() {

    String output = String.format( "%-11s left: %-8d right: %-8d line: %-8d %-8s",
      symbol.toString(),
      leftPosition,
      rightPosition,
      lineNumber,
      getType().name()
    );

    return output;
  }

  public int getLeftPosition() {
    return leftPosition;
  }

  public int getRightPosition() {
    return rightPosition;
  }

  /**
   *  @return the integer that represents the kind of symbol we have which
   *  is actually the type of token associated with the symbol
   */
  public TokenType getType() {
    return symbol.getType();
  }
}

