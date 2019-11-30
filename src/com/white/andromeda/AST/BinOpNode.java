package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Parser;
import com.white.andromeda.Token;

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
    public Integer getValue(){
        return Parser.evalExpr(this);
    }

    @Override
    public void setValue(Integer value){
        throw new SemanticsException("Присвоить значение ОПЕРАЦИИ невозможно", op);
    }

    @Override
    public String toString() {
        return left.toString() + " " + op.text + " " + right.toString();
    }
}