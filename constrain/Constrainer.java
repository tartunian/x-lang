package constrain;

import lexer.*;
import parser.Parser;
import visitor.*;
import ast.*;
import java.util.*;

/**
 * Constrainer object will visit the AST, gather/check variable type information
 * and decorate uses of variables with their declarations; the decorations will
 * be used by the code generator to provide access to the frame offset of the
 * variable for generating load/store bytecodes; <br>
 * Note that when constraining expression trees we return the type tree
 * corresponding to the result type of the expression; e.g. the result of
 * constraining the tree for 1+2*3 will be the int type tree
 */
public class Constrainer extends ASTVisitor {
  public enum ConstrainerErrors {
    BadAssignmentType, SwitchTypeMismatch, CallingNonFunction, ActualFormalTypeMismatch, NumberActualsFormalsDiffer, TypeMismatchInExpr,
    BooleanExprExpected, BadConditional, ReturnNotInFunction, BadReturnExpr
  }

  private AST t; // the AST to constrain
  private Table symtab = new Table();
  private Parser parser; // parser used with this constrainer

  /**
   * The following comment refers to the functions stack declared below the
   * comment. Whenever we start constraining a function declaration we push the
   * function decl tree which indicates we're in a function (to ensure that we
   * don't attempt to return from the main program - return's are only allowed
   * from within functions); it also gives us access to the return type to ensure
   * the type of the expr that is returned is the same as the type declared in the
   * function header
   */
  private Stack<AST> functions = new Stack<AST>();

  /**
   * readTree, writeTree, intTree, boolTree,falseTree, trueTree are AST's that
   * will be constructed (intrinsic trees) for every program. They are constructed
   * in the same fashion as source program trees to ensure consisten processing of
   * functions, etc.
   */
  public static AST readTree, writeTree, intTree, boolTree, charTree, stringTree, falseTree, trueTree, readId, writeId;

  public Constrainer(AST t, Parser parser) {
    this.t = t;
    this.parser = parser;
  }

  public void execute() {
    symtab.beginScope();
    t.accept(this);
  }

  /**
   * t is an IdTree; retrieve the pointer to its declaration
   */
  private AST lookup(AST t) {
    //System.out.println( String.format( "  Constrainer: Looking for %s in symtab.", ((IdentifierTree) t).getSymbol() ) );
    return (AST) ( symtab.get( ( ( IdentifierTree ) t ).getSymbol() ) );
  }

  /**
   * Decorate the IdTree with the given decoration - its decl tree
   */
  private void enter(AST t, AST decoration) {
    //System.out.println( String.format( "  Constrainer: Putting %s into symtab.", ((IdentifierTree) t).getSymbol() ) );
    symtab.put( ( ( IdentifierTree ) t ).getSymbol(), decoration);
  }

  /**
   * get the type of the current type tree
   *
   * @param t is the type tree
   * @return the intrinsic tree corresponding to the type of t
   */
  private AST getType(AST t) {
    if( t.getClass() == IntTypeTree.class ) {
      return intTree;
    } else if( t.getClass() == BoolTypeTree.class ) {
      return boolTree;
    } else if( t.getClass() == CharTypeTree.class ) {
      return charTree;
    } else if( t.getClass() == StringTypeTree.class ) {
      return stringTree;
    } else {
      return null;
    }
  }

  private Class getLiteralTypeFromType(AST t) {
    if( t.getClass() == IntTypeTree.class ) {
      return IntTree.class;
    } else if( t.getClass() == BoolTypeTree.class ) {
      return null;
    } else if( t.getClass() == CharTypeTree.class ) {
      return CharTree.class;
    } else if( t.getClass() == StringTypeTree.class ) {
      return StringTree.class;
    } else {
      return null;
    }
  }

  public void decorate(AST t, AST decoration) {
    t.setDecoration(decoration);
  }

  /**
   * @return the decoration of the tree
   */
  public AST decoration(AST t) {
    return t.getDecoration();
  }

  /**
   * build the intrinsic trees; constrain them in the same fashion as any other
   * AST
   */
  private void buildIntrinsicTrees() {
    trueTree = new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "true", TokenType.Identifier ) ) );
    // Is this the right way to get 'true' into the symtab?
    //enter( trueTree, trueTree );
    falseTree = new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "false", TokenType.Identifier ) ) );
    readId = new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "read", TokenType.Identifier ) ) );
    writeId = new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "write", TokenType.Identifier ) ) );
    boolTree = ( new DeclarationTree() ).addChild( new BoolTypeTree() ).addChild( new IdentifierTree( new Token( -1, -1, -1, Symbol.put( "<<bool>>", TokenType.Identifier ) ) ) );
    decorate( boolTree.getChild( 1 ), boolTree );
    intTree = ( new DeclarationTree() ).addChild( new IntTypeTree() ).addChild( new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "<<int>>", TokenType.Identifier ) ) ) );
    charTree = ( new DeclarationTree() ).addChild( new CharTypeTree() ).addChild( new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "<<char>>", TokenType.Identifier ) ) ) );
    stringTree = ( new DeclarationTree() ).addChild( new StringTypeTree() ).addChild( new IdentifierTree( new Token(-1, -1, -1, Symbol.put( "<<string>>", TokenType.Identifier ) ) ) );

    decorate( intTree.getChild( 1 ), intTree );
    // to facilitate type checking; this ensures int decls and id decls
    // have the same structure

    // read tree takes no parms and returns an int
    readTree = ( new FunctionDeclTree() ).addChild( new IntTypeTree() ).addChild( readId ).addChild( new FormalsTree() ).addChild(new BlockTree());

    // write tree takes one int parm and returns that value
    writeTree = (new FunctionDeclTree()).addChild(new IntTypeTree()).addChild(writeId);
    AST decl = (new DeclarationTree()).addChild(new IntTypeTree())
            .addChild(new IdentifierTree(new Token(-1, -1, -1, Symbol.put("dummyFormal", TokenType.Identifier))));
    AST formals = (new FormalsTree()).addChild(decl);
    writeTree.addChild(formals).addChild(new BlockTree());
    writeTree.accept(this);
    readTree.accept(this);

//    System.out.println( String.format("readTree: %s", readTree) );
//    System.out.println( String.format("writeTree: %s", writeTree) );
//    System.out.println( String.format("intTree: %s", intTree) );
//    System.out.println( String.format("boolTree: %s", boolTree) );
//    System.out.println( String.format("charTree: %s", charTree) );
//    System.out.println( String.format("stringTree: %s", stringTree) );

//    PrintVisitor pv = new PrintVisitor();
//    pv.print( "readTree", readTree );
//    pv.print( "writeTree", writeTree );
//    pv.print( "intTree", intTree );
//    pv.print( "boolTree", boolTree );
//    pv.print( "charTree", charTree );
//    pv.print( "stringTree", stringTree );
  }

  /**
   * Constrain the program tree - visit its kid
   */
  @Override
  public Object visitProgramTree(AST t) {
    buildIntrinsicTrees();
    this.t = t;
    t.getChild(0).accept(this);
    return null;
  }

  /**
   * Constrain the Block tree:<br>
   * <ol>
   * <li>open a new scope,
   * <li>constrain the kids in this new scope,
   * <li>close the scope removing any local declarations from this scope
   * </ol>
   */
  @Override
  public Object visitBlockTree(AST t) {
    symtab.beginScope();
    visitChildren(t);
    symtab.endScope();
    return null;
  }

  /**
   * Constrain the FunctionDeclTree:
   * <ol>
   * <li>Enter the function name in the current scope,
   * <li>enter the formals in the function scope and
   * <li>constrain the body of the function
   * </ol>
   */
  @Override
  public Object visitFunctionDeclarationTree(AST t) {
    AST     fname = t.getChild(1),
            returnType = t.getChild(0),
            formalsTree = t.getChild(2),
            bodyTree = t.getChild(3);
    functions.push(t);
    enter(fname, t); // enter function name in CURRENT scope
    decorate(returnType, getType(returnType));
    symtab.beginScope(); // new scope for formals and body
    visitChildren(formalsTree); // all formal names go in new scope
    bodyTree.accept(this);
    symtab.endScope();
    functions.pop();
    return null;
  }

  /**
   * Constrain the Call tree:<br>
   * check that the number and types of the actuals match the number and type of
   * the formals
   */
  @Override
  public Object visitCallTree(AST t) {
    AST fct, fname = t.getChild(0), fctType;
    visitChildren(t);
    fct = lookup(fname);
    if (fct.getClass() != FunctionDeclTree.class) {
      constraintError(ConstrainerErrors.CallingNonFunction, t);
    }
    fctType = decoration(fct.getChild(0));
    decorate(t, fctType);
    decorate(t.getChild(0), fct);
    // now check that the number/types of actuals match the
    // number/types of formals
    checkArgDecls(t, fct);
    return fctType;
  }

  private void checkArgDecls(AST caller, AST fct) {
    // check number and types of args/formals match
    AST formals = fct.getChild(2);
    Iterator<AST> actualKids = caller.getChildren().iterator(), formalKids = formals.getChildren().iterator();
    actualKids.next(); // skip past fct name
    for (; actualKids.hasNext();) {
      try {
        AST actualDecl = decoration(actualKids.next()), formalDecl = formalKids.next();
        if (decoration(actualDecl.getChild(1)) != decoration(formalDecl.getChild(1))) {
          constraintError(ConstrainerErrors.ActualFormalTypeMismatch, t);
        }
      } catch (Exception e) {
        constraintError(ConstrainerErrors.NumberActualsFormalsDiffer, t);
      }
    }
    if (formalKids.hasNext()) {
      constraintError(ConstrainerErrors.NumberActualsFormalsDiffer, t);
    }
    return;
  }

  /**
   * Constrain the Decl tree:<br>
   * <ol>
   * <li>decorate to the corresponding intrinsic type tree,
   * <li>enter the variable in the current scope so later variable references can
   * retrieve the information in this tree
   * </ol>
   */
  @Override
  public Object visitDeclarationTree(AST t) {
    AST idTree = t.getChild(1);
    enter(idTree, t);
    AST typeTree = getType(t.getChild(0));
    decorate( idTree, typeTree );
    //System.out.println(String.format("DeclTree %s: Decorating: %s (%s) with %s", t, idTree, idTree.getLabel(), typeTree));
    return null;
  }

  /**
   * Constrain the <i>If</i> tree:<br>
   * check that the first kid is an expression that is a boolean type
   */
  @Override
  public Object visitIfTree( AST t ) {
    //System.out.println( t.getChild( 0 ) );
    if( t.getChild( 0 ).accept( this ) != boolTree ) {
      constraintError( ConstrainerErrors.BadConditional, t );
    }
    t.getChild( 1 ).accept( this );
    if( t.getChildCount() > 2 ) {
      t.getChild( 2 ).accept(this);
    }
    return null;
  }

  @Override
  public Object visitUnlessTree( AST t ) {
    return null;
  }

  @Override
  public Object visitWhileTree(AST t) {
    if ( t.getChild( 0 ).accept( this ) != boolTree ) {
      constraintError( ConstrainerErrors.BadConditional, t );
    }
    t.getChild( 1 ).accept( this );
    return null;
  }

  /**
   * Constrain the Return tree:<br>
   * Check that the returned expression type matches the type indicated in the
   * function we're returning from
   */
  @Override
  public Object visitReturnTree(AST t) {
    if ( functions.empty() ) {
      constraintError( ConstrainerErrors.ReturnNotInFunction, t );
    }
    AST currentFunction = ( functions.peek() );
    decorate( t, currentFunction );
    AST returnType = decoration( currentFunction.getChild( 0 ) );
    if ( ( t.getChild( 0 ).accept( this ) ) != returnType ) {
      constraintError( ConstrainerErrors.BadReturnExpr, t );
    }
    return null;
  }

  @Override
  public Object visitSwitchStatementTree(AST t) {
    System.out.println( "visitSwitchStatementTree" );
    AST idType = lookup( t.getChild(0) ).getChild(1).getDecoration().getChild(0);
    if( idType!= intTree.getChild(0) ) {
      constraintError( ConstrainerErrors.ActualFormalTypeMismatch, t );
    }
    IdentifierTree id = (IdentifierTree)t.getChild(0);
    id.accept( this );
    AST switchBlock = t.getChild(1);
    decorate( switchBlock, t.getChild(0) );
    switchBlock.accept( this );
    return null;
  }

  @Override
  public Object visitSwitchBlockTree(AST t) {
    System.out.println( "visitSwitchBlockTree" );
    ArrayList<AST> caseStatements = t.getChildren();
    for ( AST child : caseStatements ) {
      decorate( child, t.getDecoration() );
      child.accept( this );
    }
    return null;
  }

  @Override
  public Object visitCaseStatementTree(AST t) {
    System.out.println( "visitCaseStatementTree" );
    AST switcherandDeclaration = lookup( t.getDecoration() ).getChild(0);
    Class expectedLiteralType = getLiteralTypeFromType( switcherandDeclaration );
    Class literalType = t.getChild(0).getClass();
    if( expectedLiteralType!=literalType ) {
      constraintError( ConstrainerErrors.SwitchTypeMismatch, t );
    }
    AST block = t.getChild( 1 );
    block.accept( this );
    return null;
  }

  @Override
  public Object visitDefaultStatementTree(AST t) {
    System.out.println( "visitDefaultStatementTree" );
    AST block = t.getChild( 0 );
    block.accept( this );
    return null;
  }

  /**
   * Constrain the Assign tree:<br>
   * be sure the types of the right-hand-side expression and variable match; when
   * we constrain an expression we'll return a reference to the intrinsic type
   * tree describing the type of the expression
   */
  @Override
  public Object visitAssignTree(AST t) {
    AST idTree = t.getChild(0), idDecl = lookup(idTree), typeTree;
    decorate(idTree, idDecl);
    //System.out.println(String.format("AssignTree(%s): Decorating: %s with %s", t, idTree, idDecl));
    typeTree = decoration(idDecl.getChild(1));

    // now check that the types of the expr and id are the same
    // visit the expr tree and get back its type
    AST o = (AST)t.getChild(1).accept(this);
    if (!o.equals(typeTree)) {
      //System.out.println(String.format("AssignTree(%s): IdTree %s has TypeTree %s, Expected: %s", t, idTree, typeTree, o) );
      constraintError( ConstrainerErrors.BadAssignmentType, t );
    }
    return null;
  }

  @Override
  public Object visitIntTree(AST t) {
    decorate(t, intTree);
    return intTree;
  }

  @Override
  public Object visitIdentifierTree(AST t) {
    //System.out.println( String.format( "Constrainer: Visiting %s tree.", t.toString() ) );
    AST decl = lookup( t );
    decorate(t, decl);
    return decoration(decl.getChild(1));
  }

  @Override
  public Object visitRelationalOperationTree(AST t) {
    AST leftOp = t.getChild(0), rightOp = t.getChild(1);
    if ((AST) (leftOp.accept(this)) != (AST) (rightOp.accept(this))) {
      constraintError(ConstrainerErrors.TypeMismatchInExpr, t);
    }
    decorate(t, boolTree);
    return boolTree;
  }

  /**
   * Constrain the expression tree with an adding op at the root:<br>
   * e.g. t1 + t2<br>
   * check that the types of t1 and t2 match, if it's a plus tree then the types
   * must be a reference to the intTree
   *
   * @return the type of the tree
   */
  @Override
  public Object visitAdditionOperationTree(AST t) {
    AST leftOpType = (AST) (t.getChild(0).accept(this)), rightOpType = (AST) (t.getChild(1).accept(this));
    if (leftOpType != rightOpType) {
      constraintError(ConstrainerErrors.TypeMismatchInExpr, t);
    }
    decorate(t, leftOpType);
    return leftOpType;
  }

  @Override
  public Object visitMultiplicationOperationTree(AST t) {
    return visitAdditionOperationTree(t);
  }

  @Override
  public Object visitIntTypeTree(AST t) {
    //System.out.println("IntTypeTree");
    return t;
  }

  @Override
  public Object visitBoolTypeTree(AST t) {
    //System.out.println("BoolTypeTree");
    return null;
  }

  @Override
  public Object visitStringTypeTree(AST t) {
    //System.out.println("StringTypeTree");
    return t;
  }

  @Override
  public Object visitCharTypeTree(AST t) {
    //System.out.println("CharTypeTree");
    return t;
  }

  @Override
  public Object visitStringTree(AST t) {
    //System.out.println("StringTree");
    decorate(t, stringTree);
    return stringTree;
  }

  @Override
  public Object visitCharTree(AST t) {
    //System.out.println("CharTree");
    decorate(t, charTree);
    return charTree;
  }

  @Override
  public Object visitFormalsTree(AST t) {
    //System.out.println("Formals");
    return t;
  }

  @Override
  public Object visitActualArgumentsTree(AST t) {
    //System.out.println("Actuals");
    return t;
  }

  void constraintError( ConstrainerErrors err, AST tree ) {
    PrintVisitor v1 = new PrintVisitor();
    System.out.println( "****CONSTRAINER ERROR: " + err + "   ****" );
    tree.accept( v1 );
    System.exit(1);
    return;
  }

}