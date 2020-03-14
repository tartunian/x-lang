package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.*;

public class AdditionOperationTree extends AST {
    private Symbol symbol;

    public AdditionOperationTree(Token tok) {
        this.symbol = tok.getSymbol();
    }

    public Object accept(ASTVisitor v) {
        return v.visitAddOpTree(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

}
