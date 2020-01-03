package com.hexminds.sparrow.lexer;

import com.hexminds.util.StringUtils;

import java.util.ArrayList;

public class Operator {
    protected static TokenSet operators;

    static {
        operators = new TokenSet();
        operators.add(new Token(TokenType.ADD, "+"));
        operators.add(new Token(TokenType.SUB, "-"));
        operators.add(new Token(TokenType.MUL, "*"));
        operators.add(new Token(TokenType.DIV, "/"));
        operators.add(new Token(TokenType.DIV, "^"));
        operators.add(new Token(TokenType.DIV, "="));
        operators.add(new Token(TokenType.DIV, ">"));
        operators.add(new Token(TokenType.DIV, ">="));
        operators.add(new Token(TokenType.DIV, "=="));
        operators.add(new Token(TokenType.DIV, "<="));
        operators.add(new Token(TokenType.DIV, "<"));
        operators.add(new Token(TokenType.DIV, "!"));
    }

    public static ArrayList<Token> getFirst(String s) {
        ArrayList<Token> ret = new ArrayList<>();

        if (s == null || s.length() == 0)
            return null;

        for (Token operator : operators) {
            if (operator.getLexeme().startsWith(s))
                ret.add(operator);
        }

        return ret;
    }

    public static boolean isOperator(String s) {
        if (s == null || s.length() == 0)
            return false;

        for (Token operator : operators) {
            if (operator.getLexeme().equals(s))
                return true;
        }

        return false;
    }

    public static boolean isOperatorStart(int codePoint) {
        for (Token operator : operators) {
            if (operator.getLexeme().startsWith(StringUtils.codePointsToString(codePoint)))
                return true;
        }

        return false;
    }
}
