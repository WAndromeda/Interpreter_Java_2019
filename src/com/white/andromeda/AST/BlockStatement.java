package com.white.andromeda.AST;

import java.util.List;

public class BlockStatement extends StmtNode {

    public final List<StmtNode> statements;

    public BlockStatement(List<StmtNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString(){
        return "{\n " + statements.toString() + " \n}";
    }
}
