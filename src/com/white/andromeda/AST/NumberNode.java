package com.white.andromeda.AST;

import com.white.andromeda.Token;

import java.util.ArrayDeque;
import java.util.Map;

public class NumberNode extends ExprNode{

    public final Token number;

    public NumberNode(Token number) {
        this.number = number;
    }

    @Override
    public int getValue(ArrayDeque<Map<String, Integer>> variablesMap){
        return Integer.parseInt(number.text);
    }

    @Override
    public String toString() {
        return number.text;
    }



}