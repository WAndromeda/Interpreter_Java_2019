package com.white.andromeda.AST;

import java.util.List;

public class IfElseNode extends IfNode {

    public final List<StmtNode> elseStatements;

    public IfElseNode(ExprNode condition, List<StmtNode> ifStatements , List<StmtNode> elseStatements) {
        super(condition, ifStatements);
        this.elseStatements = elseStatements;
    }

    @Override
    public String toString() {
        return "if ( " + condition.toString() +  " )\n" + ifStatements.toString() + "\nelse\n" + elseStatements.toString();
    }
}
