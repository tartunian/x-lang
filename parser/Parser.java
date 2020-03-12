package parser;

import java.util.*;
import lexer.*;
import ast.*;
import visitor.PrintVisitor;

/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the <b>Abstract Syntax Tree</b> representation for the source
 * program<br>
 * Following is the Grammar we are using:<br>
 * <pre>
 *  PROGRAM -> �program� BLOCK ==> program
 *
 *  BLOCK -> �{� D* S* �}�  ==> block
 *
 *  D -> TYPE NAME                    ==> decl
 *    -> TYPE NAME FUNHEAD BLOCK      ==> functionDecl
 *
 *  TYPE  ->  �int�
 *        ->  �boolean�
 *
 *  FUNHEAD  -> '(' (D list ',')? ')'  ==> formals<br>
 *
 *  S -> �if� E �then� BLOCK �else� BLOCK  ==> if
 *    -> �while� E BLOCK               ==> while
 *    -> �return� E                    ==> return
 *    -> BLOCK
 *    -> NAME �=� E                    ==> assign<br>
 *
 *  E -> SE
 *    -> SE �==� SE   ==> =
 *    -> SE �!=� SE   ==> !=
 *    -> SE �<�  SE   ==> <
 *    -> SE �<=� SE   ==> <=
 *
 *  SE  ->  T
 *      ->  SE �+� T  ==> +
 *      ->  SE �-� T  ==> -
 *      ->  SE �|� T  ==> or
 *
 *  T  -> F
 *     -> T �*� F  ==> *
 *     -> T �/� F  ==> /
 *     -> T �&� F  ==> and
 *
 *  F  -> �(� E �)�
 *     -> NAME
 *     -> <int>
 *     -> NAME '(' (E list ',')? ')' ==> call<br>
 *
 *  NAME  -> <id>
 * </pre>
 */
public class Parser {

    private Token currentToken;
    private Lexer lexer;
    private EnumSet<TokenType> relationalOps
            = EnumSet.of(TokenType.Equal, TokenType.NotEqual, TokenType.Less, TokenType.LessEqual);
    private EnumSet<TokenType> addingOps
            = EnumSet.of(TokenType.Plus, TokenType.Minus, TokenType.Or);
    private EnumSet<TokenType> multiplyingOps
            = EnumSet.of(TokenType.Multiply, TokenType.Divide, TokenType.And);

    /**
     * Construct a new Parser;
     *
     * @param sourceProgram - source file name
     * @exception Exception - thrown for any problems at startup (e.g. I/O)
     */
    public Parser(String sourceProgram) throws Exception {
        try {
            lexer = new Lexer(sourceProgram);
            scan();
        } catch (Exception e) {
            System.out.println("********exception*******" + e.toString());
            throw e;
        };
    }

    public Lexer getLexer() {
        return lexer;
    }

    /**
     * Execute the parse command
     *
     * @return the AST for the source program
     * @exception Exception - pass on any type of exception raised
     */
    public AST execute() throws Exception {
        try {
            return rProgram();
        } catch (SyntaxError e) {
            e.print();
            throw e;
        }
    }

    /**
     * <
     * pre>
     * Program -> 'program' block ==> program
     * </pre>
     *
     * @return the program tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rProgram() throws LexicalException, SyntaxError {
        // note that rProgram actually returns a ProgramTree; we use the 
        // principle of substitutability to indicate it returns an AST
        AST t = new ProgramTree();
        expect( TokenType.Program );
        t.addChild( rBlock() );
        return t;
    }

    /**
     * <
     * pre>
     * block -> '{' d* s* '}' ==> block
     * </pre>
     *
     * @return block tree
     * @exception SyntaxError - thrown for any syntax error e.g. an expected
     * left brace isn't found
     */
    public AST rBlock() throws LexicalException, SyntaxError {
        expect( TokenType.LeftBrace );
        AST t = new BlockTree();
        while ( startingDecl() ) {  // get decls
                t.addChild( rDecl() );
        }
        while ( startingStatement() ) {  // get statements
                t.addChild( rStatement() );
        }
        expect( TokenType.RightBrace );
        return t;
    }

    boolean startingDecl() {
        if (isNextTok(TokenType.Int) || isNextTok(TokenType.BOOLean)) {
            return true;
        }
        return false;
    }

    boolean startingStatement() {
        if (isNextTok(TokenType.If) || isNextTok(TokenType.While) || isNextTok(TokenType.Return)
                || isNextTok(TokenType.LeftBrace) || isNextTok(TokenType.Identifier)) {
            return true;
        }
        return false;
    }

    /**
     * <
     * pre>
     * d -> type name ==> decl -> type name funcHead block ==> functionDecl
     * </pre>
     *
     * @return either the decl tree or the functionDecl tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rDecl() throws LexicalException, SyntaxError {
        AST t, t1;
        t = rType();
        t1 = rName();
        if (isNextTok(TokenType.LeftParen)) { // function
            t = (new FunctionDeclTree()).addChild(t).addChild(t1);
            t.addChild(rFunHead());
            t.addChild(rBlock());
            return t;
        }
        t = (new DeclTree()).addChild(t).addChild(t1);
        return t;
    }

    /**
     * <
     * pre>
     * type -> 'int' type -> 'bool'
     * </pre>
     *
     * @return either the intType or boolType tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rType() throws LexicalException, SyntaxError {
        AST t;
        if (isNextTok(TokenType.Int)) {
            t = new IntTypeTree();
            scan();
        } else {
            expect(TokenType.BOOLean);
            t = new BoolTypeTree();
        }
        return t;
    }

    /**
     * <
     * pre>
     * funHead -> '(' (decl list ',')? ')' ==> formals note a funhead is a list
     * of zero or more decl's separated by commas, all in parens
     * </pre>
     *
     * @return the formals tree describing this list of formals
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rFunHead() throws LexicalException, SyntaxError {
        AST t = new FormalsTree();
        expect(TokenType.LeftParen);
        if (!isNextTok(TokenType.RightParen)) {
            do {
                t.addChild(rDecl());
                if (isNextTok(TokenType.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(TokenType.RightParen);
        return t;
    }

    /**
     * <
     * pre>
     * S -> 'if' e 'then' block 'else' block ==> if -> 'while' e block ==> while
     * -> 'return' e ==> return -> block -> name '=' e ==> assign
     * </pre>
     *
     * @return the tree corresponding to the statement found
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rStatement() throws LexicalException, SyntaxError {
        AST t;
        if (isNextTok(TokenType.If)) {
            scan();
            t = new IfTree();
            t.addChild(rExpr());
            expect(TokenType.Then);
            t.addChild(rBlock());
            expect(TokenType.Else);
            t.addChild(rBlock());
            return t;
        }
        if (isNextTok(TokenType.While)) {
            scan();
            t = new WhileTree();
            t.addChild(rExpr());
            t.addChild(rBlock());
            return t;
        }
        if (isNextTok(TokenType.Return)) {
            scan();
            t = new ReturnTree();
            t.addChild(rExpr());
            return t;
        }
        if (isNextTok(TokenType.LeftBrace)) {
            return rBlock();
        }
        t = rName();
        t = (new AssignTree()).addChild(t);
        expect(TokenType.Assign);
        t.addChild(rExpr());
        return t;
    }

    /**
     * <
     * pre>
     * e -> se -> se '==' se ==> = -> se '!=' se ==> != -> se '<' se ==> < -> se
     * '<=' se ==> <= </pre> @return the tree corresponding to the expression
     *
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rExpr() throws LexicalException, SyntaxError {
        AST t, kid = rSimpleExpr();
        t = getRelationTree();
        if (t == null) {
            return kid;
        }
        t.addChild(kid);
        t.addChild(rSimpleExpr());
        return t;
    }

    /**
     * <
     * pre>
     * se -> t -> se '+' t ==> + -> se '-' t ==> - -> se '|' t ==> or This rule
     * indicates we should pick up as many <i>t</i>'s as possible; the
     * <i>t</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the adding expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rSimpleExpr() throws LexicalException, SyntaxError {
        AST t, kid = rTerm();
        while ((t = getAddOperTree()) != null) {
            t.addChild(kid);
            t.addChild(rTerm());
            kid = t;
        }
        return kid;
    }

    /**
     * <
     * pre>
     * t -> f -> t '*' f ==> * -> t '/' f ==> / -> t '&' f ==> and This rule
     * indicates we should pick up as many <i>f</i>'s as possible; the
     * <i>f</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the multiplying expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rTerm() throws LexicalException, SyntaxError {
        AST t, kid = rFactor();
        while ((t = getMultOperTree()) != null) {
            t.addChild(kid);
            t.addChild(rFactor());
            kid = t;
        }
        return kid;
    }

    /**
     * <
     * pre>
     * f -> '(' e ')' -> name -> <int>
     * -> name '(' (e list ',')? ')' ==> call
     * </pre>
     *
     * @return the tree corresponding to the factor expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rFactor() throws LexicalException, SyntaxError {
        AST t;
        if (isNextTok(TokenType.LeftParen)) { // -> (e)
            scan();
            t = rExpr();
            expect(TokenType.RightParen);
            return t;
        }
        if (isNextTok(TokenType.INTeger)) {  //  -> <int>
            t = new IntTree(currentToken);
            scan();
            return t;
        }
        t = rName();
        if (!isNextTok(TokenType.LeftParen)) {  //  -> name
            return t;
        }
        scan();     // -> name '(' (e list ',')? ) ==> call
        t = (new CallTree()).addChild(t);
        if (!isNextTok(TokenType.RightParen)) {
            do {
                t.addChild(rExpr());
                if (isNextTok(TokenType.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(TokenType.RightParen);
        return t;
    }

    /**
     * <
     * pre>
     * name -> <id>
     * </pre>
     *
     * @return the id tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rName() throws LexicalException, SyntaxError {
        AST t;
        if (isNextTok(TokenType.Identifier)) {
            t = new IdTree(currentToken);
            scan();
            return t;
        }
        throw new SyntaxError(currentToken, TokenType.Identifier);
    }

    AST getRelationTree() throws LexicalException {  // build tree with current token's relation
        TokenType kind = currentToken.getType();
        if (relationalOps.contains(kind)) {
            AST t = new RelOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getAddOperTree() throws LexicalException {
        TokenType kind = currentToken.getType();
        if (addingOps.contains(kind)) {
            AST t = new AddOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getMultOperTree() throws LexicalException {
        TokenType kind = currentToken.getType();
        if (multiplyingOps.contains(kind)) {
            AST t = new MultOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private boolean isNextTok( TokenType type ) {
        return ( currentToken == null || currentToken.getType() != type ) ? false : true;
    }

    private void expect( TokenType kind ) throws LexicalException, SyntaxError {
        if ( isNextTok( kind ) ) {
            scan();
            return;
        }
        throw new SyntaxError( currentToken, kind );
    }

    private void scan() throws LexicalException {
        currentToken = lexer.nextToken();
        if ( currentToken != null && currentToken.getType() != TokenType.EndProgram ) {
            System.out.println( currentToken );
        }
        return;
    }

    public static void main( String args[] ) {
        String sourceFile = args[0];
        Parser parser;

        try {
            parser = new Parser( sourceFile );
            AST tree = parser.execute();

            System.out.println();
            System.out.println( "---------------AST-------------" );

            new PrintVisitor().print( "Program" , tree);
        } catch ( Exception e ) {
        }
    }

}