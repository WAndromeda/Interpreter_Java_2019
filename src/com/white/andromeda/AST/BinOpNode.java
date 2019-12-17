package com.white.andromeda.AST;

import com.white.andromeda.Executor;
import com.white.andromeda.Token;

import java.util.ArrayDeque;
import java.util.Map;

public class BinOpNode extends ExprNode {

    public final Token op;
    public final ExprNode left;
    public final ExprNode right;

    public BinOpNode(Token op, ExprNode left, ExprNode right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public int getValue(ArrayDeque<Map<String, Integer>> variables){
        return Executor.evalExpr(this, variables);
    }

    @Override
    public String toString() {
        return left.toString() + " " + op.text + " " + right.toString();
    }
}