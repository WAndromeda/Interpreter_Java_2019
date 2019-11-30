package com.white.andromeda.AST;

import java.util.List;

public class IfNode extends StmtNode {

    public final List<StmtNode> ifStatements;
    public final ExprNode condition;

    public IfNode(ExprNode condition, List<StmtNode> ifStatements) {
        this.ifStatements = ifStatements;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "if ( " + condition.toString() +  " )\n" + ifStatements.toString();
    }

}
