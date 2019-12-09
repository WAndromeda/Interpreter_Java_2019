package com.white.andromeda.AST;

import java.util.List;
import java.util.Map;

public abstract class ExprNode {

    public abstract int getValue(List<Map<String, Integer>> variablesMap);

}
