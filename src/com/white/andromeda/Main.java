package com.white.andromeda;

import com.white.andromeda.AST.ExprNode;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.white.andromeda.Parser.eval;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int a = 5;
        System.out.println(5 + (a = 6) );
        int x = 0, y = 0;
        a =  a = 5 + 2 + a;
        a = ~( a=5+
                        ~(
                                x & y
                        )
                & ~0
        );
        while(true){
            menu();
            int com = scanner.nextInt();
            switch (com){
                case 1:
                    Translator.translate("./src/com/white/andromeda/resources/code.txt");
                    break;
                case 2:
                    Translator.translate("./src/com/white/andromeda/resources/factorial.txt");
                    break;
                case 0:
                    return;
            }

        }
    }

    public static void  menu(){
        System.out.println("Николаев Никита Сергеевич | ИКБО-13-17 | ТАиФЯ\r\nAST дерево (интерпретатор)");
        System.out.println("1. Выполнить разбор стандартного файла");
        System.out.println("2. Выполнить разбор вычисления факториала");
        System.out.println("0. Выйти");
        System.out.print("> ");
    }
}
