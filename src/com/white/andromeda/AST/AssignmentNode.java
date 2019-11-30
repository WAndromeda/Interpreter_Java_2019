package com.white.andromeda.AST;

import com.white.andromeda.Token;

public class AssignmentNode extends StmtNode {

    public final VarNode id;
    public final Token assign;
    public final ExprNode expr;

    public AssignmentNode(VarNode id, Token assign, ExprNode expr) {
        this.id = id;
        this.assign = assign;
        this.expr = expr;
    }
}
