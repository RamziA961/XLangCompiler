package ast;

import visitor.ASTVisitor;

public class SwitchBlockTree extends AST {

    public Object accept(ASTVisitor v) {
        return v.visitSwitchBlockTree(this);
    }

    @Override
    public String toString(){
        return "switchBlockTree";
    }
}
