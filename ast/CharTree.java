package ast;

import visitor.ASTVisitor;

public class CharTree extends AST {

    public Object accept(ASTVisitor v) {
        return v.visitCharTree(this);
    }

    @Override
    public String toString(){
        return "CharTree";
    }
}
