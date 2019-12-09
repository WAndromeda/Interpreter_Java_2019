package com.white.andromeda.AST;

public class WhileNode extends StmtNode {

    public final StmtNode statement;
    public final ExprNode condition;

    public WhileNode(StmtNode statement, ExprNode condition) {
        this.statement = statement;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "while ( " + condition.toString() +  " )\n" + statement.toString();
    }
}
