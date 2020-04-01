package parser;

import java.io.IOException;
import java.util.*;
import lexer.*;
import ast.*;
import util.DebugOptions;
import util.DebugOptions.Options;
import visitor.PrintVisitor;

public class Parser {

  private DebugOptions debugOptions;

  private Token currentToken;
  private Lexer lexer;

  private EnumSet<TokenType> startOfDeclaration =
    EnumSet.of( TokenType.INTeger, TokenType.Int, TokenType.StringType, TokenType.Char, TokenType.BOOLean );
  private EnumSet<TokenType> startOfStatement =
    EnumSet.of( TokenType.If, TokenType.While, TokenType.Return, TokenType.LeftBrace, TokenType.Identifier,
      TokenType.Switch, TokenType.Unless );
  private EnumSet<TokenType> relationalOperators =
    EnumSet.of( TokenType.Greater, TokenType.GreaterEqual, TokenType.Equal, TokenType.NotEqual, TokenType.Less, TokenType.LessEqual );
  private EnumSet<TokenType> additionOperations =
    EnumSet.of( TokenType.Plus, TokenType.Minus, TokenType.Or );
  private EnumSet<TokenType> multiplicationOperations =
    EnumSet.of( TokenType.Multiply, TokenType.Divide, TokenType.And );

  private boolean nextTokenStartsDeclaration() {
    return startOfDeclaration.contains( currentToken.getType() );
  }
  private boolean nextTokenStartsStatement() {
    TokenType t = currentToken.getType();
    return startOfStatement.contains( t );
  }

  public Lexer getLexer() {
    return lexer;
  }

  public AST execute() throws LexicalException, SyntaxException {
    AST tree = getProgramTree();
    if( debugOptions.contains( Options.AST ) ) {
      System.out.println();
      printAST( tree );
    }
    return tree;
  }

  public Parser( String sourceProgram ) throws IOException, LexicalException {
    this.debugOptions = new DebugOptions( Options.AST, Options.TOKENS );
    lexer = new Lexer( sourceProgram, this.debugOptions );
    scan();
  }

  public Parser( String sourceProgram, DebugOptions debugOptions ) throws IOException, LexicalException {
    this.debugOptions = debugOptions;
    lexer = new Lexer( sourceProgram, this.debugOptions );
    scan();
  }

  public AST getProgramTree() throws LexicalException, SyntaxException {
    // note that rProgram actually returns a ProgramTree; we use the
    // principle of substitutability to indicate it returns an AST
    AST t = new ProgramTree();
    expect( TokenType.Program );
    scan();
    t.addChild( getBlockTree() );
    return t;
  }

  public AST getBlockTree() throws LexicalException, SyntaxException {
    expect( TokenType.LeftBrace );
    scan();
    AST t = new BlockTree();
    while ( nextTokenStartsDeclaration() ) {  // get decls
      t.addChild( getDeclarationTree() );
    }
    while ( nextTokenStartsStatement() ) {  // get statements
      t.addChild( getStatementTree() );
    }
    expect( TokenType.RightBrace );
    scan();
    return t;
  }

  public AST getDeclarationTree() throws LexicalException, SyntaxException {
    AST t, t1;
    t = getTypeTree();
    t1 = getIdentifierTree();
    if ( currentTokenIsOfType( TokenType.LeftParen ) ) { // function
      t = ( new FunctionDeclTree() ).addChild( t ).addChild( t1 );
      t.addChild( getFunctionHeadTree() );
      t.addChild( getBlockTree() );
      return t;
    }
    t = ( new DeclarationTree() ).addChild( t ).addChild( t1 );
    return t;
  }

  public AST getTypeTree() throws LexicalException, SyntaxException {
    AST t;
    switch( currentToken.getType() ) {
      case Int: {
        t = new IntTypeTree();
        scan();
        return t;
      }
      case INTeger: {
        t = new IntTree( currentToken );
        scan();
        return t;
      }
      case BOOLean: {
        t = new BoolTypeTree();
        scan();
        return t;
      }
      case StringType: {
        t = new StringTypeTree();
        scan();
        return t;
      }
      case Char: {
        t = new CharTypeTree();
        scan();
        return t;
      }
      default: {
        throw new SyntaxException( lexer.getSource().getLineNumber(), "Type or Literal", currentToken.getType().name() );
      }
    }
  }

  public AST getFunctionHeadTree() throws LexicalException, SyntaxException {
    AST t = new FormalsTree();
    expect( TokenType.LeftParen );
    scan();
    if ( !currentTokenIsOfType( TokenType.RightParen ) ) {
      do {
        t.addChild( getDeclarationTree() );
        if ( currentTokenIsOfType( TokenType.Comma ) ) {
          scan();
        } else {
          break;
        }
      } while (true);
    }
    expect( TokenType.RightParen );
    scan();
    return t;
  }

  public AST getStatementTree() throws LexicalException, SyntaxException {
    AST t;
    switch ( currentToken.getType() ) {
      case Unless: {
        scan();
        t = new UnlessTree();
        t.addChild( getExpressionTree() );
        expect( TokenType.Then );
        scan();
        t.addChild( getBlockTree() );
        return t;
      }
      case If: {
        scan();
        t = new IfTree();
        t.addChild( getExpressionTree() );
        expect( TokenType.Then );
        scan();
        t.addChild( getBlockTree() );
        if( currentTokenIsOfType( TokenType.Else ) ) {
          scan();
          t.addChild( getBlockTree() );
        }
        return t;
      }
      case While: {
        scan();
        t = new WhileTree();
        t.addChild( getExpressionTree() );
        t.addChild( getBlockTree() );
        return t;
      }
      case Return: {
        scan();
        t = new ReturnTree();
        t.addChild( getExpressionTree() );
        return t;
      }
      case LeftBrace: {
        return getBlockTree();
      }
      case Switch: {
        scan();
        t = new SwitchStatementTree();
        expect( TokenType.LeftParen );
        scan();
        expect( TokenType.Identifier );
        t.addChild( getIdentifierTree() );
        expect( TokenType.RightParen );
        scan();
        t.addChild( getSwitchBlockTree() );
        return t;
      }
      case Identifier: {
        t = getIdentifierTree();
        t = ( new AssignTree() ).addChild( t );
        expect( TokenType.Assign );
        scan();
        t.addChild( getExpressionTree() );
        return t;
      }
      default: {
        throw new SyntaxException( lexer.getSource().getLineNumber(), "StartOfStatement", currentToken.getType().name() );
      }
    }
  }

  public AST getSwitchBlockTree() throws LexicalException, SyntaxException {
    expect( TokenType.LeftBrace );
    scan();
    AST t = new SwitchBlockTree();
    while ( currentTokenIsOfType( TokenType.Case ) ) {
      t.addChild( getCaseStatementTree() );
    }
    if( currentTokenIsOfType( TokenType.Default ) ) {
      t.addChild( getCaseStatementTree() );
    }
    expect( TokenType.RightBrace );
    scan();
    return t;
  }

  public AST getCaseStatementTree() throws LexicalException, SyntaxException {
    AST t;
    if( currentTokenIsOfType( TokenType.Case ) ) {
      t = new CaseStatementTree();
      scan();
      expect( TokenType.INTeger );
      t.addChild( getTypeTree() );
    } else {
      t = new DefaultStatementTree();
      expect( TokenType.Default );
      scan();
    }
    expect( TokenType.Colon );
    scan();
    t.addChild( getStatementTree() );
    return t;
  }

  public AST getExpressionTree() throws LexicalException, SyntaxException {
    AST t, child = getSimpleExpressionTree();
    t = getRelationTree();
    if ( t == null ) {
      return child;
    }
    t.addChild( child );
    t.addChild( getSimpleExpressionTree() );
    return t;
  }

  public AST getSimpleExpressionTree() throws LexicalException, SyntaxException {
    AST t, child = getTermTree();
    while ( ( t = getAdditionOperationTree() ) != null ) {
      t.addChild( child );
      t.addChild( getTermTree() );
      child = t;
    }
    return child;
  }

  public AST getTermTree() throws LexicalException, SyntaxException {
    AST t, child = getFactorTree();
    while ( (t = getMultiplicationOperationTree() ) != null ) {
      t.addChild( child );
      t.addChild( getFactorTree() );
      child = t;
    }
    return child;
  }

  public AST getFactorTree() throws LexicalException, SyntaxException {
    AST t;
    if ( currentTokenIsOfType( TokenType.LeftParen ) ) {
      scan();
      t = getExpressionTree();
      expect(TokenType.RightParen);
      scan();
      return t;
    }
    if ( currentTokenIsOfType( TokenType.INTeger ) ) {
      t = new IntTree(currentToken);
      scan();
      return t;
    }
    if( currentTokenIsOfType( TokenType.StringLit ) ) {
      t = new StringTree( currentToken );
      scan();
      return t;
    }
    if( currentTokenIsOfType( TokenType.CharLit ) ) {
      t = new CharTree( currentToken );
      scan();
      return t;
    }
    t = getIdentifierTree();
    if ( !currentTokenIsOfType( TokenType.LeftParen ) ) {
      return t;
    }
    scan();
    t = ( new CallTree() ).addChild(t);
    if ( !currentTokenIsOfType( TokenType.RightParen ) ) {
      do {
        t.addChild( getExpressionTree() );
        if ( currentTokenIsOfType( TokenType.Comma ) ) {
          scan();
        } else {
          break;
        }
      } while ( true );
    }
    expect( TokenType.RightParen );
    scan();

    return t;
  }

  public AST getIdentifierTree() throws LexicalException, SyntaxException {
    AST t;
    if ( currentTokenIsOfType( TokenType.Identifier ) ) {
      t = new IdentifierTree( currentToken );
      scan();
      return t;
    }
    throw new SyntaxException( lexer.getSource().getLineNumber(), currentToken, TokenType.Identifier );
  }

  AST getRelationTree() throws LexicalException {
    TokenType type = currentToken.getType();
    if ( relationalOperators.contains( type ) ) {
      AST t = new RelationalOperationTree( currentToken );
      scan();
      return t;
    } else {
      return null;
    }
  }

  private AST getAdditionOperationTree() throws LexicalException {
    TokenType type = currentToken.getType();
    if ( additionOperations.contains( type ) ) {
      AST t = new AdditionOperationTree( currentToken );
      scan();
      return t;
    } else {
      return null;
    }
  }

  private AST getMultiplicationOperationTree() throws LexicalException {
    TokenType type = currentToken.getType();
    if ( multiplicationOperations.contains( type ) ) {
      AST t = new MultiplicationOperationTree( currentToken );
      scan();
      return t;
    } else {
      return null;
    }
  }

  private boolean currentTokenIsOfType( TokenType type ) {
    return currentToken != null && currentToken.getType() == type;
  }

  private void expect( TokenType type ) throws SyntaxException {
    if( ! currentTokenIsOfType( type ) ) {
      throw new SyntaxException( lexer.getSource().getLineNumber(), currentToken, type );
    }
  }

  private void scan() throws LexicalException {
    currentToken = lexer.getNextToken();
  }

  private void printAST( AST tree ) {
    System.out.println("---------------AST-------------");
    tree.accept( new PrintVisitor() );
  }

  public static void main( String args[] ) {

    String sourceFile = args[0];
    Parser parser;

    try {
      parser = new Parser( sourceFile, new DebugOptions( Options.SOURCECODE, Options.TOKENS, Options.AST ) );
      parser.execute();
      System.out.println();
    } catch ( IOException | LexicalException | SyntaxException e ) {
      System.out.println( e );
    }

  }

}