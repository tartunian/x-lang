package parser;

import lexer.Token;
import lexer.TokenType;

class SyntaxError extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;
  /**
   *
   */
  private Token tokenFound;
  private TokenType expectedType;

  /**
   * record the syntax error just encountered
   *
   * @param tokenFound is the token just found by the parser
   * @param kindExpected is the token we expected to find based on the current
   * context
   */
  public SyntaxError( Token tokenFound, TokenType expectedType ) {
    this.tokenFound = tokenFound;
    this.expectedType = expectedType;
  }

  @Override
  public String toString() {
    return "Expected: " + expectedType;
  }

}