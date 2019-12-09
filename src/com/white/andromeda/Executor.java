package com.white.andromeda;

import com.white.andromeda.AST.*;
import com.white.andromeda.Exception.SemanticsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.white.andromeda.TokenType.ASSIGNMENT;

public class Executor {

    //public static HashMap<String, Integer> intVariables = new HashMap<>();

    public static void executeAProgram(ArrayList<StmtNode> stmtNodes){
        stmtNodes.removeIf(stmtNode -> stmtNode instanceof EmptyNode);
        List<Map<String, Integer>> variablesMapList = new ArrayList<>();
        variablesMapList.add(new HashMap<>());
        for (StmtNode stmtNode : stmtNodes)
            eval(stmtNode, variablesMapList);
    }

    public static void eval(StmtNode node, List<Map<String, Integer>> variablesMapList) {
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof EmptyNode) {
            return;
        }else if (node instanceof BlockStatement) {
            BlockStatement blockStatement = (BlockStatement) node;
            variablesMapList.add(new HashMap<>());
            for (StmtNode stmtNode : blockStatement.statements)
                eval(stmtNode, variablesMapList);
            variablesMapList.remove(variablesMapList.size()-1);
            return;
        }
        else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            variablesMapList.add(new HashMap<>());
            if (valueToBoolean(ifNode.condition.getValue(variablesMapList)))
                eval(ifNode.ifStatement, variablesMapList);
            else
                if (node instanceof IfElseNode) {
                    IfElseNode ifElseNode = (IfElseNode) node;
                    eval(ifElseNode.elseStatement, variablesMapList);
                }
            variablesMapList.remove(variablesMapList.size()-1);
            return;
        }else if (node instanceof WhileNode) {
            WhileNode whileNode = (WhileNode) node;
            variablesMapList.add(new HashMap<>());
            while (valueToBoolean(whileNode.condition.getValue(variablesMapList))) {
                eval(whileNode.statement, variablesMapList);
            }
            variablesMapList.remove(variablesMapList.size()-1);
            return;
        }else if (node instanceof ForNode) {
            ForNode forNode = (ForNode) node;
            variablesMapList.add(new HashMap<>());
            for (eval(forNode.stmtNodeLeft, variablesMapList); valueToBoolean(forNode.conditionMiddle.getValue(variablesMapList)); eval(forNode.operationRight, variablesMapList))
                eval(forNode.statement, variablesMapList);
            variablesMapList.remove(variablesMapList.size()-1);
            return;
        }else if (node instanceof ExprStmtNode) {
            ExprStmtNode exprStmtNode = (ExprStmtNode) node;
            for (ExprNode exprNode : exprStmtNode.expr)
                evalExpr(exprNode, variablesMapList);
            return;
        }else if (node instanceof PrintNode) {
            PrintNode printNode = (PrintNode) node;
            System.out.println(printNode.var.getValue(variablesMapList));
            return;
        }
        throw new IllegalStateException(node.toString());
    }

    public static Integer evalExpr(ExprNode node, List<Map<String, Integer>> variablesMapList){
        //System.out.println("СЛУЖЕБНОЕ: " + node.toString());
        if (node instanceof NumberNode || node instanceof VarNode){
            return node.getValue(variablesMapList);
        }else if (node instanceof UnaryOpNode) {
            UnaryOpNode unaryOpNode = (UnaryOpNode) node;
            switch (unaryOpNode.operation.type) {
                case NOT:
                    return binaryUnsignedNOT(unaryOpNode.operand.getValue(variablesMapList));
                case DEC:
                    if (unaryOpNode.operand instanceof VarNode) {
                        VarNode varNode = (VarNode) unaryOpNode.operand;
                        varNode.setValue(varNode.getValue(variablesMapList) - 1, variablesMapList);
                        return varNode.getValue(variablesMapList);
                    }else throw new SemanticsException("Декремент может применяться только для переменных");
                case INC:
                    if (unaryOpNode.operand instanceof VarNode) {
                        VarNode varNode = (VarNode) unaryOpNode.operand;
                        varNode.setValue(varNode.getValue(variablesMapList) + 1, variablesMapList);
                        return varNode.getValue(variablesMapList);
                    }else throw new SemanticsException("Инкремент может применяться только для переменных");
            }
        } else if (node instanceof BinOpNode) {
            BinOpNode binOp = (BinOpNode) node;
            Integer right = binOp.right.getValue(variablesMapList);
            /*
                Присваивание вынесено отдельно из-за того, что получать значение left нельзя, ибо переменная может быть не инциализирована (из-за чего вылетает Exception),
                Поэтому, чтобы не писать каждый раз binOp.left.getValue(), а просто использовать left, был использован данный костыль
            */
            if (binOp.op.type == ASSIGNMENT) {
                if (binOp.left instanceof VarNode) {
                    VarNode var = (VarNode) binOp.left;
                    var.setValue(right, variablesMapList);
                    return binOp.left.getValue(variablesMapList);
                }else
                    throw new SemanticsException("Присвоить значение можно только переменным");
            }
            Integer left = binOp.left.getValue(variablesMapList);
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
                            var.setValue(result, variablesMapList);
                        }else
                            throw new SemanticsException("Присвоить значение можно только переменным");
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
