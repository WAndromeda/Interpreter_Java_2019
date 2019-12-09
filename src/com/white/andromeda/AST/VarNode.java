package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Token;

import java.util.List;
import java.util.Map;

public class VarNode extends ExprNode{

    public final Token id;

    public VarNode(Token id) {
        this.id = id;
    }

    @Override
    public int getValue(List<Map<String, Integer>> variablesMapList){
        for (int i = variablesMapList.size()-1; i >= 0; i--)
            if (variablesMapList.get(i).get(id.text) != null)
                return variablesMapList.get(i).get(id.text);
        throw new SemanticsException("Переменная " + id.text + " не инициализирована", id);

    }

    public void setValue(final Integer value, List<Map<String, Integer>> variablesMapList){
        for (int i = variablesMapList.size()-1; i >= 0; i--)
            if (variablesMapList.get(i).get(id.text) != null) {
                variablesMapList.get(i).put(id.text, value);
                return;
            }
        variablesMapList.get(variablesMapList.size()-1).put(id.text, value);
    }

    @Override
    public String toString() {
        return id.text;
    }
}