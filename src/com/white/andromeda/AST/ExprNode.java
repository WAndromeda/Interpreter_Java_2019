package com.white.andromeda.AST;

import java.util.ArrayDeque;
import java.util.Map;

public abstract class ExprNode {

    public abstract int getValue(ArrayDeque<Map<String, Integer>> variablesMap);

}
