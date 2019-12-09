package com.white.andromeda.AST;

public class IfNode extends StmtNode {

    public final StmtNode ifStatement;
    public final ExprNode condition;

    public IfNode(ExprNode condition, StmtNode ifStatement) {
        this.ifStatement = ifStatement;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "if ( " + condition.toString() +  " )\n" + ifStatement.toString();
    }

}
