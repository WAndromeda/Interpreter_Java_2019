package com.white.andromeda;

import com.white.andromeda.AST.ExprNode;
import com.white.andromeda.AST.StmtNode;
import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import static com.white.andromeda.Parser.eval;

public class Translator {

    public static void translate(String path) throws IOException, InterruptedException {
        String code = "";
        File file = new File(path);
        FileInputStream inputStream = new FileInputStream(file);
        try {
            code = IOUtils.toString(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }

        Lexer l = new Lexer(code);
        List<Token> tokens = l.lex();
        //System.out.println(tokens);
        Parser p = new Parser(tokens);
        while(true) {
            StmtNode node = p.parse();
            if (node == null)
                break;
            eval(node);
        }
        p.clear();
    }
}
