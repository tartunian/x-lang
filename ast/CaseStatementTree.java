package ast;

import visitor.ASTVisitor;

public class CaseStatementTree extends AST {

  @Override
  public Object accept(ASTVisitor v) {
    return v.visitCaseStatementTree( this );
  }

}
