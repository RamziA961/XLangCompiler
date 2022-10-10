package ast;

import visitor.ASTVisitor;

public class SwitchTree extends AST{

    public Object accept(ASTVisitor v) {
        return v.visitSwitchTree(this);
    }

    @Override
    public String toString(){
        return "switch";
    }
}
