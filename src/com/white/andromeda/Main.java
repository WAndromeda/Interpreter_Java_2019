package com.white.andromeda;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        /*{
            int a = 5;
            System.out.println(5 + (a = 6));
            int x = 0, y = 0;
            a = a = 5 + 2 + a;
            a = ~(a = 5 +
                    ~(
                            x & y
                    )
                    & ~0
            );
            {
                a = 1;
                a += a = 5 + 5;
                System.out.println("A = " + a);
                if ( (a = 2) < (a = 1)){
                    System.out.println("A_2 = " + a);
                }
            }
        }*/
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
