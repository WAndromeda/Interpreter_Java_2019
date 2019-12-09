package com.white.andromeda.AST;

import java.util.List;

public class ExprStmtNode extends StmtNode {

    public final List<ExprNode> expr;

    public ExprStmtNode(List<ExprNode> expr) {
        this.expr = expr;
    }

    @Override
    public String toString(){
        return  expr.toString() + ";";
    }
}
