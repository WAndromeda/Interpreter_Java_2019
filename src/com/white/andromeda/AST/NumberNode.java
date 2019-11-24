package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Token;

public class NumberNode extends ExprNode{

    public final Token number;

    public NumberNode(Token number) {
        this.number = number;
    }

    @Override
    public Integer getValue(){
        return Integer.parseInt(number.text);
    }

    @Override
    public void setValue(final Integer value){
        throw new SemanticsException("Присвоение значения КОНСТАНТЕ невозможно", number);
    }


    @Override
    public String toString() {
        return number.text;
    }



}