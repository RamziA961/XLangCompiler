package parser;

import java.util.*;
import lexer.*;
import ast.*;

/**
 * The Parser class performs recursive-descent parsing; as a by-product it will
 * build the <b>Abstract Syntax Tree</b> representation for the source
 * program<br>
 * Following is the Grammar we are using:<br>
 * <pre>
 *  PROGRAM -> �program� BLOCK ==> program
 *
 *  BLOCK -> �{� D* S* �}�  ==> block
 *
 *  D -> TYPE NAME                    ==> decl
 *    -> TYPE NAME FUNHEAD BLOCK      ==> functionDecl
 *
 *  TYPE  ->  �int�
 *        ->  �boolean�
 *
 *  FUNHEAD  -> '(' (D list ',')? ')'  ==> formals<br>
 *
 *  S -> �if� E �then� BLOCK �else� BLOCK  ==> if
 *    -> �while� E BLOCK               ==> while
 *    -> �return� E                    ==> return
 *    -> BLOCK
 *    -> NAME �=� E                    ==> assign<br>
 *
 *  E -> SE
 *    -> SE �==� SE   ==> =
 *    -> SE �!=� SE   ==> !=
 *    -> SE �<�  SE   ==> <
 *    -> SE �<=� SE   ==> <=
 *
 *  SE  ->  T
 *      ->  SE �+� T  ==> +
 *      ->  SE �-� T  ==> -
 *      ->  SE �|� T  ==> or
 *
 *  T  -> F
 *     -> T �*� F  ==> *
 *     -> T �/� F  ==> /
 *     -> T �&� F  ==> and
 *
 *  F  -> �(� E �)�
 *     -> NAME
 *     -> <int>
 *     -> NAME '(' (E list ',')? ')' ==> call<br>
 *
 *  NAME  -> <id>
 * </pre>
 */
public class Parser {

    private Token currentToken;
    private Lexer lex;
    private EnumSet<Tokens> relationalOps
            = EnumSet.of(Tokens.Equal, Tokens.NotEqual, Tokens.Less, Tokens.LessEqual, Tokens.Greater, Tokens.GreaterEqual);
    private EnumSet<Tokens> addingOps
            = EnumSet.of(Tokens.Plus, Tokens.Minus, Tokens.Or);
    private EnumSet<Tokens> multiplyingOps
            = EnumSet.of(Tokens.Multiply, Tokens.Divide, Tokens.And);

    /**
     * Construct a new Parser;
     *
     * @param sourceProgram - source file name
     * @exception Exception - thrown for any problems at startup (e.g. I/O)
     */
    public Parser(String sourceProgram) throws Exception {
        try {
            lex = new Lexer(sourceProgram);
            scan();
        } catch (Exception e) {
            System.out.println("********exception*******" + e.toString());
            throw e;
        };
    }

    public Lexer getLex() {
        return lex;
    }

    /**
     * Execute the parse command
     *
     * @return the AST for the source program
     * @exception Exception - pass on any type of exception raised
     */
    public AST execute() throws Exception {
        try {
            return rProgram();
        } catch (SyntaxError e) {
            e.print();
            throw e;
        }
    }

    /**
     * <
     * pre>
     * Program -> 'program' block ==> program
     * </pre>
     *
     * @return the program tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rProgram() throws SyntaxError {
        // note that rProgram actually returns a ProgramTree; we use the 
        // principle of substitutability to indicate it returns an AST
        AST t = new ProgramTree();
        expect(Tokens.Program);
        t.addKid(rBlock());
        return t;
    }

    /**
     * <
     * pre>
     * block -> '{' d* s* '}' ==> block
     * </pre>
     *
     * @return block tree
     * @exception SyntaxError - thrown for any syntax error e.g. an expected
     * left brace isn't found
     */
    public AST rBlock() throws SyntaxError {
        expect(Tokens.LeftBrace);
        AST t = new BlockTree();
        while (startingDecl()) {  // get decls
                t.addKid(rDecl());
        }
        while (startingStatement()) {  // get statements
                t.addKid(rStatement());
        }
        expect(Tokens.RightBrace);
        return t;
    }

    public AST rSwitchBlock(Token switchArgumentSymbol) throws SyntaxError{
        expect(Tokens.LeftBrace);
        AST switchBlockTree = new SwitchBlockTree();

        while(!isThisTok(Tokens.RightBrace)) { //was Tokens.case
            expect(Tokens.Case); //checks for case token and advances
            AST caseTree = new CaseTree();

            if (isThisDataType()) {
                caseTree.addKid(createDataTree()); //creates appropriate tree based on token's type
                scan();
            } else {
                throw new SyntaxError(currentToken, switchArgumentSymbol.getKind());
            }
            expect(Tokens.Colon);

            AST caseChild = (new AssignTree().addKid(rName())); //rName scans, new assign tree with identifier child
            expect(Tokens.Assign);
            caseTree.addKid(caseChild.addKid(rExpr()));  //rExpr scans, makes expression child of assign tree, make caseTree parent of assign tree
            switchBlockTree.addKid(caseTree); //make case tree child of switchBlockTree

            if (isThisTok(Tokens.Default)) {    //is there a default statement?
                AST defaultTree = new DefaultTree(); //create tree
                scan();
                expect(Tokens.Colon);
                defaultTree.addKid(new AssignTree().addKid(rName())); //add id to new assign tree
                expect(Tokens.Assign);
                defaultTree.getKid(1).addKid(rExpr());
                switchBlockTree.addKid(defaultTree); //add default to switchBlockTree
            }
        }
        expect(Tokens.RightBrace);
        return switchBlockTree;
    }

    //added
    //produces IntTree, CharLitTree, StringLitTree
    private AST createDataTree() {
        AST tree;
        if(isThisTok(Tokens.StringLit)){
            tree = new StringLitTree(currentToken);
        }else if(isThisTok(Tokens.CharLit)){
            tree = new CharLitTree(currentToken);
        }else if(isThisTok(Tokens.INTeger)){
            tree = new IntTree(currentToken);
        }else{
            tree = new BoolTypeTree();
        }
        return tree;
    }

    boolean startingDecl() {
        if (isThisTok(Tokens.Int) || isThisTok(Tokens.BOOLean) || isThisTok(Tokens.StringType) || isThisTok(Tokens.Char)) {
            return true;
        }
        return false;
    }

    boolean startingStatement() {
        if (isThisTok(Tokens.If) || isThisTok(Tokens.While) || isThisTok(Tokens.Return)
                || isThisTok(Tokens.LeftBrace) || isThisTok(Tokens.Identifier) || isThisTok(Tokens.Switch) || isThisTok(Tokens.Unless)) {
            return true;
        }
        return false;
    }

    //added
    boolean isThisDataType(){
        return isThisTok(Tokens.StringLit) || isThisTok(Tokens.CharLit) || isThisTok(Tokens.INTeger);
    }

    /**
     * <
     * pre>
     * d -> type name ==> decl -> type name funcHead block ==> functionDecl
     * </pre>
     *
     * @return either the decl tree or the functionDecl tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rDecl() throws SyntaxError {
        AST t, t1;
        t = rType();
        t1 = rName();
        if (isThisTok(Tokens.LeftParen)) { // function
            t = (new FunctionDeclTree()).addKid(t).addKid(t1);
            t.addKid(rFunHead());
            t.addKid(rBlock());
            return t;
        }
        t = (new DeclTree()).addKid(t).addKid(t1);
        return t;
    }

    /**
     * <
     * pre>
     * type -> 'int' type -> 'bool'
     * </pre>
     *
     * @return the dataType tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rType() throws SyntaxError {
        AST t;
        if (isThisTok(Tokens.Int)) {
            t = new IntTypeTree();
            scan();
        }else if (isThisTok(Tokens.StringType) ){
            t = new StringTypeTree();
            scan();
        }else if(isThisTok(Tokens.Char)){
            t = new CharTree();
            scan();
        }else{
            expect(Tokens.BOOLean);
            t = new BoolTypeTree();
        }
        return t;
    }

    /**
     * <
     * pre>
     * funHead -> '(' (decl list ',')? ')' ==> formals note a funhead is a list
     * of zero or more decl's separated by commas, all in parens
     * </pre>
     *
     * @return the formals tree describing this list of formals
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rFunHead() throws SyntaxError {
        AST t = new FormalsTree();
        expect(Tokens.LeftParen);
        if (!isThisTok(Tokens.RightParen)) {
            do {
                t.addKid(rDecl());
                if (isThisTok(Tokens.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(Tokens.RightParen);
        return t;
    }

    /**
     * <
     * pre>
     * S -> 'if' e 'then' block 'else' block ==> if -> 'while' e block ==> while
     * -> 'return' e ==> return -> block -> name '=' e ==> assign
     * </pre>
     *
     * @return the tree corresponding to the statement found
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rStatement() throws SyntaxError {
        AST tree;
        if (isThisTok(Tokens.If)) {
            scan();
            tree = new IfTree();
            tree.addKid(rExpr());
            expect(Tokens.Then);
            tree.addKid(rBlock());
            if (isThisTok(Tokens.Else)) { //optional else
                scan();
                tree.addKid(rBlock());
            }
            return tree;
        } else if (isThisTok(Tokens.While)) {
            scan();
            tree = new WhileTree();
            tree.addKid(rExpr());
            tree.addKid(rBlock());
            return tree;

        } else if (isThisTok(Tokens.Return)) {
            scan();
            tree = new ReturnTree();
            tree.addKid(rExpr());
            return tree;

        } else if (isThisTok(Tokens.LeftBrace)) {
            return rBlock();

        } else if (isThisTok(Tokens.Unless)) {
            scan(); //advance to nextToken
            tree = new UnlessTree();
            tree.addKid(rExpr());
            expect(Tokens.Then); //check for then; advance
            tree.addKid(rBlock());
            if (isThisTok(Tokens.Else)) { //optional else
                scan();
                tree.addKid(rBlock());
            }

        } else if (isThisTok(Tokens.Switch)) {
            tree = new SwitchTree();
            scan(); //advance to token following switch
            expect(Tokens.LeftParen); //if true advance

            Token switchArgToken = currentToken;
            tree.addKid(rName());  //current token should be identifier

            expect(Tokens.RightParen); //if true advance
            tree.addKid(rSwitchBlock(switchArgToken)); //switch block handles braces and case statements
        } else {
            tree = rName();
            tree = (new AssignTree()).addKid(tree);
            expect(Tokens.Assign);
            tree.addKid(rExpr());
        }
        return tree;
    }

    /**
     * <
     * pre>
     * e -> se -> se '==' se ==> = -> se '!=' se ==> != -> se '<' se ==> < -> se
     * '<=' se ==> <= </pre> @return the tree corresponding to the expression
     *
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rExpr() throws SyntaxError {
        AST t, kid = rSimpleExpr();
        t = getRelationTree();
        if (t == null) {
            return kid;
        }
        t.addKid(kid);
        t.addKid(rSimpleExpr());
        return t;
    }

    /**
     * <
     * pre>
     * se -> t -> se '+' t ==> + -> se '-' t ==> - -> se '|' t ==> or This rule
     * indicates we should pick up as many <i>t</i>'s as possible; the
     * <i>t</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the adding expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rSimpleExpr() throws SyntaxError {
        AST t, kid = rTerm();
        while ((t = getAddOperTree()) != null) {
            t.addKid(kid);
            t.addKid(rTerm());
            kid = t;
        }
        return kid;
    }

    /**
     * <
     * pre>
     * t -> f -> t '*' f ==> * -> t '/' f ==> / -> t '&' f ==> and This rule
     * indicates we should pick up as many <i>f</i>'s as possible; the
     * <i>f</i>'s will be left associative
     * </pre>
     *
     * @return the tree corresponding to the multiplying expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rTerm() throws SyntaxError {
        AST t, kid = rFactor();
        while ((t = getMultOperTree()) != null) {
            t.addKid(kid);
            t.addKid(rFactor());
            kid = t;
        }
        return kid;
    }

    /**
     * <
     * pre>
     * f -> '(' e ')' -> name -> <int>
     * -> name '(' (e list ',')? ')' ==> call
     * </pre>
     *
     * @return the tree corresponding to the factor expression
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rFactor() throws SyntaxError {
        AST t;
        if (isThisTok(Tokens.LeftParen)) { // -> (e)
            scan();
            t = rExpr();
            expect(Tokens.RightParen);
            return t;
        }
        if (isThisTok(Tokens.INTeger)) {  //  -> <int>
            t = new IntTree(currentToken);
            scan();
            return t;
        }
        t = rName();
        if (!isThisTok(Tokens.LeftParen)) {  //  -> name
            return t;
        }
        scan();     // -> name '(' (e list ',')? ) ==> call
        t = (new CallTree()).addKid(t);
        if (!isThisTok(Tokens.RightParen)) {
            do {
                t.addKid(rExpr());
                if (isThisTok(Tokens.Comma)) {
                    scan();
                } else {
                    break;
                }
            } while (true);
        }
        expect(Tokens.RightParen);
        return t;
    }

    /**
     * <
     * pre>
     * name -> <id>
     * </pre>
     *
     * @return the id tree
     * @exception SyntaxError - thrown for any syntax error
     */
    public AST rName() throws SyntaxError {
        AST t;
        if (isThisTok(Tokens.Identifier)) {
            t = new IdTree(currentToken);
            scan();
            return t;
        }
        throw new SyntaxError(currentToken, Tokens.Identifier);
    }

    AST getRelationTree() {  // build tree with current token's relation
        Tokens kind = currentToken.getKind();
        if (relationalOps.contains(kind)) {
            AST t = new RelOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getAddOperTree() {
        Tokens kind = currentToken.getKind();
        if (addingOps.contains(kind)) {
            AST t = new AddOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private AST getMultOperTree() {
        Tokens kind = currentToken.getKind();
        if (multiplyingOps.contains(kind)) {
            AST t = new MultOpTree(currentToken);
            scan();
            return t;
        } else {
            return null;
        }
    }

    private boolean isThisTok(Tokens kind) {
        if ((currentToken == null) || (currentToken.getKind() != kind)) {
            return false;
        }
        return true;
    }

    private void expect(Tokens kind) throws SyntaxError {
        if (isThisTok(kind)) {
            scan();
            return;
        }
        throw new SyntaxError(currentToken, kind);
    }

    private void scan() {
        currentToken = lex.nextToken();
//        if (currentToken != null) {
//            System.out.println(currentToken.toString());   // debug printout
//        }
        return;
    }
}

class SyntaxError extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private Token tokenFound;
    private Tokens kindExpected;

    /**
     * record the syntax error just encountered
     *
     * @param tokenFound is the token just found by the parser
     * @param kindExpected is the token we expected to find based on the current
     * context
     */
    public SyntaxError(Token tokenFound, Tokens kindExpected) {
        this.tokenFound = tokenFound;
        this.kindExpected = kindExpected;
    }

    void print() {
        System.out.println("Expected: "
                + kindExpected);
        return;
    }
}
