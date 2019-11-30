package com.white.andromeda.AST;

import java.util.List;

public class WhileNode extends StmtNode {

    public final List<StmtNode> statements;
    public final ExprNode condition;

    public WhileNode(List<StmtNode> statements, ExprNode condition) {
        this.statements = statements;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "while ( " + condition.toString() +  " )\n" + statements.toString();
    }
}
