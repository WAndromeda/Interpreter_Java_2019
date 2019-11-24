package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Parser;
import com.white.andromeda.Token;

public class UnaryOpNode extends ExprNode {

    public final Token op;
    public final ExprNode operand;

    public UnaryOpNode(Token op, ExprNode operand) {
        this.op = op;
        this.operand = operand;
    }

    @Override
    public Integer getValue(){
        return Parser.eval(this);
    }

    @Override
    public void setValue(Integer value){
        throw new SemanticsException("Присвоить значение ОПЕРАЦИИ невозможно", op);
    }

    @Override
    public String toString() {
        return op.text + " " + (operand == null ? "null" : operand.toString());
    }
}
