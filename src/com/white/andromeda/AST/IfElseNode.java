package com.white.andromeda.AST;

public class IfElseNode extends IfNode {

    public final StmtNode elseStatement;

    public IfElseNode(ExprNode condition, StmtNode ifStatement, StmtNode elseStatement) {
        super(condition, ifStatement);
        this.elseStatement = elseStatement;
    }

    @Override
    public String toString() {
        return super.toString() + "\nelse\n" + elseStatement.toString();
    }
}
