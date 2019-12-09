package com.white.andromeda.AST;

import com.white.andromeda.Executor;
import com.white.andromeda.Token;

import java.util.List;
import java.util.Map;

public class UnaryOpNode extends ExprNode {

    public final Token operation;
    public final ExprNode operand;

    public UnaryOpNode(Token operation, ExprNode operand) {
        this.operation = operation;
        this.operand = operand;
    }

    @Override
    public int getValue(List<Map<String, Integer>> variables){
        return Executor.evalExpr(this, variables);
    }

    @Override
    public String toString() {
        return operation.text + " " + (operand == null ? "null" : operand.toString());
    }
}
