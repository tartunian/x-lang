package parser;

import lexer.Token;
import lexer.TokenType;

public class SyntaxException extends Exception {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  public SyntaxException(String message) {
    super( message );
  }

  public SyntaxException( int lineNumber, Token currentToken, TokenType expectedType ) {
    super( String.format( "Line %2d: Expected %s but got %s", lineNumber, expectedType.name(), currentToken.getType().name() ) );
  }

  public SyntaxException( int lineNumber, String expectedValue, String receivedValue ) {
    super( String.format( "Line %2d: Expected %s but got %s", lineNumber, expectedValue, receivedValue ) );
  }

}