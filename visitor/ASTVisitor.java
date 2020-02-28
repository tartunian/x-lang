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

    public void visitChildren(AST t) {
        for (AST kid : t.getChildren()) {
            kid.accept(this);
        }
        return;
    }

    public abstract Object visitProgramTree(AST t) throws Exception;
    public abstract Object visitBlockTree(AST t) throws Exception;
    public abstract Object visitFunctionDeclTree(AST t) throws Exception;
    public abstract Object visitCallTree(AST t) throws Exception;
    public abstract Object visitDeclTree(AST t) throws Exception;
    public abstract Object visitIntTypeTree(AST t) throws Exception;
    public abstract Object visitBoolTypeTree(AST t) throws Exception;
    public abstract Object visitFormalsTree(AST t) throws Exception;
    public abstract Object visitActualArgsTree(AST t) throws Exception;
    public abstract Object visitIfTree(AST t) throws Exception;
    public abstract Object visitWhileTree(AST t) throws Exception;
    public abstract Object visitReturnTree(AST t) throws Exception;
    public abstract Object visitAssignTree(AST t) throws Exception;
    public abstract Object visitIntTree(AST t) throws Exception;
    public abstract Object visitIdTree(AST t) throws Exception;
    public abstract Object visitRelOpTree(AST t) throws Exception;
    public abstract Object visitAddOpTree(AST t) throws Exception;
    public abstract Object visitMultOpTree(AST t) throws Exception;
}