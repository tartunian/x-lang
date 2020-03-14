package ast;

import visitor.*;

public class SwitchStatementTree extends AST {

  public SwitchStatementTree() {
  }

  @Override
  public Object accept( ASTVisitor visitor ) {
    return visitor.visitSwitchStatementTree( this );
  }

}