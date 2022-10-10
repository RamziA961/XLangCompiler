package ast;

import visitor.*;

public class IfTree extends AST {

    public IfTree() {
    }

    public Object accept(ASTVisitor v) {
        return v.visitIfTree(this);
    }

    @Override
    public String toString(){
        return "IfTree";
    }

}

