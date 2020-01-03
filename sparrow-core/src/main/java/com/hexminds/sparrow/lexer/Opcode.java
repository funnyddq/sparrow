package com.hexminds.sparrow.lexer;

import java.util.ArrayList;

public class Opcode {
    protected static TokenSet opcodes;

    static {
        opcodes = new TokenSet();
        opcodes.add(new Token(TokenType.OPCODE, "MOV"));
        opcodes.add(new Token(TokenType.OPCODE, "INC"));
        opcodes.add(new Token(TokenType.OPCODE, "ADD"));
        opcodes.add(new Token(TokenType.OPCODE, "SUB"));
        opcodes.add(new Token(TokenType.OPCODE, "MOV"));
        opcodes.add(new Token(TokenType.OPCODE, "MOV"));
        opcodes.add(new Token(TokenType.OPCODE, "INT"));
    }

    public static ArrayList<Token> getFirst(String str) {
        String s = str.toUpperCase();
        ArrayList<Token> ret = new ArrayList<>();

        if (s == null || s.length() == 0)
            return null;

        for (Token opcode : opcodes) {
            if (opcode.getLexeme().startsWith(s))
                ret.add(opcode);
        }

        return ret;
    }

    public static boolean isOpcode(String str) {
        String s = str.toUpperCase();

        if (s == null || s.length() == 0)
            return false;

        for (Token opcode : opcodes) {
            if (opcode.getLexeme().equals(s))
                return true;
        }

        return false;
    }
}
