package com.white.andromeda.AST;

public class PrintNode extends StmtNode {

    public final VarNode var;

    public PrintNode(VarNode var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "print " + var.toString();
    }
}
