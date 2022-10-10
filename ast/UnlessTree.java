package ast;

import visitor.ASTVisitor;

public class UnlessTree extends AST{

    public Object accept(ASTVisitor v) {
        return v.visitUnlessTree(this);
    }

    @Override
    public String toString(){
        return "unlessTree";
    }
}
