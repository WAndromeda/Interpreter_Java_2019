package com.white.andromeda;

import com.white.andromeda.AST.StmtNode;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Translator {

    public static void translate(String path) {
        String code = "";
        try {
            code = Files.readString(FileSystems.getDefault().getPath(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Lexer l = new Lexer(code);
        List<Token> tokens = l.lex();
        //System.out.println(tokens);
        Parser p = new Parser(tokens);
        ArrayList<StmtNode> stmtNodes = (ArrayList<StmtNode>) p.parseProgram();
        //System.out.println(stmtNodes);
        Executor.executeAProgram(stmtNodes);
    }
}
