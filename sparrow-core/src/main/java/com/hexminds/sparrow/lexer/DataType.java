package com.hexminds.sparrow.lexer;

import java.util.ArrayList;

public class DataType {
    private static String[] _dataTypes = {
            "byte",
            "word",
            "dword",
    };
    protected static TokenSet dataTypes;

    static {
        dataTypes = new TokenSet();
        for (String dataType : _dataTypes) {
            dataTypes.add(new Token(TokenType.DATA_TYPE, dataType));
        }
    }

    public static ArrayList<Token> getFirst(String str) {
        String s = str.toUpperCase();
        ArrayList<Token> ret = new ArrayList<>();

        if (s == null || s.length() == 0)
            return null;

        for (Token dataType : dataTypes) {
            if (dataType.getLexeme().startsWith(s))
                ret.add(dataType);
        }

        return ret;
    }

    public static boolean isDataType(String str) {
        String s = str;
        // s = s.toUpperCase();

        if (s == null || s.length() == 0)
            return false;

        for (Token dataType : dataTypes) {
            if (dataType.getLexeme().equals(s))
                return true;
        }

        return false;
    }
}
