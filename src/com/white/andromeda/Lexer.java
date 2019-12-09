package com.white.andromeda;

import com.white.andromeda.Exception.LexerException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    private String src;                 // Весь исходной код программы
    private int pos;                    // Конечная позиция разбора на n-ой итерации
    private int colon;                  // Колонка, которая увеличивается на на размер лексемы, в конце строки сбраывается на 1
    private int line;                   // Строка (линия), на которой находился анализатор
    private ArrayList<Token> tokens;    // Список токенов исходного текста @src

      public Lexer(String src) {
        this.src = src;
        pos = 0;
        colon = 1;
        line = 1;
        tokens = new ArrayList<>();
    }

    private boolean nextToken(){
        if (pos >= src.length())
            return false;
        else{
            for (TokenType tt : TokenType.values()){
                Matcher m = tt.pattern.matcher(src);
                m.region(pos, src.length());
                if (m.lookingAt()){
                    String tokenText = m.group();
                    tokens.add(new Token(tt, tokenText, colon, line));
                    if (tt == TokenType.LINE) {
                        line++;
                        colon = 1;
                    }else  colon += tokenText.length();
                    pos = m.end();
                    return true;
                }
            }
            throw new LexerException("Неизвестный символ\nСтрока: " + line +"\nПозиция: " + pos + "\n");
        }
    }

    public List<Token> lex(){
        while(nextToken());
        return tokens;
    }

}