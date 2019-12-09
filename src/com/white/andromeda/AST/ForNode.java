package com.white.andromeda.AST;

public class ForNode extends StmtNode {

    public final StmtNode statement;
    public final StmtNode stmtNodeLeft;
    public final ExprNode conditionMiddle;
    public final ExprStmtNode operationRight;

    public ForNode(StmtNode statement, StmtNode stmtNodeLeft, ExprNode conditionMiddle, ExprStmtNode operationRight) {
        this.statement = statement;
        this.stmtNodeLeft = stmtNodeLeft;
        this.conditionMiddle = conditionMiddle;
        this.operationRight = operationRight;
    }


    @Override
    public String toString() {
        return "for ( "+ stmtNodeLeft +";" + conditionMiddle.toString() +  "; "+ operationRight.toString() +" )\n" + statement.toString();
    }
}
