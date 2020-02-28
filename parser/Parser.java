package parser;

import java.util.*;
import lexer.*;
import ast.*;

public class Parser {

    private Token currentToken;
    private Lexer lexer;
    private EnumSet<TokenType> relationalOperators
            = EnumSet.of( TokenType.Equal, TokenType.NotEqual, TokenType.Less, TokenType.LessEqual );
    private EnumSet<TokenType> additionOperators
            = EnumSet.of( TokenType.Plus, TokenType.Minus, TokenType.Or );
    private EnumSet<TokenType> multiplicationOperators
            = EnumSet.of( TokenType.Multiply, TokenType.Divide, TokenType.And );

    public Parser( String sourceProgram ) throws Exception {
        try {
            lexer = new Lexer( sourceProgram );
            advanceToken();
        } catch ( Exception e ) {
            System.out.println( "********exception*******" + e.toString() );
            throw e;
        }
    }

    public Lexer getLexer() {
        return lexer;
    }

    public AST getTree() throws Exception {
        try {
            return getProgramTree();
        } catch ( SyntaxError e ) {
            System.out.println( e );
            throw e;
        }
    }

    public AST getProgramTree() throws SyntaxError {
        // note that rProgram actually returns a ProgramTree; we use the 
        // principle of substitutability to indicate it returns an AST
        AST t = new ProgramTree();
        checkCurrentTokenAndAdvance( TokenType.Program );
        t.addChild( getBlockTree() );
        return t;
    }

    public AST getBlockTree() throws SyntaxError {
        checkCurrentTokenAndAdvance( TokenType.LeftBrace );
        AST t = new BlockTree();
        while ( checkStartingDeclaration() ) {  // get decls
                t.addChild( getDeclarationTree() );
        }
        while ( checkStartingStatement() ) {  // get statements
                t.addChild( getStatementTree() );
        }
        checkCurrentTokenAndAdvance( TokenType.RightBrace );
        return t;
    }

    /*
     * Check for valid first line declarations?
     */
    boolean checkStartingDeclaration() {
        if ( isCurrentTokenOfType( TokenType.Int ) || isCurrentTokenOfType( TokenType.BOOLean ) ) {
            return true;
        }
        return false;
    }

    boolean checkStartingStatement() {
        if ( isCurrentTokenOfType( TokenType.If ) || isCurrentTokenOfType( TokenType.While ) ||
                isCurrentTokenOfType( TokenType.Return ) || isCurrentTokenOfType( TokenType.LeftBrace ) ||
                isCurrentTokenOfType( TokenType.Identifier )) {
            return true;
        }
        return false;
    }

    public AST getDeclarationTree() throws SyntaxError {
        AST typeTree, identifierTree;
        typeTree = getIntBoolTypeTree();
        identifierTree = getIdentifierTree();
        if ( isCurrentTokenOfType( TokenType.LeftParen ) ) { // function
            typeTree = ( new FunctionDeclTree() ).addChild( typeTree ).addChild( identifierTree );
            typeTree.addChild( getFormalsTree() );
            typeTree.addChild( getBlockTree() );
            return typeTree;
        }
        typeTree = ( new DeclTree() ).addChild( typeTree ).addChild( identifierTree );
        return typeTree;
    }

    public AST getIntBoolTypeTree() throws SyntaxError {
        AST t;
        if ( isCurrentTokenOfType( TokenType.Int ) ) {
            t = new IntTypeTree();
            advanceToken();
        } else {
            checkCurrentTokenAndAdvance( TokenType.BOOLean );
            t = new BoolTypeTree();
        }
        return t;
    }

    public AST getFormalsTree() throws SyntaxError {
        AST t = new FormalsTree();
        checkCurrentTokenAndAdvance( TokenType.LeftParen );
        if ( !isCurrentTokenOfType( TokenType.RightParen ) ) {
            do {
                t.addChild( getDeclarationTree() );
                if ( isCurrentTokenOfType( TokenType.Comma ) ) {
                    advanceToken();
                } else {
                    break;
                }
            } while ( true );
        }
        checkCurrentTokenAndAdvance( TokenType.RightParen );
        return t;
    }

    public AST getStatementTree() throws SyntaxError {
        AST t;
        if ( isCurrentTokenOfType( TokenType.If ) ) {
            advanceToken();
            t = new IfTree();
            t.addChild( getExpressionTree() );
            checkCurrentTokenAndAdvance( TokenType.Then );
            t.addChild( getBlockTree() );
            checkCurrentTokenAndAdvance( TokenType.Else );
            t.addChild( getBlockTree() );
            return t;
        } else if ( isCurrentTokenOfType( TokenType.While ) ) {
            advanceToken();
            t = new WhileTree();
            t.addChild( getExpressionTree() );
            t.addChild( getBlockTree() );
            return t;
        } else if ( isCurrentTokenOfType( TokenType.Return ) ) {
            advanceToken();
            t = new ReturnTree();
            t.addChild( getExpressionTree() );
            return t;
        } else if ( isCurrentTokenOfType( TokenType.LeftBrace ) ) {
            return getBlockTree();
        } else {
            t = getIdentifierTree();
            t = ( new AssignTree() ).addChild( t );
            checkCurrentTokenAndAdvance( TokenType.Assign );
            t.addChild( getExpressionTree() );
            return t;
        }
    }

    public AST getExpressionTree() throws SyntaxError {
        AST t, child = getSimpleExpressionTree();
        t = getRelationTree();
        if ( t == null ) {
            return child;
        }
        t.addChild( child );
        t.addChild( getSimpleExpressionTree() );
        return t;
    }

    public AST getSimpleExpressionTree() throws SyntaxError {
        AST t, child = getMultiplicationExpressionTree();
        while ( ( t = getAdditionOperationTree() ) != null ) {
            t.addChild( child );
            t.addChild( getMultiplicationExpressionTree() );
            child = t;
        }
        return child;
    }

    public AST getMultiplicationExpressionTree() throws SyntaxError {
        AST t, child = getFactorTree();
        while ( ( t = getMultiplicationOperationTree() ) != null ) {
            t.addChild( child );
            t.addChild( getFactorTree() );
            child = t;
        }
        return child;
    }

    public AST getFactorTree() throws SyntaxError {
        AST t;
        if ( isCurrentTokenOfType( TokenType.LeftParen ) ) {
            advanceToken();
            t = getExpressionTree();
            checkCurrentTokenAndAdvance( TokenType.RightParen );
            return t;
        }
        if ( isCurrentTokenOfType( TokenType.INTeger ) ) {
            t = new IntTree( currentToken );
            advanceToken();
            return t;
        }
        t = getIdentifierTree();
        if ( !isCurrentTokenOfType( TokenType.LeftParen ) ) {
            return t;
        }
        advanceToken();
        t = ( new CallTree() ).addChild( t );
        if ( !isCurrentTokenOfType( TokenType.RightParen ) ) {
            do {
                t.addChild( getExpressionTree() );
                if ( isCurrentTokenOfType( TokenType.Comma ) ) {
                    advanceToken();
                } else {
                    break;
                }
            } while ( true );
        }
        checkCurrentTokenAndAdvance( TokenType.RightParen );
        return t;
    }

    public AST getIdentifierTree() throws SyntaxError {
        AST t;
        if ( isCurrentTokenOfType( TokenType.Identifier ) ) {
            t = new IdTree( currentToken );
            advanceToken();
            return t;
        }
        throw new SyntaxError( currentToken, TokenType.Identifier );
    }

    AST getRelationTree() {
        TokenType type = currentToken.getType();
        if ( relationalOperators.contains( type ) ) {
            AST t = new RelOpTree( currentToken );
            advanceToken();
            return t;
        } else {
            return null;
        }
    }

    private AST getAdditionOperationTree() {
        TokenType type = currentToken.getType();
        if ( additionOperators.contains( type ) ) {
            AST t = new AddOpTree( currentToken );
            advanceToken();
            return t;
        } else {
            return null;
        }
    }

    private AST getMultiplicationOperationTree() {
        TokenType type = currentToken.getType();
        if ( multiplicationOperators.contains( type ) ) {
            AST t = new MultOpTree( currentToken );
            advanceToken();
            return t;
        } else {
            return null;
        }
    }

    private boolean isCurrentTokenOfType( TokenType type ) {
        if ( ( currentToken == null ) || ( currentToken.getType() != type ) ) {
            return false;
        }
        return true;
    }

    private void checkCurrentTokenAndAdvance( TokenType type ) throws SyntaxError {
        if ( isCurrentTokenOfType( type ) ) {
            advanceToken();
            return;
        }
        throw new SyntaxError( currentToken, type );
    }

    private void advanceToken() {
        currentToken = lexer.nextToken();
        if ( currentToken.getType() != TokenType.EndProgram ) {
            System.out.println( currentToken );
        }
    }

    public static void main( String args[] ) {
        String sourceFile = args[0];
        Parser parser;

        try {
            parser = new Parser( sourceFile );
            parser.getTree();
        } catch ( Exception e ) { }
    }

}