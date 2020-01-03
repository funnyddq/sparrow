package com.hexminds.sparrow.lexer;

import java.util.ArrayList;

public class Separator {
    protected static TokenSet separators;

    static {
        separators = new TokenSet();
        separators.add(new Token(TokenType.LEFT_PARENTHESIS, "("));
        separators.add(new Token(TokenType.RIGHT_PARENTHESIS, ")"));
        separators.add(new Token(TokenType.LEFT_SQUARE_BRACKET, "["));
        separators.add(new Token(TokenType.RIGHT_SQUARE_BRACKET, "]"));
        separators.add(new Token(TokenType.LEFT_CURLY_BRACE, "{"));
        separators.add(new Token(TokenType.LEFT_CURLY_BRACE, "{<"));
        separators.add(new Token(TokenType.RIGHT_CURLY_BRACE, "}"));
        separators.add(new Token(TokenType.COMMA, ","));
        separators.add(new Token(TokenType.SEMICOLON, ";"));
        separators.add(new Token(TokenType.COLON, ":"));
        separators.add(new Token(TokenType.DOT_MARK, "."));
    }

    public static ArrayList<Token> getFirst(String s) {
        ArrayList<Token> ret = new ArrayList<>();

        if (s == null || s.length() == 0)
            return null;

        for (Token separator : separators) {
            if (separator.getLexeme().startsWith(s))
                ret.add(separator);
        }

        return ret;
    }

    public static boolean isSeparator(String s) {
        if (s == null || s.length() == 0)
            return false;

        for (Token separator : separators) {
            if (separator.getLexeme().equals(s))
                return true;
        }

        return false;
    }
}
