package ast;

import visitor.ASTVisitor;

public class DefaultTree extends AST {

    public Object accept(ASTVisitor v) {
        return v.visitDefaultTree(this);
    }

    @Override
    public String toString(){
        return "defaultTree";
    }
}
