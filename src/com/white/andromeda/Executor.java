package com.white.andromeda;

import com.white.andromeda.AST.*;
import com.white.andromeda.Exception.SemanticsException;

import java.util.*;

import static com.white.andromeda.TokenType.ASSIGNMENT;

public class Executor {

    public static void executeAProgram(ArrayList<StmtNode> stmtNodes){
        stmtNodes.removeIf(stmtNode -> stmtNode instanceof EmptyNode);
        ArrayDeque<Map<String, Integer>> variablesMapDeque = new ArrayDeque<>();
        variablesMapDeque.push(new HashMap<>());
        for (StmtNode stmtNode : stmtNodes)
            eval(stmtNode, variablesMapDeque);
    }

    public static void eval(StmtNode node, ArrayDeque<Map<String, Integer>> variablesMapDeque) {
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof EmptyNode) {
            return;
        }else if (node instanceof BlockStatement) {
            BlockStatement blockStatement = (BlockStatement) node;
            variablesMapDeque.push(new HashMap<>());
            for (StmtNode stmtNode : blockStatement.statements)
                eval(stmtNode, variablesMapDeque);
            variablesMapDeque.pop();
            return;
        }
        else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            variablesMapDeque.push(new HashMap<>());
            if (valueToBoolean(ifNode.condition.getValue(variablesMapDeque)))
                eval(ifNode.ifStatement, variablesMapDeque);
            else
                if (node instanceof IfElseNode) {
                    IfElseNode ifElseNode = (IfElseNode) node;
                    eval(ifElseNode.elseStatement, variablesMapDeque);
                }
            variablesMapDeque.pop();
            return;
        }else if (node instanceof WhileNode) {
            WhileNode whileNode = (WhileNode) node;
            variablesMapDeque.push(new HashMap<>());
            while (valueToBoolean(whileNode.condition.getValue(variablesMapDeque))) {
                eval(whileNode.statement, variablesMapDeque);
            }
            variablesMapDeque.pop();
            return;
        }else if (node instanceof ForNode) {
            ForNode forNode = (ForNode) node;
            variablesMapDeque.push(new HashMap<>());
            for (eval(forNode.stmtNodeLeft, variablesMapDeque); valueToBoolean(forNode.conditionMiddle.getValue(variablesMapDeque)); eval(forNode.operationRight, variablesMapDeque))
                eval(forNode.statement, variablesMapDeque);
            variablesMapDeque.pop();
            return;
        }else if (node instanceof ExprStmtNode) {
            ExprStmtNode exprStmtNode = (ExprStmtNode) node;
            for (ExprNode exprNode : exprStmtNode.expr)
                evalExpr(exprNode, variablesMapDeque);
            return;
        }else if (node instanceof PrintNode) {
            PrintNode printNode = (PrintNode) node;
            System.out.println(printNode.var.getValue(variablesMapDeque));
            return;
        }
        throw new IllegalStateException(node.toString());
    }

    public static int evalExpr(ExprNode node, ArrayDeque<Map<String, Integer>> variablesMapDeque){
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof NumberNode || node instanceof VarNode){
            return node.getValue(variablesMapDeque);
        }else if (node instanceof UnaryOpNode) {
            UnaryOpNode unaryOpNode = (UnaryOpNode) node;
            switch (unaryOpNode.operation.type) {
                case NOT:
                    return binaryUnsignedNOT(unaryOpNode.operand.getValue(variablesMapDeque));
                case SUB:
                    return -unaryOpNode.operand.getValue(variablesMapDeque);
                case DEC:
                    if (unaryOpNode.operand instanceof VarNode) {
                        VarNode varNode = (VarNode) unaryOpNode.operand;
                        varNode.setValue(varNode.getValue(variablesMapDeque) - 1, variablesMapDeque);
                        return varNode.getValue(variablesMapDeque);
                    }else throw new SemanticsException("Декремент может применяться только для переменных", unaryOpNode.operation);
                case INC:
                    if (unaryOpNode.operand instanceof VarNode) {
                        VarNode varNode = (VarNode) unaryOpNode.operand;
                        varNode.setValue(varNode.getValue(variablesMapDeque) + 1, variablesMapDeque);
                        return varNode.getValue(variablesMapDeque);
                    }else throw new SemanticsException("Инкремент может применяться только для переменных", unaryOpNode.operation);
            }
        } else if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            Integer right = binOp.right.getValue(variablesMapDeque);

            /*
                Присваивание вынесено отдельно из-за того, что получать значение left нельзя, ибо переменная может быть не инциализирована (из-за чего вылетает Exception),
                Поэтому, чтобы не писать каждый раз binOp.left.getValue(), а просто использовать left, был использован данный костыль
            */

            if (binOp.op.type == ASSIGNMENT) {
                if (binOp.left instanceof VarNode) {
                    VarNode var = (VarNode) binOp.left;
                    var.setValue(right, variablesMapDeque);
                    return right;
                }else
                    throw new SemanticsException("Присвоить значение можно только переменным", binOp.op);
            }

            Integer left = binOp.left.getValue(variablesMapDeque);
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
                default:
                    Integer result = executeSpecialOperation(left, right, binOp.op);
                    if (result != null) {
                        if (binOp.left instanceof VarNode) {
                            VarNode var = (VarNode) binOp.left;
                            var.setValue(result, variablesMapDeque);
                        }else
                            throw new SemanticsException("Присвоить значение можно только переменным", binOp.op);
                        return result;
                    }

            }
        }
        throw new IllegalStateException(node.toString());
    }

    private static Integer executeSpecialOperation(Integer left, Integer right, Token operation){
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
        //System.out.println("СЛУЖЕБНОЕ: " + binary);
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

}
