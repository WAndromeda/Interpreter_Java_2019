package com.white.andromeda.AST;

import java.util.List;

public class ForNode extends StmtNode {

    public final List<StmtNode> statements;
    public final StmtNode stmtNodeLeft;
    public final ExprNode conditionMiddle;
    public final ExprNode operationRight;

    public ForNode(List<StmtNode> statements, StmtNode stmtNodeLeft, ExprNode conditionMiddle, ExprNode operationRight) {
        this.statements = statements;
        this.stmtNodeLeft = stmtNodeLeft;
        this.conditionMiddle = conditionMiddle;
        this.operationRight = operationRight;
    }


    @Override
    public String toString() {
        return "for ( "+ stmtNodeLeft +";" + conditionMiddle.toString() +  "; "+ operationRight.toString() +" )\n" + statements.toString();
    }
}
