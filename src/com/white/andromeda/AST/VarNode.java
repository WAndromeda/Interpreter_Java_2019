package com.white.andromeda.AST;

import com.white.andromeda.Parser;
import com.white.andromeda.Token;

public class VarNode extends ExprNode{

    public final Token id;

    public VarNode(Token id) {
        this.id = id;
    }

    @Override
    public Integer getValue(){
        return Parser.intVariables.get(id.text);
    }

    @Override
    public void setValue(final Integer value){
        Parser.intVariables.put(id.text, value);
    }

    @Override
    public String toString() {
        return id.text;
    }
}