package com.white.andromeda;

import com.white.andromeda.AST.*;
import com.white.andromeda.Exception.ParserException;
import com.white.andromeda.Exception.SemanticsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import static com.white.andromeda.TokenType.*;

public class Parser {
    public static Hashtable<String, Integer> intVariables;

    private List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokens.removeIf(token -> token.type == TokenType.SPACE || token.type == TokenType.LINE || token.type == COMMENT || token.type == MULTI_COMMENT);
        intVariables = new Hashtable<>();
    }

    private Parser() {
    }

    private void error(String message) {
        if (pos < tokens.size()) {
            Token t = tokens.get(pos);
            throw new ParserException(message + "\nв строке:  " + t.line + "\nв позиции: " + t.pos);
        } else {
            throw new ParserException(message + " в конце файла");
        }
    }

    private Token match(TokenType... expected) {
        if (pos < tokens.size()) {
            Token curr = tokens.get(pos);
            if (Arrays.asList(expected).contains(curr.type)) {
                pos++;
                return curr;
            }
        }
        return null;
    }

    private Token require(TokenType... expected) {
        Token t = match(expected);
        if (t == null)
            error("Ожидается " + Arrays.toString(expected));
        return t;
    }

    private ExprNode parseElem() {
        Token num = match(TokenType.NUMBER);
        if (num != null)
            return new NumberNode(num);
        Token id = match(TokenType.ID);
        if (id != null)
            return new VarNode(id);
        error("Ожидается число или переменная");
        return null;
    }

    private ExprNode parseParens() {
        if (match(TokenType.LPAR) != null) {
            ExprNode e = parseExpression();
            require(TokenType.RPAR);
            return e;
        } else {
            return parseElem();
        }
    }

    private ExprNode parseUnary() {
        Token unary = match(NOT);
        ExprNode e1 = parseParens();
        if (unary != null)
            e1 = new UnaryOpNode(unary, e1);
        return e1;
    }

    private ExprNode parseDivMul() {
        ExprNode e1 = parseUnary();
        Token op;
        while ((op = match(TokenType.MUL, TokenType.DIV)) != null) {
            ExprNode e2 = parseUnary();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseAddSub() {
        ExprNode e1 = parseDivMul();
        Token op;
        while ((op = match(TokenType.ADD, TokenType.SUB)) != null) {
            ExprNode e2 = parseDivMul();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseLessLEqualGreaterGEqual() {
        ExprNode e1 = parseAddSub();
        Token op;
        while ((op = match(LESS, LESS_EQUAL, GREATER, GREATER_EQUAL)) != null) {
            ExprNode e2 = parseAddSub();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseEqualNEqual() {
        ExprNode e1 = parseLessLEqualGreaterGEqual();
        Token op;
        while ((op = match(EQUAL, NEQUAL)) != null) {
            ExprNode e2 = parseLessLEqualGreaterGEqual();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseAnd() {
        ExprNode e1 = parseEqualNEqual();
        Token op;
        while ((op = match(TokenType.AND)) != null) {
            ExprNode e2 = parseEqualNEqual();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseXor() {
        ExprNode e1 = parseAnd();
        Token op;
        while ((op = match(XOR)) != null) {
            ExprNode e2 = parseAnd();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseOr() {
        ExprNode e1 = parseXor();
        Token op;
        while ((op = match(OR)) != null) {
            ExprNode e2 = parseXor();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseLogicalAnd() {
        ExprNode e1 = parseOr();
        Token op;
        while ((op = match(LAND)) != null) {
            ExprNode e2 = parseOr();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseLogicalOr() {
        ExprNode e1 = parseLogicalAnd();
        Token op;
        while ((op = match(LOR)) != null) {
            ExprNode e2 = parseLogicalAnd();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private ExprNode parseAssignment() {
        ExprNode e1 = parseLogicalOr();
        Token op;
        while ((op = match(ASSIGNMENT, ASSIGNMENT_ADD, ASSIGNMENT_SUB, ASSIGNMENT_DIV, ASSIGNMENT_MUL, ASSIGNMENT_AND, ASSIGNMENT_XOR, ASSIGNMENT_OR)) != null) {
            ExprNode e2 = parseLogicalOr();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    private StmtNode parseStatement() {
        StmtNode stmtNode = null;
        Token token = require(PRINT, ID, IF, WHILE);
        switch (token.type) {
            case PRINT:
                stmtNode = new PrintNode(new VarNode(require(ID)));
                break;
            case ID:
                Token assign = require(ASSIGNMENT, ASSIGNMENT_ADD, ASSIGNMENT_SUB, ASSIGNMENT_DIV, ASSIGNMENT_MUL, ASSIGNMENT_AND, ASSIGNMENT_XOR, ASSIGNMENT_OR);
                stmtNode =  new AssignmentNode(new VarNode(token), assign, parseExpression());
                break;
            case WHILE:
                final ExprNode conditionWhile = parseLogicalCondition();
                List<StmtNode> stmtNodesWhile = parseLogicalBody(token);
                return new WhileNode(stmtNodesWhile, conditionWhile);
            case IF:
                final ExprNode conditionIf = parseLogicalCondition();
                List<StmtNode> stmtNodesIf = parseLogicalBody(token);
                if (match(ELSE) != null){
                    List<StmtNode> stmtNodesElse = parseLogicalBody(token);
                    return new IfElseNode(conditionIf, stmtNodesIf,  stmtNodesElse);
                }else
                    return new IfNode(conditionIf, stmtNodesIf);
        }
        require(SEMICOLON);
        return stmtNode;
    }

    public StmtNode parse() {
        if (pos >= tokens.size())
            return null;
        return parseStatement();
    }

    private ExprNode parseExpression() {
        if (pos >= tokens.size())
            return null;
        return parseAssignment();
    }

    private ExprNode parseLogicalCondition() {
        require(LPAR);
        ExprNode e = parseExpression();
        require(RPAR);
        return e;
    }

    private List<StmtNode> parseLogicalBody(Token token){
        List<StmtNode> stmtNodes = new ArrayList<>();
        if (match(LBRACE) != null){
            while (match(RBRACE) == null) {
                StmtNode node = parse();
                if (node == null) {
                    throw new ParserException("Ожидалось " + RBRACE + " для цикла|условия", token);
                }
                stmtNodes.add(node);
            }
        }else{
            StmtNode node = parse();
            if (node == null) {
                throw new ParserException("Пустое тело цикла|условия", token);
            }
            stmtNodes.add(node);
        }
        return stmtNodes;
    }

    public static void eval(StmtNode node) {
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            if (valueToBoolean(ifNode.condition.getValue()))
                for (StmtNode stmtNode : ifNode.ifStatements) {
                    eval(stmtNode);
                }
            else
                if (node instanceof IfElseNode) {
                    IfElseNode ifElseNode = (IfElseNode) node;
                    for (StmtNode stmtNode : ifElseNode.elseStatements) {
                        eval(stmtNode);
                    }
                }
            return;
        }else if (node instanceof WhileNode) {
            WhileNode whileNode = (WhileNode) node;
            while (valueToBoolean(whileNode.condition.getValue())){
                for (StmtNode stmtNode : whileNode.statements){
                    eval(stmtNode);
                }
            }
            return;
        }else if (node instanceof AssignmentNode) {
            AssignmentNode assignNode = (AssignmentNode) node;
            Integer left = assignNode.id.getValue();
            Integer right = assignNode.expr.getValue();
            if (assignNode.assign.type == ASSIGNMENT){
                assignNode.id.setValue(right);
                return;
            }else {
                Integer num = executeSpecialAssignments(left, right, assignNode.assign, assignNode);
                if (num != null) {
                    assignNode.id.setValue(num);
                    return;
                }
            }
        }else if (node instanceof PrintNode) {
            PrintNode printNode = (PrintNode) node;
            System.out.println(printNode.var.getValue());
            return;
        }
        throw new IllegalStateException(node.toString());
    }

    public static Integer evalExpr(ExprNode node){
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof NumberNode || node instanceof VarNode){
            return node.getValue();
        }else if (node instanceof UnaryOpNode) {
            UnaryOpNode unaryOpNode = (UnaryOpNode) node;
            switch (unaryOpNode.operation.type) {
                case NOT:
                    return binaryUnsignedNOT(unaryOpNode.operand.getValue());
            }
        } else if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            Integer left = binOp.left.getValue();
            Integer right = binOp.right.getValue();
            switch (binOp.op.type) {
                case ADD:
                    return left + right;
                case SUB:
                    return left - right;
                case MUL:
                    return left * right;
                case DIV:
                    return left / right;
                case XOR:
                    return left ^ right;
                case AND:
                    return left & right;
                case OR:
                    return left | right;
                case EQUAL:
                    if (left.equals(right)) return 1;
                    else return 0;
                case NEQUAL:
                    if (!left.equals(right)) return 1;
                    else return 0;
                case LESS:
                    if (left < right) return 1;
                    else return 0;
                case LESS_EQUAL:
                    if (left <= right) return 1;
                    else return 0;
                case GREATER:
                    if (left > right) return 1;
                    else return 0;
                case GREATER_EQUAL:
                    if (left >= right) return 1;
                    else return 0;
                case LAND:
                    if (valueToBoolean(left) && valueToBoolean(right)) return 1;
                    else return 0;
                case LOR:
                    if (valueToBoolean(left) || valueToBoolean(right)) return 1;
                    else return 0;
                case ASSIGNMENT:
                    Integer value = 1;
                    if (left == null)
                        value = null;
                    binOp.left.setValue(right);
                    if (value != null)
                        value = binOp.left.getValue();
                    return value;
                default:
                    Integer num = executeSpecialAssignments(left, right, binOp.op, binOp);
                    if (num != null) {
                        binOp.left.setValue(right);
                        return num;
                    }

            }
        }
        throw new IllegalStateException(node.toString());
    }

    private static Integer executeSpecialAssignments(Integer left, Integer right, Token operation, Node node){
        final String ifAssignError = "Недопустимое использование " + operation.type + " к неинициализированной переменной\nВ выражении:  " + node + "\nВ строке: " + operation.line + "\nВ позиции: " + operation.pos;
        if (left == null)
            throw new SemanticsException(ifAssignError);
        switch (operation.type) {
            case ASSIGNMENT_ADD:
                return left + right;
            case ASSIGNMENT_SUB:
                return left - right;
            case ASSIGNMENT_DIV:
                return left / right;
            case ASSIGNMENT_MUL:
                return left * right;
            case ASSIGNMENT_AND:
                return left & right;
            case ASSIGNMENT_XOR:
                return left ^ right;
            case ASSIGNMENT_OR:
                return left | right;
            default:
                return null;
        }
    }

    private static int binaryUnsignedNOT(int number) {
        String binary = Integer.toBinaryString(number);
        StringBuilder sum = new StringBuilder();
        for (char ch : binary.toCharArray()) {
            if (ch == '0')
                sum.append("1");
            else
                sum.append("0");
        }
        //System.out.println("СЛУЖЕБНОЕ: " + sum + "\n");
        return Integer.parseInt(sum.toString(), 2);
    }

    private static boolean valueToBoolean(Integer integer) {
        return integer != null && integer != 0;
    }

    public void clear() {
        intVariables.clear();
        tokens.clear();
        pos = 0;
    }

}
