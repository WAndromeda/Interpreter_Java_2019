package com.white.andromeda.Exception;

import com.white.andromeda.Token;

public class SemanticsException extends RuntimeException {
    public SemanticsException(String message){
        super(message);
    }

    public SemanticsException(String message, Token token){
        super(message + "\nВ строке: " + token.line + "\nВ позиции: " + token.pos);
    }
}
