package com.white.andromeda.AST;

import com.white.andromeda.Exception.SemanticsException;
import com.white.andromeda.Token;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VarNode extends ExprNode{

    public final Token id;

    public VarNode(Token id) {
        this.id = id;
    }

    @Override
    public int getValue(ArrayDeque<Map<String, Integer>> variablesMapList){
        for (Iterator<Map<String, Integer>> it = variablesMapList.iterator(); it.hasNext();) {
            Map<String, Integer> level = it.next();
            if (level.get(id.text) != null)
                return level.get(id.text);
        }
        throw new SemanticsException("Переменная " + id.text + " не инициализирована", id);

    }

    public void setValue(final Integer value, ArrayDeque<Map<String, Integer>> variablesMapDeque){
        for (Iterator<Map<String, Integer>> it = variablesMapDeque.iterator(); it.hasNext();) {
            Map<String, Integer> level = it.next();
            if (level.get(id.text) != null) {
                level.put(id.text, value);
                return;
            }
        }
        variablesMapDeque.getLast().put(id.text, value);
    }

    @Override
    public String toString() {
        return id.text;
    }
}