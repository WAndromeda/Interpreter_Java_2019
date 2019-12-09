package com.white.andromeda.Exception;

import com.white.andromeda.Token;

public class LexerException extends  RuntimeException{
    public LexerException(String message){
        super(message);
    }

    public LexerException(String message, Token token){
        super(message + "\nВ строке: " + token.line + "\nВ позиции: " + token.colon);
    }
}
