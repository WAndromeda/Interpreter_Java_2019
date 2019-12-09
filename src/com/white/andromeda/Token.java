package com.white.andromeda;

public class Token {

    public final TokenType type; //Тип токена
    public final String text; // Текст токена
    public final int colon; //Индекс первого символа лексемы в исходном тексте
    public final int line; //Индекс первого символа лексемы в исходном тексте

    public Token(TokenType type, String text, int colon, int line) {
        this.type = type;
        this.text = text;
        this.colon = colon;
        this.line = line;
    }
    @Override
    public String toString() {
        return "{\ntokenType: " + type + ",\nline: "+ line +",\ncolon: " + colon + ",\ntext: " + text + "\n}";
    }
}