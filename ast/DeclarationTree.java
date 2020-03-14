package ast;

import visitor.*;

public class DeclarationTree extends AST {

    public DeclarationTree() {
    }

    public Object accept(ASTVisitor v) {
        return v.visitDeclTree(this);
    }

}

