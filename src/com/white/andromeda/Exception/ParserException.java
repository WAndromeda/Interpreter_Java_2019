package com.white.andromeda.Exception;

import com.white.andromeda.Token;

public class ParserException extends RuntimeException {
    public ParserException(String message){
        super(message);
    }

    public ParserException(String message, Token token){
        super(message + "\nВ строке: " + token.line + "\nВ позиции: " + token.pos);
    }
}
