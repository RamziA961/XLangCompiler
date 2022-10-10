package ast;

import visitor.*;

public class BlockTree extends AST {

    public BlockTree() {
    }

    public Object accept(ASTVisitor v) {
        return v.visitBlockTree(this);
    }

    @Override
    public String toString(){
        return "BlockTree";
    }

}

