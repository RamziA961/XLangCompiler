package ast;

import lexer.Symbol;
import lexer.Token;
import visitor.ASTVisitor;

public class StringLitTree extends AST {
    private Symbol symbol;

    public StringLitTree(Token token){
        this.symbol = token.getSymbol();
    }
    public Object accept(ASTVisitor v) {
        return v.visitStringLitTree(this);
    }

    public Symbol getSymbol() {
        return symbol;
    }

    @Override
    public String toString(){
        return "StringLitTree"+ getSymbol().toString();
    }
}
