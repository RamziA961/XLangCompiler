package graphics;

import ast.AST;

public class ASTData {
    private int offset, depth;
    private AST tree;

    public ASTData(AST tree){
        this.tree = tree;
        offset = 0;
        depth = 0;
    }

    public ASTData(AST tree, int depth, int offset){
        this.tree = tree;
        this.offset = offset;
        this.depth = depth;
    }

    public void setOffset(int offset){
        this.offset = offset;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public AST getTree(){
        return this.tree;
    }


    public int getOffset(){
        return offset;
    }

    public int getDepth(){
        return depth;
    }

}
