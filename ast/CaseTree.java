package ast;

import visitor.ASTVisitor;

public class CaseTree extends AST {

    public Object accept(ASTVisitor v) {
        return v.visitCaseTree(this);
    }

    @Override
    public String toString(){
        return "caseTree";
    }
}
