package lexer;

import java.io.IOException;

/**
 *  The Lexer class is responsible for scanning the source file
 *  which is a stream of characters and returning a stream of
 *  tokens; each token object will contain the string (or access
 *  to the string) that describes the token along with an
 *  indication of its location in the source program to be used
 *  for error reporting; we are tracking line numbers; white spaces
 *  are space, tab, newlines
 */
public class Lexer {
  private final char StringTerminator = '\"';
  private final char CharTerminator = '\'';
  private boolean atEOF = false;
  // next character to process
  private char currentCharacter;
  private SourceReader source;

  // positions in line of current token
  private int startPosition, endPosition;

  /**
   *  Lexer constructor
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer( String sourceFile ) throws IOException {
    // init token table
    new TokenStore();
    source = new SourceReader( sourceFile );
    currentCharacter = source.read();
  }

  private void consumeComments() throws IOException {
    int startingLine = source.getLineNumber();
    do {
      currentCharacter = source.read();
    } while ( source.getLineNumber() == startingLine );
  }

  private void consumeWhiteSpace() throws IOException {
    while( Character.isWhitespace( currentCharacter ) ) {
      currentCharacter = source.read();
    }
    startPosition = source.getPosition();
    endPosition = startPosition - 1;
  }

  private Token getIdentifierToken() throws IOException {
    String id = "";

    do {
      endPosition++;
      id += currentCharacter;
      currentCharacter = source.read();
    } while ( Character.isJavaIdentifierPart( currentCharacter ) );

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( id, TokenType.Identifier ) );
  }

  private Token getKeywordToken() throws IOException {
    String word = "";

    do {
      word += currentCharacter;
      currentCharacter = source.read();
      endPosition++;
    } while ( Character.isAlphabetic( currentCharacter ) );

    Symbol s = Symbol.getSymbolForKeywordString( word );
    return new Token(source.getLineNumber(), startPosition, endPosition, s);
  }

  private Token getNumberToken() throws IOException {
    String number = "";

    do {
      endPosition++;
      number += currentCharacter;
      currentCharacter = source.read();
    } while( Character.isDigit( currentCharacter ) );

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( number, TokenType.INTeger ) );
  }

  private Token getStringLiteralToken() throws IOException, LexicalException {
    String word = "";

    do {
      word += currentCharacter;
      currentCharacter = source.read();
      endPosition++;
      if( source.isAtEndOfLine() && currentCharacter != StringTerminator ) {
        throw new LexicalException( String.format( "******** illegal characters: %s", word ) );
      }
    } while ( currentCharacter != StringTerminator );

    currentCharacter = source.read();
    endPosition++;
    word += StringTerminator;

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( word, TokenType.StringLit ) );
  }

  private Token getCharacterLiteralToken() throws IOException, LexicalException {
    String c = "";

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    if( currentCharacter != CharTerminator ) {
      throw new LexicalException( String.format( "******** illegal characters: %s", c ) );
    }

    currentCharacter = source.read();
    endPosition++;

    c += CharTerminator;

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( c, TokenType.CharLit ) );
  }

  private Token getTwoCharToken() throws IOException, LexicalException {
    char firstCharacter = currentCharacter;
    String twoCharToken = Character.toString( firstCharacter );
    endPosition++;
    currentCharacter = source.read();
    twoCharToken += currentCharacter;

    if ( twoCharToken == "//" ) {
      consumeComments();
    }

    Symbol sym = Symbol.put( twoCharToken, TokenType.BogusToken );
    if ( sym == null ) {
      sym = Symbol.put( Character.toString( firstCharacter ), TokenType.BogusToken );
      if ( sym == null ) {
        throw new LexicalException( "******** illegal character: " + firstCharacter );
      } else {
        return new Token( source.getLineNumber(), startPosition, endPosition, sym );
      }
    } else {

      endPosition++;
      currentCharacter = source.read();

      sym = Symbol.put( twoCharToken, TokenType.BogusToken );
      if ( sym == null ) {
        throw new LexicalException( "******** illegal character: " + twoCharToken );
      } else {
        return new Token( source.getLineNumber(), startPosition, endPosition, sym );
      }
    }
  }

  private Token getEndOfFileToken() {
    return new Token( source.getLineNumber(), startPosition, endPosition, TokenStore.getSymbolByTokenType( TokenType.EndProgram ) );
  }

  /**
   *  @return the next Token found in the source file
   */
  public Token nextToken() throws LexicalException {

    try {
      consumeWhiteSpace();
      if ( Character.isJavaIdentifierStart( currentCharacter ) ) {
        return getIdentifierToken();
      } else if ( Character.isLetter( currentCharacter ) ) {
        return getKeywordToken();
      } else if ( currentCharacter == StringTerminator ) {
        return getStringLiteralToken();
      } else if ( currentCharacter == CharTerminator ) {
        return getCharacterLiteralToken();
      } else if ( Character.isDigit( currentCharacter ) ) {
        return getNumberToken();
      } else {
        return getTwoCharToken();
      }
    } catch ( IOException e ) { };

    return getEndOfFileToken();
  }

  /*
   * Command-line entry point
   */
  public static void main( String args[] ) {

    String sourceFile = args[0];
    Lexer lexer = null;

    try {
      lexer = new Lexer( sourceFile );
      Token token;

      while( ( token = lexer.nextToken() ).getType() != TokenType.EndProgram ) {
        System.out.println( token );
      }

    } catch ( LexicalException e ) {
      System.out.println( e.getMessage() );
    } catch ( IOException e ) {}

    System.out.println();

    try {
      lexer.source.reset();

      // Print out the full source code.
      String sourceCodeLine;
      while ( ( sourceCodeLine = lexer.source.readLine() ) != null ) {
        String s = String.format( "%3d: %s", lexer.source.getLineNumber(), sourceCodeLine );
        System.out.println( s );
      }
    } catch ( IOException e ) {}

  }

}