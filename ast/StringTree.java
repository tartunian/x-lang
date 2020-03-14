package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.ASTVisitor;

public class StringTree extends AST {

  private Symbol symbol;

  public StringTree( Token token ) {
    this.symbol = token.getSymbol();
  }

  public Object accept(ASTVisitor v) {
    return v.visitStringTree(this);
  }

  public Symbol getSymbol() {
    return symbol;
  }

}
