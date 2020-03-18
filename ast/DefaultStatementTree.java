package ast;

import visitor.ASTVisitor;

public class DefaultStatementTree extends AST {

  @Override
  public Object accept(ASTVisitor v) {
    return v.visitDefaultStatementTree( this );
  }

}
