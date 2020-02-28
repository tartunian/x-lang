package lexer;

import util.ErrorHandler;

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
  public Lexer( String sourceFile ) throws Exception {
    // init token table
    new TokenStore();
    source = new SourceReader( sourceFile );
    currentCharacter = source.read();
  }

  /**
   *  newIdTokens are either ids or reserved words; new id's will be inserted
   *  in the symbol table with an indication that they are id's
   *  @param id is the String just scanned - it's either an id or reserved word
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token; either an id or one for the reserved words
   */
  public Token newIdToken( String id, int lineNumber, int startPosition, int endPosition ) {
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.put( id, TokenType.Identifier )
    );
  }

  /**
   *  number tokens are inserted in the symbol table; we don't convert the
   *  numeric strings to numbers until we load the bytecodes for interpreting;
   *  this ensures that any machine numeric dependencies are deferred
   *  until we actually run the program; i.e. the numeric constraints of the
   *  hardware used to compile the source program are not used
   *  @param number is the int String just scanned
   *  @param startPosition is the column in the source file where the int begins
   *  @param endPosition is the column in the source file where the int ends
   *  @return the int Token
   */
  public Token newNumberToken( String number, int lineNumber, int startPosition, int endPosition) {
    return new Token(
      lineNumber,
      startPosition,
      endPosition,
      Symbol.put( number, TokenType.INTeger )
    );
  }

  private void consumeComments() throws IOException {
    int startingLine = source.getLineNumber();
    do {
      currentCharacter = source.read();
    } while ( source.getLineNumber() == startingLine );
  }

  /**
   *  build the token for operators (+ -) or separators (parens, braces)
   *  filter out comments which begin with two slashes
   *  @param s is the String representing the token
   *  @param startPosition is the column in the source file where the token begins
   *  @param endPosition is the column in the source file where the token ends
   *  @return the Token just found
   */
  public Token makeToken( String s, int lineNumber, int startPosition, int endPosition ) {

    // ensure it's a valid token
    Symbol symbol = Symbol.put( s, TokenType.BogusToken );

    if( symbol == null ) {
      //System.out.println( "******** illegal character: " + s );
      ErrorHandler.getInstance().logError("InvalidCharacter", lineNumber, startPosition, s);
      atEOF = true;
      return nextToken();
    }

    return new Token( lineNumber, startPosition, endPosition, symbol );
  }

  private void consumeWhiteSpace() throws IOException {
    while( Character.isWhitespace( currentCharacter )) {
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

    return newIdToken( id, source.getLineNumber(), startPosition, endPosition );
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

    return newNumberToken( number, source.getLineNumber(), startPosition, endPosition );
  }

  private Token getStringLiteralToken() throws IOException {
    String word = "";

    do {
      word += currentCharacter;
      currentCharacter = source.read();
      endPosition++;
    } while ( currentCharacter != '\"' );

    currentCharacter = source.read();
    endPosition++;
    word += "\"";

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( word, TokenType.StringLit ) );
  }

  private Token getCharacterLiteralToken() throws IOException {
    String c = "";

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    if( currentCharacter != '\'' ) {
      System.out.println( String.format( "******** illegal character: line: %-8d column: %-8d %-2s", source.getLineNumber(), startPosition, currentCharacter ) );
      return null;
    }

    currentCharacter = source.read();
    endPosition++;

    c += '\'';

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( c, TokenType.CharLit ) );
  }

  private Token getTwoCharToken() throws IOException {
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
      return makeToken( Character.toString( firstCharacter ), source.getLineNumber(), startPosition, endPosition );
    }

    endPosition++;
    currentCharacter = source.read();

    return makeToken( twoCharToken, source.getLineNumber(), startPosition, endPosition );
  }

  private Token getEndOfFileToken() {
    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put("XD", TokenType.EndProgram ) );
  }

  /**
   *  @return the next Token found in the source file
   */
  public Token nextToken() {

    try {
      consumeWhiteSpace();
      if ( Character.isJavaIdentifierStart( currentCharacter ) ) {
        return getIdentifierToken();
      } else if ( Character.isLetter( currentCharacter ) ) {
        return getKeywordToken();
      } else if ( currentCharacter == '\"') {
        return getStringLiteralToken();
      } else if ( currentCharacter == '\'' ) {
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
    Lexer lexer;

    try {
      lexer = new Lexer( sourceFile );
      Token token;

      while( ( token = lexer.nextToken() ).getType() != TokenType.EndProgram ) {
        System.out.println( token );
      }

      System.out.println();

      // Reset the SourceReader to the beginning of the source file.
      lexer.source.reset();

      // Print out the full source code.
      String sourceCodeLine;
      while( ( sourceCodeLine = lexer.source.readLine() ) != null ) {
        String s = String.format( "%3s: %s", lexer.source.getLineNumber(), sourceCodeLine );
        System.out.println( s );
      }

    } catch (Exception e) { System.out.println( e ); }

  }

}