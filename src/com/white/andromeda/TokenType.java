package com.white.andromeda;

import java.util.regex.Pattern;

public enum TokenType{
    COMMENT         ("//[^\\r\\n]+"),         // Однострочный комментарий
    NUMBER          ("[-]?[\\d]+"),         // Число
    IF              ("if"),                 // if условие
    WHILE           ("while"),              // while цикл
    XOR             ("[xX][oO][rR]"),       // Исключающее ИЛИ
    NOT             ("[nN][oO][tT]"),       // Логическое отрицание
    OR              ("[oO][rR]"),           // Логическое ИЛИ
    AND             ("[aA][nN][dD]"),       // Логическое И
    PRINT           ("print|PRINT"),        // Вывод идентификатора
    SEMICOLON       (";"),                  // Точка с запятой
    ID              ("[a-zA-Z][\\w]*"),     // Индентификатор
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
    LINE            ("[\\r]*[\\n]{1}"),     // Перенос строки (служит разделителем команд)
    SPACE           ("[ \\s\t]+"),          // Пробел
    ASSIGNMENT      ("=");                 // Присваивание

    Pattern pattern;

    TokenType(String regEx) {
        this.pattern = Pattern.compile(regEx);
    }
}
