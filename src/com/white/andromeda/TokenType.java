package com.white.andromeda;

import java.util.regex.Pattern;

public enum TokenType{
    MULTI_COMMENT   ("/\\*([^*]|(\\*+([^*/])))*\\*+/"), // Многострочный комментарий
    COMMENT         ("//[^\\r\\n]+"),       // Однострочный комментарий
    NUMBER          ("[-]?[\\d]+"),         // Число
    FOR             ("for"),                // for цикл
    IF              ("if"),                 // if условие
    ELSE            ("else"),               // else от if условия
    WHILE           ("while"),              // while цикл
    XOR             ("xor|XOR"),            // Исключающее ИЛИ
    NOT             ("not|NOT"),            // Логическое отрицание
    OR              ("or|OR"),              // Логическое ИЛИ
    AND             ("and|AND"),            // Логическое И
    PRINT           ("print|PRINT"),        // Вывод идентификатора
    SEMICOLON       (";"),                  // Точка с запятой
    ID              ("[a-zA-Z][\\w]*"),     // Индентификатор
    LAND            ("&&"),                 // Логическое И
    LOR             ("\\|\\|"),             // Логическое ИЛИ
    GREATER         (">"),                  // Сравнение БОЛЬШЕ. Левый больше правого?
    LESS            ("<"),                  // Сравнение МЕНЬШЕ. Левый меньше правого?
    GREATER_EQUAL   (">="),                 // Сравнение БОЛЬШЕ ИЛИ РАВНО. Левый больше или равен правому?
    LESS_EQUAL      ("<="),                 // Сравнение МЕНЬШЕ ИЛИ РАВНО. Левый меньше или равен правому?
    NEQUAL          ("!="),                 // НеРавенство сравниваемых элементов
    EQUAL           ("=="),                 // Равенство сравниваемых элементов
    ASSIGNMENT_ADD  ("\\+="),               // Присваивание с СЛОЖЕНИЕМ
    ASSIGNMENT_SUB  ("-="),                 // Присваивание с ВЫЧИТАНИЕМ
    ASSIGNMENT_MUL  ("\\*="),               // Присваивание с УМНОЖЕНИЕМ
    ASSIGNMENT_DIV  ("/="),                 // Присваивание с ДЕЛЕНИЕМ
    ASSIGNMENT_AND  ("&="),                 // Присваивание с ЛОГИЧЕСКИМ AND (И)
    ASSIGNMENT_XOR  ("\\^="),               // Присваивание с XOR (Исключающее ИЛИ)
    ASSIGNMENT_OR   ("\\|="),               // Присваивание с ЛОГИЧЕСКИМ OR (ИЛИ)
    ADD             ("\\+"),                // Сумма
    SUB             ("-"),                  // Вычитание
    MUL             ("\\*"),                // Умножение
    DIV             ("/"),                  // Деление
    LPAR            ("\\("),                // Левая скобка
    RPAR            ("\\)"),                // Правая скобка
    LBRACE          ("\\{"),                // Левая скобка
    RBRACE          ("\\}"),                // Правая скобка
    LINE            ("[\\r]*[\\n]"),        // Перенос строки (служит разделителем команд)
    SPACE           ("[ \\s\\t\\v]+"),         // Пробел
    ASSIGNMENT      ("=");                  // Присваивание

    Pattern pattern;

    TokenType(String regEx) {
        this.pattern = Pattern.compile(regEx);
    }
}
