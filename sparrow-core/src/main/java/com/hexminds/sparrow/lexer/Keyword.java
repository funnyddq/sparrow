package com.hexminds.sparrow.lexer;

import java.util.ArrayList;

public class Keyword {
    private static String[] _keywords = {
            "mode",
            "entry",
            "function",
            "segment",
            "call"

    };
    protected static TokenSet keywords;

    static {
        keywords = new TokenSet();
        for (String _keyword : _keywords) {
            keywords.add(new Token(TokenType.KEYWORD, _keyword));
        }
    }

    public static ArrayList<Token> getFirst(String str) {
        String s = str.toUpperCase();
        ArrayList<Token> ret = new ArrayList<>();

        if (s == null || s.length() == 0)
            return null;

        for (Token keyword : keywords) {
            if (keyword.getLexeme().startsWith(s))
                ret.add(keyword);
        }

        return ret;
    }

    public static boolean isKeyword(String str) {
        String s = str;
        // s = s.toUpperCase();

        if (s == null || s.length() == 0)
            return false;

        for (Token keyword : keywords) {
            if (keyword.getLexeme().equals(s))
                return true;
        }

        return false;
    }
}
