package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.ASTVisitor;

public class CharLitTree extends AST {
    private Symbol symbol;

    public CharLitTree(Token token){
        this.symbol = token.getSymbol();
    }

    @Override
    public Object accept(ASTVisitor v) {
       return v.visitCharLitTree(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString(){
        return "CharLitTree: "+ getSymbol().toString();
    }
}
