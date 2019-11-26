package com.white.andromeda;

import com.white.andromeda.AST.*;
import com.white.andromeda.Exception.ParserException;
import com.white.andromeda.Exception.SemanticsException;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import static com.white.andromeda.TokenType.*;

public class Parser {
    public static Hashtable<String, Integer> intVariables;
    private Stack<UnaryOpNode> conditions;
    private Stack<Integer> positions;
    private List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokens.removeIf(token -> token.type == TokenType.SPACE || token.type == TokenType.LINE || token.type == COMMENT);
        intVariables = new Hashtable<>();
        conditions = new Stack<>();
        positions = new Stack<>();
    }

    private Parser() {}

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

    private Integer skipBlock(){
        for (int i = positions.peek(); i < tokens.size(); i++){
            if (tokens.get(i).type == IF || tokens.get(i).type == WHILE){
                pos = i+1;
                parseLogicalCondition();
                conditions.push(new UnaryOpNode(tokens.get(i), null));
                positions.push(pos);
                i = skipBlock();
            }else
                if (tokens.get(i).type == RBRACE){
                    pos = i+1;
                    positions.pop();
                    conditions.pop();
                    return pos;
                }
        }
        throw new ParserException("Ожидалось " + RBRACE + " для условия|цикла расположенного", conditions.pop().op);
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

    private ExprNode parseUnary(){
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

    private ExprNode parseAssignment(){
        ExprNode e1 = parseLogicalOr();
        Token op;
        while ((op = match(ASSIGNMENT, ASSIGNMENT_ADD, ASSIGNMENT_SUB, ASSIGNMENT_DIV, ASSIGNMENT_MUL, ASSIGNMENT_AND, ASSIGNMENT_XOR, ASSIGNMENT_OR)) != null) {
            ExprNode e2 = parseLogicalOr();
            e1 = new BinOpNode(op, e1, e2);
        }
        return e1;
    }

    public ExprNode parse(){
        if (pos >= tokens.size())
            return null;
        boolean isNeedSemicolon = true;
        ExprNode e1 = null;
        Token service = require(PRINT, ID, IF, WHILE, RBRACE);
        if (service.type == PRINT)
            e1 = new UnaryOpNode( service, new VarNode(require(ID)) );
        else
            if (service.type == ID) {
                Token assign = require(ASSIGNMENT, ASSIGNMENT_ADD, ASSIGNMENT_SUB, ASSIGNMENT_DIV, ASSIGNMENT_MUL, ASSIGNMENT_AND, ASSIGNMENT_XOR, ASSIGNMENT_OR);
                e1 = parseExpression();
                e1 = new BinOpNode(assign, new VarNode(service), e1);
            }
            else
                if (service.type == IF || service.type == WHILE){
                    isNeedSemicolon = false;
                    UnaryOpNode unaryOpNode = new UnaryOpNode(service, parseLogicalCondition());
                    conditions.push(unaryOpNode);
                    positions.push(pos);
                    if (!valueToBoolean(unaryOpNode.operand.getValue())){
                        skipBlock();
                    }
                    e1 = parse();
                }
                    else
                        if (service.type == RBRACE){
                            isNeedSemicolon = false;
                            UnaryOpNode unaryOpNode = new UnaryOpNode(service, null);
                            checkBlockEnd(unaryOpNode);
                            e1 = parse();
                        }

        if (isNeedSemicolon) {
            require(SEMICOLON);
        }
        return e1;
    }

    private void checkBlockEnd(UnaryOpNode unaryOpNode){
        if (conditions.size() == 0 || positions.size() == 0){
            throw new ParserException("Лишняя закрывающая скобка", unaryOpNode.op);
        }
        if (conditions.peek().op.type == IF) {
            conditions.pop();
            positions.pop();
        }
        else
        if (conditions.peek().op.type == WHILE){
            if (conditions.peek().operand.getValue().equals(1))
                pos = positions.peek();
            else {
                conditions.pop();
                positions.pop();
            }
        }
    }

    public ExprNode parseExpression() {
        return parseAssignment();
    }

    public ExprNode parseLogicalCondition() {
        require(TokenType.LPAR);
        ExprNode e = parseExpression();
        require(TokenType.RPAR);
        require(LBRACE);
        return e;
    }

    public static Integer eval(ExprNode node) {
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof NumberNode || node instanceof VarNode) {
            return node.getValue();
        }else
            if (node instanceof UnaryOpNode){
                UnaryOpNode unaryOpNode = (UnaryOpNode) node;
                switch (unaryOpNode.op.type) {
                    case PRINT:
                        System.out.println(unaryOpNode.operand.getValue());
                        return null;
                    case NOT: return binaryUnsignedNOT(unaryOpNode.operand.getValue());
                }
            }
            else
                if (node instanceof BinOpNode) {
                    BinOpNode binOp = (BinOpNode) node;
                    final String ifAssignError = "Недопустимое использование " + binOp.op.type + " к неинициализированной переменной\nВ выражении:  " + binOp + "\nВ строке: " + binOp.op.line + "\nВ позиции: " + binOp.op.pos;
                    Integer right = binOp.right.getValue();
                    switch (binOp.op.type) {
                        case ADD: return binOp.left.getValue() + right;
                        case SUB: return binOp.left.getValue() - right;
                        case MUL: return binOp.left.getValue() * right;
                        case DIV: return binOp.left.getValue() / right;
                        case XOR: return binOp.left.getValue() ^ right;
                        case AND: return binOp.left.getValue() & right;
                        case OR:  return binOp.left.getValue() | right;
                        case EQUAL:
                            if (binOp.left.getValue().equals(right) )  return 1;
                            else   return 0;
                        case NEQUAL:
                            if (!binOp.left.getValue().equals(right))    return 1;
                            else     return 0;
                        case LESS:
                            if (binOp.left.getValue() < right)    return 1;
                            else return 0;
                        case LESS_EQUAL:
                            if (binOp.left.getValue() <= right)    return 1;
                            else return 0;
                        case GREATER:
                            if (binOp.left.getValue() > right)  return 1;
                            else  return 0;
                        case GREATER_EQUAL:
                            if (binOp.left.getValue() >= right) return 1;
                            else return 0;
                        case LAND:
                            if (valueToBoolean(binOp.left.getValue()) && valueToBoolean(right)) return 1;
                            else return 0;
                        case LOR:
                            if (valueToBoolean(binOp.left.getValue()) || valueToBoolean(right))  return 1;
                            else return 0;
                        case ASSIGNMENT:
                            Integer value = 1;
                            if (binOp.left.getValue() == null)
                                value = null;
                            binOp.left.setValue(right);
                            if (value != null)
                                value = binOp.left.getValue();
                            return value;
                        case ASSIGNMENT_ADD:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() + right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_SUB:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() - right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_DIV:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() / right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_MUL:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() * right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_AND:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() & right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_XOR:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() ^ right);
                            return binOp.left.getValue();
                        case ASSIGNMENT_OR:
                            if (binOp.left.getValue() == null)
                                throw new SemanticsException(ifAssignError);
                            binOp.left.setValue(binOp.left.getValue() | right);
                            return binOp.left.getValue();
                    }
                }
        throw new IllegalStateException(node.toString());
    }


    private static int binaryUnsignedNOT(int number){
        String binary  = Integer.toBinaryString(number);
        StringBuilder sum = new StringBuilder();
        for (char ch : binary.toCharArray()){
            if (ch == '0')
                sum.append("1");
            else
                sum.append("0");
        }
        //System.out.println("СЛУЖЕБНОЕ: " + sum + "\n");
        return Integer.parseInt(sum.toString(), 2);
    }

    private static boolean valueToBoolean(Integer integer){
        return integer != null && integer != 0;
    }

    public void clear(){
        intVariables.clear();
        conditions.clear();
        positions.clear();
        tokens.clear();
        pos = 0;
    }

}
