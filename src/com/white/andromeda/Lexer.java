package com.white.andromeda;

import com.white.andromeda.Exception.LexException;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private String src;                 // Весь исходной код программы
    private int pos;                    // Позиция в строке (линии), на котороый находится программа на этапе анализа
    private int line;                   // Строка (линия), на которой находился анализатор
    private LinkedList<Token> tokens;   // Список токенов исходного текста @src

    public Lexer() {
        src = "";
        pos = 0;
        line = 1;
        tokens = new LinkedList<>();
    }

    public Lexer(String src) {
        this.src = src;
        pos = 0;
        line = 1;
        tokens = new LinkedList<>();
    }

    private int sumPreviousPos(){
        int sum = 0;
        for (Token token : tokens){
            if (token.type == TokenType.LINE){
                sum += token.pos + 2;
            }
        }
        return sum;
    }

    private boolean nextToken(){
        if (pos >= src.length())
            return false;
        else{
            for (TokenType tt : TokenType.values()){
                Matcher m = tt.pattern.matcher(src);
                m.region(pos, src.length());
                if (m.lookingAt()){
                    tokens.add(new Token(tt, m.group(), pos - sumPreviousPos(), line));
                    if (tt == TokenType.LINE) {
                        line++;
                    }
                    pos = m.end();
                    return true;
                }
            }
            throw new LexException("Неизвестный символ\nСтрока: " + line +"\nПозиция: " + pos + "\n");
        }
    }

    public List<Token> lex(){
        while(nextToken()){}
        if (!tokens.isEmpty())
            tokens.add(new Token(TokenType.LINE, "\n", 0, tokens.getLast().line));
        return tokens;
    }

    public void setSrc(String src){
        this.src = src.toUpperCase();
    }

    public void clear(){
        src = "";
        tokens.clear();
        pos = 0;
    }

}