package com.white.andromeda.Exception;

import com.white.andromeda.Token;

public class LexException  extends  RuntimeException{
    public LexException(String message){
        super(message);
    }

    public LexException(String message, Token token){
        super(message + "\nВ строке: " + token.line + "\nВ позиции: " + token.pos);
    }
}
