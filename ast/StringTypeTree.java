package ast;

import visitor.ASTVisitor;

public class StringTypeTree extends AST {

    public Object accept(ASTVisitor v) {
        return v.visitStringTypeTree(this);
    }
    @Override
    public String toString(){
        return "StringTypeTree";
    }
}
