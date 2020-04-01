package lexer;

import java.io.IOException;

import util.DebugOptions;
import util.DebugOptions.Options;

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

  private DebugOptions debugOptions;

  private final char StringTerminator = '\"';
  private final char CharTerminator = '\'';
  private char currentCharacter;
  private SourceReader source;

  // positions in line of current token
  private int startPosition, endPosition;

  /**
   *  Lexer constructor
   * @param sourceFile is the name of the File to read the program source from
   */
  public Lexer( String sourceFile ) throws IOException {
    this.debugOptions = new DebugOptions( Options.TOKENS, Options.SOURCECODE );
    new TokenStore();
    source = new SourceReader( sourceFile, true );
    currentCharacter = source.read();
  }

  public Lexer( String sourceFile, DebugOptions debugOptions ) throws IOException {
    this.debugOptions = debugOptions;
    new TokenStore();
    source = new SourceReader( sourceFile, debugOptions.contains( Options.TOKENS ) );
    currentCharacter = source.read();
  }

  public SourceReader getSource() {
    return source;
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
    return new Token( source.getLineNumber(), startPosition, endPosition, s );
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

    if ( twoCharToken.equals( TokenStore.getSymbolByTokenType( TokenType.Comment ).toString() ) ) {
      consumeComments();
      return nextToken();
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
  private Token nextToken() throws LexicalException {
    Token token;
    try {
      consumeWhiteSpace();      
      if ( Character.isJavaIdentifierStart( currentCharacter ) ) {
        token = getIdentifierToken();
      } else if ( Character.isLetter( currentCharacter ) ) {
        token = getKeywordToken();
      } else if ( currentCharacter == StringTerminator ) {
        token = getStringLiteralToken();
      } else if ( currentCharacter == CharTerminator ) {
        token = getCharacterLiteralToken();
      } else if ( Character.isDigit( currentCharacter ) ) {
        token = getNumberToken();
      } else {
        token = getTwoCharToken();
      }
    } catch ( IOException e ) {
      token = getEndOfFileToken();
    }
    return token;
  }
  
  public Token getNextToken() throws LexicalException {
    Token token = nextToken();
    if ( ! token.getType().equals( TokenType.EndProgram  ) ) {
      if ( debugOptions.contains( Options.TOKENS ) ) {
        System.out.println( token );
      }
    } else {
      try {
        System.out.println();
        printSourceCode();
      } catch( IOException e ){ }
    }
    return token;
  }

  private void processTokens() throws IOException, LexicalException {
    while( ! ( getNextToken() ).getType().equals( TokenType.EndProgram ) );
  }

  public void printSourceCode() throws IOException {
    if ( debugOptions.contains( Options.SOURCECODE ) ) {
      source.reset();
      String sourceCodeLine;
      while( ( sourceCodeLine = source.readLine() ) != null ) {
        String output = String.format( "%3d: %s", source.getLineNumber(), sourceCodeLine );
        System.out.println( output );
      }
    }
  }

  /*
   * Command-line entry point
   */
  public static void main( String args[] ) {

    String sourceFile = args[0];
    Lexer lexer;

    try {
      lexer = new Lexer( sourceFile );
      lexer.processTokens();
    } catch ( IOException | LexicalException e ) {
      System.out.println( e );
    }

  }

}