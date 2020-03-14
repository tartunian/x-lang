package parser;

import lexer.Token;
import lexer.TokenType;

class SyntaxException extends Exception {

  public SyntaxException( String message ) {
    super( message );
  }

  public SyntaxException( Token currentToken, TokenType expectedType ) {
    super( String.format( "Expected %s but got %s", expectedType.name(), currentToken.getType().name() ) );
  }

  public SyntaxException( String expectedValue, String receivedValue ) {
    super( String.format( "Expected %s but got %s", expectedValue, receivedValue ) );
  }

}