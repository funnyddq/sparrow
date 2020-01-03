package com.hexminds.sparrow.lexer;

import java.util.ArrayList;
import java.util.HashMap;

public class Register {
    protected static String[] r8 = {
            "AL",
            "CL",
            "DL",
            "BL",
            "AH",
            "CH",
            "DH",
            "BH"
    };

    protected static String[] r16 = {
            "AX",
            "CX",
            "DX",
            "BX",
            "SI",
            "DI",
            "SP",
            "BP"
    };

    protected static String[] seg16 = {
            "CS",
            "DS",
            "SS",
            "ES"
    };

    protected static String[] seg32 = {
            "EAX",
            "EBX",
            "ECX",
            "EDX"
    };

    protected static String[] seg64 = {
            "RAX",
            "RBX",
            "RCX",
            "RDX"
    };

    protected static HashMap<String, TokenType> registers;

    static {
        registers = new HashMap<>();
        for (String s : r8) {
            registers.put(s, TokenType.REGISTER);
        }
        for (String s : r16) {
            registers.put(s, TokenType.REGISTER);
        }
        for (String s : seg32) {
            registers.put(s, TokenType.REGISTER);
        }
        for (String s : seg64) {
            registers.put(s, TokenType.REGISTER);
        }
    }

    public static ArrayList<TokenType> getFirst(String str) {
        ArrayList<TokenType> ret = new ArrayList<>();
        String s = str.toUpperCase();

        if (s == null || s.length() == 0)
            return null;

        for (String register : registers.keySet()) {
            if (register.startsWith(s))
                ret.add(registers.get(s));
        }

        return ret;
    }

    public static boolean isRegister(String str) {
        String s = str.toUpperCase();
        if (s == null || s.length() == 0)
            return false;

        for (String register : registers.keySet()) {
            if (register.equals(s))
                return true;
        }
        return false;
    }

//    public Type getType(String s) {
//        if (s == null || s.length() == 0)
//            return Type.UNKNOWN;
//
//        ArrayList list = getFirst(s);
//        if (list.size() == 0)
//            return Type.UNKNOWN;
//        for (String op : oneChar) {
//            if (op.startsWith(s))
//                ret.add(s);
//        }
//
//        for (String op : twoChars) {
//            if (op.startsWith(s))
//                ret.add(s);
//        }
//    }
}

