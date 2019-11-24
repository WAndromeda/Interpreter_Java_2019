package com.white.andromeda.Exception;

import com.white.andromeda.Token;

public class ParseException extends RuntimeException {
    public ParseException(String message){
        super(message);
    }

    public ParseException(String message, Token token){
        super(message + "\nВ строке: " + token.line + "\nВ позиции: " + token.pos);
    }
}
