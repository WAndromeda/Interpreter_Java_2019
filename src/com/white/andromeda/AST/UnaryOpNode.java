package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Parser;
import com.white.andromeda.Token;

public class UnaryOpNode extends ExprNode {

    public final Token operation;
    public final ExprNode operand;

    public UnaryOpNode(Token operation, ExprNode operand) {
        this.operation = operation;
        this.operand = operand;
    }

    @Override
    public Integer getValue(){
        return Parser.eval(this);
    }

    @Override
    public void setValue(Integer value){
        throw new SemanticsException("Присвоить значение ОПЕРАЦИИ невозможно", operation);
    }

    @Override
    public String toString() {
        return operation.text + " " + (operand == null ? "null" : operand.toString());
    }
}
