package constrain;

import lexer.*;
import parser.Parser;
import visitor.*;
import ast.*;
import java.util.*;

/**
 *  Constrainer object will visit the AST, gather/check variable
 *  type information and decorate uses of variables with their
 *  declarations; the decorations will be used by the code generator
 *  to provide access to the frame offset of the variable for generating
 *  load/store bytecodes; <br>
 *  Note that when constraining expression trees we return the type tree
 *  corresponding to the result type of the expression; e.g. 
 *  the result of constraining the tree for 1+2*3 will be the int type
 *  tree
*/
public class Constrainer extends ASTVisitor {

    private AST t;           // the AST to constrain
    private Table symtab = new Table();
    private Parser parser;   // parser used with this constrainer
    
/**
 *  The following comment refers to the functions stack
 *  declared below the comment.
 *  Whenever we start constraining a function declaration
 *  we push the function decl tree which indicates we're
 *  in a function (to ensure that we don't attempt to return
 *  from the main program - return's are only allowed from
 *  within functions); it also gives us access to the return
 *  type to ensure the type of the expr that is returned is
 *  the same as the type declared in the function header
*/
    private Stack<AST> functions = new Stack<AST>();

/**
 *  readTree, writeTree, intTree, boolTree,falseTree, trueTree
 *  are AST's that will be constructed (intrinsic trees) for
 *  every program. They are constructed in the same fashion as
 *  source program trees to ensure consisten processing of 
 *  functions, etc.
*/
    public static AST ReadTree, WriteTree, IntTree, BoolTree,
        FalseTree, TrueTree, ReadID, WriteID;
               
    public Constrainer(AST t, Parser parser) {
        this.t = t;
        this.parser = parser;
    }
    
    public void execute() {
        symtab.beginScope();
        t.accept(this);
    }

/**
 *  t is an IdTree; retrieve the pointer to its declaration
*/
    private AST lookup( AST t ) throws ConstraintException {
        return ( AST )( symtab.get( ( ( IdTree ) t ).getSymbol() ) );
    }

/**
 *  Decorate the IdTree with the given decoration - its decl tree
*/
    private void enter( AST t, AST decoration ) {
        symtab.put( ( ( IdTree ) t ).getSymbol(), decoration );
    }

/**
 *  get the type of the current type tree
 *  @param t is the type tree
 *  @return the intrinsic tree corresponding to the type of t
*/
    private AST getType( AST t ) {
        return ( t.getClass() == IntTypeTree.class ) ? IntTree : BoolTree;
    }

    public void decorate( AST t, AST decoration ) {
        t.setDecoration( decoration );
    }

/**
 *  @return the decoration of the tree
*/
    public AST decoration( AST t ) {
        return t.getDecoration();
    }
    
/**
 *  build the intrinsic trees; constrain them in the same fashion
 *  as any other AST
*/
    private void buildIntrinsicTrees() {
        Lexer lex = parser.getLexer();
        TrueTree = new IdTree( lex.newIdToken( "true",-1,-1, -1 ) );
        FalseTree = new IdTree(lex.newIdToken( "false",-1,-1, -1 ) );
        ReadID = new IdTree( lex.newIdToken( "read",-1,-1, -1 ) );
        WriteID = new IdTree( lex.newIdToken( "write",-1,-1, -1 ) );
        BoolTree = ( new DeclTree() ).addChild( new BoolTypeTree() ).
          addChild( new IdTree( lex.newIdToken( "<<bool>>",-1,-1, -1 ) ) );
        decorate( BoolTree.getChild( 2 ), BoolTree );
        IntTree = ( new DeclTree() ).addChild( new IntTypeTree() ).
          addChild( new IdTree( lex.newIdToken( "<<int>>",-1,-1, -1 ) ) );
        decorate( IntTree.getChild( 2 ), IntTree );
        // to facilitate type checking; this ensures int decls and id decls
        // have the same structure
        
        // read tree takes no parms and returns an int
        ReadTree = (new FunctionDeclTree()).addChild(new IntTypeTree()).
          addChild(ReadID).addChild(new FormalsTree()).
          addChild(new BlockTree());
        
        // write tree takes one int parm and returns that value
        WriteTree = (new FunctionDeclTree()).addChild(new IntTypeTree()).
          addChild(WriteID);
        AST decl = (new DeclTree()).addChild(new IntTypeTree()).
          addChild(new IdTree(lex.newIdToken("dummyFormal",-1,-1, -1)));
        AST formals = (new FormalsTree()).addChild(decl);
        WriteTree.addChild(formals).addChild(new BlockTree());
        WriteTree.accept(this);
        ReadTree.accept(this);
    }
    
/**
 *  Constrain the program tree - visit its kid
*/
    public Object visitProgramTree(AST t) {
        buildIntrinsicTrees();
        this.t = t;
        t.getChild(1).accept(this);
        return null;
    }

/**
 *  Constrain the Block tree:<br>
 *  <ol><li>open a new scope, <li>constrain the kids in this new scope, <li>close the
 *  scope removing any local declarations from this scope</ol>
*/
    public Object visitBlockTree(AST t) {
        symtab.beginScope();
        visitChildren(t);
        symtab.endScope();
        return null; }
        
/**
 *  Constrain the FunctionDeclTree:
 *  <ol><li>Enter the function name in the current scope, <li>enter the formals
 *  in the function scope and <li>constrain the body of the function</ol>
*/
    public Object visitFunctionDeclTree(AST t) {
        AST fname = t.getChild(2),
            returnType = t.getChild(1),
            formalsTree = t.getChild(3),
            bodyTree = t.getChild(4);
        functions.push(t);
        enter(fname,t);  // enter function name in CURRENT scope
        decorate(returnType,getType(returnType));
        symtab.beginScope();  // new scope for formals and body
        visitChildren(formalsTree); // all formal names go in new scope
        bodyTree.accept(this);
        symtab.endScope();
        functions.pop();
        return null;
    }
        
/**
 *  Constrain the Call tree:<br>
 *  check that the number and types of the actuals match the
 *  number and type of the formals
*/
    public Object visitCallTree(AST t) throws ConstraintException {
        AST fct,
            fname = t.getChild(1),
            fctType;
        visitChildren(t);
        fct = lookup(fname);
        if (fct.getClass() != FunctionDeclTree.class) {
            throw new ConstraintException( ConstraintExceptionType.CallingNonFunction );
        }
        fctType = decoration(fct.getChild(1));
        decorate(t,fctType);
        decorate(t.getChild(1),fct);
        // now check that the number/types of actuals match the
        // number/types of formals
        checkArgDecls(t,fct);
        return fctType;
    }
    
    private void checkArgDecls(AST caller, AST fct) throws ConstraintException {
        // check number and types of args/formals match
        AST formals = fct.getChild(3);
        Iterator<AST> actualChildren = caller.getChildren().iterator(),
                    formalChildren = formals.getChildren().iterator();
        actualChildren.next();  // skip past fct name
        for (; actualChildren.hasNext();) {
            try {
                AST actualDecl = decoration(actualChildren.next()),
                    formalDecl = formalChildren.next();
                if (decoration(actualDecl.getChild(2)) !=
                    decoration(formalDecl.getChild(2))) {
                    throw new ConstraintException( ConstraintExceptionType.ActualFormalTypeMismatch )
                }
            } catch (Exception e) {
                throw new ConstraintException( ConstraintExceptionType.NumberActualsFormalsDiffer );
            }
        }
        if (formalChildren.hasNext()) {
            throw new ConstraintException( ConstraintExceptionType.NumberActualsFormalsDiffer );
        }
        return;
    }
                
/**
 *  Constrain the Decl tree:<br>
 *  <ol><li>decorate to the corresponding intrinsic type tree, <li>enter the
 *  variable in the current scope so later variable references can
 *  retrieve the information in this tree</ol>
*/
    public Object visitDeclTree(AST t) {
        AST idTree = t.getChild(2);
        enter(idTree,t);
        AST typeTree = getType(t.getChild(1));
        decorate(idTree,typeTree);
        return null; }
        
/**
 *  Constrain the <i>If</i> tree:<br>
 *  check that the first kid is an expression that is a boolean type
*/
    public Object visitIfTree(AST t) throws ConstraintException {
        if ( t.getChild(1).accept(this) != BoolTree) {
            throw new ConstraintException( ConstraintExceptionType.BadConditional );
        }
        t.getChild(2).accept(this);
        t.getChild(3).accept(this);
        return null;
    }
        
    public Object visitWhileTree(AST t) throws ConstraintException {
        if ( t.getChild(1).accept(this) != BoolTree) {
            throw new ConstraintException( ConstraintExceptionType.BadConditional );
        }
        t.getChild(2).accept(this);
        return null;
    }
        
/**
 *  Constrain the Return tree:<br>
 *  Check that the returned expression type matches the type indicated
 *  in the function we're returning from
*/
    public Object visitReturnTree(AST t) {
        if (functions.empty()) {
            constraintError(ConstrainerErrors.ReturnNotInFunction);
        }
        AST currentFunction = (functions.peek());
        decorate(t,currentFunction);
        AST returnType = decoration(currentFunction.getChild(1));
        if ( (t.getChild(1).accept(this)) != returnType) {
            constraintError(ConstrainerErrors.BadReturnExpr);
        }
        return null;
    }
        
/**
 *  Constrain the Assign tree:<br>
 *  be sure the types of the right-hand-side expression and variable
 *  match; when we constrain an expression we'll return a reference
 *  to the intrinsic type tree describing the type of the expression
*/
    public Object visitAssignTree(AST t) {
        AST idTree = t.getChild(1),
            idDecl = lookup(idTree),
            typeTree;
        decorate(idTree,idDecl);
        typeTree = decoration(idDecl.getChild(2));
        
        // now check that the types of the expr and id are the same
        // visit the expr tree and get back its type
        if ( (t.getChild(2).accept(this)) != typeTree) {
            constraintError(ConstrainerErrors.BadAssignmentType);
        }
        return null;
    }
        
    public Object visitIntTree(AST t) {
        decorate(t, IntTree);
        return IntTree;
    }
        
    public Object visitIdTree(AST t) {
        AST decl = lookup(t);
        decorate(t,decl);
        return decoration(decl.getChild(2));
    }
        
    public Object visitRelOpTree(AST t) {
        AST leftOp = t.getChild(1),
            rightOp = t.getChild(2);
        if ( (AST)(leftOp.accept(this)) != (AST)(rightOp.accept(this)) ) {
            constraintError(ConstrainerErrors.TypeMismatchInExpr);
        }
        decorate(t, BoolTree);
        return BoolTree;
    }
 
/**
 *  Constrain the expression tree with an adding op at the root:<br>
 *  e.g. t1 + t2<br>
 *  check that the types of t1 and t2 match, if it's a plus tree
 *  then the types must be a reference to the intTree
 *  @return the type of the tree
*/
    public Object visitAddOpTree(AST t) {
        AST leftOpType = (AST)(t.getChild(1).accept(this)),
            rightOpType = (AST)(t.getChild(2).accept(this));
        if (leftOpType != rightOpType) {
            constraintError(ConstrainerErrors.TypeMismatchInExpr);
        }
        decorate(t,leftOpType);
        return leftOpType;
    }
        
    public Object visitMultOpTree(AST t) {
        return visitAddOpTree(t);
    }

    public Object visitIntTypeTree(AST t) {return null;}
    public Object visitBoolTypeTree(AST t) {return null;}
    public Object visitFormalsTree(AST t) {return null;}
    public Object visitActualArgsTree(AST t) {return null;}
    
    void constraintError(ConstrainerErrors err) {
        PrintVisitor v1 = new PrintVisitor();
        v1.visitProgramTree(t);
        System.out.println("****CONSTRAINER ERROR: " + err + "   ****");
        System.exit(1);
        return;
    }
    
}