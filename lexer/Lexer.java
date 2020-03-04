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
    new TokenType();
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

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( id, Tokens.Identifier ) );
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

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( number, Tokens.INTeger ) );
  }

  private Token getStringLiteralToken() throws IOException, LexicalException {
    String word = "";
    int startingLineNumber = source.getLineNumber();

    do {
      word += currentCharacter;
      currentCharacter = source.read();
      endPosition++;
      if( source.getLineNumber() != startingLineNumber ) {
        throw new LexicalException( String.format( "******** illegal characters: %s", word ) );
        /*System.out.println( String.format( "******** illegal characters: %s", word ) );
        errorLineNumber = startingLineNumber;
        word += '\"';
        return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( word, Tokens.StringLit ) );*/
      }
    } while ( currentCharacter != '\"' );

    currentCharacter = source.read();
    endPosition++;
    word += "\"";

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( word, Tokens.StringLit ) );
  }

  private Token getCharacterLiteralToken() throws IOException, LexicalException {
    String c = "";
    int startingLineNumber = source.getLineNumber();

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    c += currentCharacter;

    currentCharacter = source.read();
    endPosition++;

    if( currentCharacter != '\'' ) {
      throw new LexicalException( String.format( "******** illegal characters: %s", c ) );
      /*System.out.println( String.format( "******** illegal characters: %s", c ) );
      c += '\'';
      return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( c, Tokens.CharLit ) );*/
    }

    currentCharacter = source.read();
    endPosition++;

    c += '\'';

    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put( c, Tokens.CharLit ) );
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

    Symbol sym = Symbol.put( twoCharToken, Tokens.BogusToken );
    if ( sym == null ) {
      sym = Symbol.put( Character.toString( firstCharacter ), Tokens.BogusToken );
      if ( sym == null ) {
        throw new LexicalException( "******** illegal character: " + firstCharacter );
        /*System.out.println( "******** illegal character: " + firstCharacter );
        atEOF = true;
        return nextToken();*/
      } else {
        return new Token( source.getLineNumber(), startPosition, endPosition, sym );
      }
    } else {

      endPosition++;
      currentCharacter = source.read();

      sym = Symbol.put( twoCharToken, Tokens.BogusToken );
      if ( sym == null ) {
        throw new LexicalException( "******** illegal character: " + twoCharToken );
        /*System.out.println( "******** illegal character: " + twoCharToken );
        atEOF = true;
        return nextToken();*/
      } else {
        return new Token( source.getLineNumber(), startPosition, endPosition, sym );
      }
    }
  }

  private Token getEndOfFileToken() {
    return new Token( source.getLineNumber(), startPosition, endPosition, Symbol.put("XD", Tokens.EndProgram ) );
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
    Lexer lexer = null;

    try {
      lexer = new Lexer( sourceFile );
      Token token;

      while( ( token = lexer.nextToken() ).getKind() != Tokens.EndProgram ) {
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