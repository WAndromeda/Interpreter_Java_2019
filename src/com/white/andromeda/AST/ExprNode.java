package com.white.andromeda.AST;

public abstract class ExprNode extends Node{

    public Integer getValue(){
        throw new RuntimeException("Получение значения у абстрактного узла невозможно");
    }
    public void setValue(Integer value){throw new RuntimeException("Установкая значения у абстрактного узла невозможно");}

}
