package visitor;

import ast.*;

/**
 *  ASTVisitor class is the root of the Visitor hierarchy for visiting
 *  various AST's; each visitor asks each node in the AST it is given
 *  to <i>accept</i> its visit; <br>
 *  each subclass <b>must</b> provide all of the visitors mentioned
 *  in this class; <br>
 *  after visiting a tree the visitor can return any Object of interest<br>
 *  e.g. when the constrainer visits an expression tree it will return
 *  a reference to the type tree representing the type of the expression
 */
public abstract class ASTVisitor {

  public void visitChildren( AST t ) {
    for ( AST child : t.getChildren() ) {
      child.accept( this );
    }
    return;
  }

  public abstract Object visitProgramTree(AST t);
  public abstract Object visitBlockTree(AST t);
  public abstract Object visitFunctionDeclarationTree(AST t);
  public abstract Object visitCallTree(AST t);
  public abstract Object visitDeclarationTree(AST t);

  public abstract Object visitIntTypeTree(AST t);
  public abstract Object visitBoolTypeTree(AST t);
  public abstract Object visitStringTypeTree(AST t);
  public abstract Object visitCharTypeTree(AST t);

  public abstract Object visitStringTree(AST t);
  public abstract Object visitCharTree(AST t);

  public abstract Object visitFormalsTree(AST t);
  public abstract Object visitActualArgumentsTree(AST t);
  public abstract Object visitIfTree(AST t);
  public abstract Object visitUnlessTree(AST t);
  public abstract Object visitWhileTree(AST t);
  public abstract Object visitReturnTree(AST t);

  public abstract Object visitSwitchBlockTree(AST t);
  public abstract Object visitSwitchStatementTree(AST t);
  public abstract Object visitCaseStatementTree(AST t);
  public abstract Object visitDefaultStatementTree(AST t);

  public abstract Object visitAssignTree(AST t);
  public abstract Object visitIntTree(AST t);
  public abstract Object visitIdentifierTree(AST t);
  public abstract Object visitRelationalOperationTree(AST t);
  public abstract Object visitAdditionOperationTree(AST t);
  public abstract Object visitMultiplicationOperationTree(AST t);
}