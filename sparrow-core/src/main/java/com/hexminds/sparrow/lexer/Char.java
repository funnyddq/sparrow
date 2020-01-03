package com.hexminds.sparrow.lexer;

public class Char {
    public static boolean isDigit(int codePoint) {
        if (codePoint >= '0' && codePoint <= '9')
            return true;
        else
            return false;
    }

    public static boolean isAlpahbeta(int codePoint) {
        if (codePoint >= 'a' && codePoint <= 'z' || codePoint >= 'A' && codePoint <= 'Z')
            return true;
        else
            return false;
    }

    public static boolean isUnderScore(int codePoint) {
        if (codePoint == '_')
            return true;
        else
            return false;
    }

    public static boolean isBackslash(int codePoint) {
        if (codePoint == '\\')
            return true;
        else
            return false;
    }

    public static boolean isSlash(int codePoint) {
        if (codePoint == '/')
            return true;
        else
            return false;
    }

    public static boolean isIdStartChar(int codePoint) {
        return isAlpahbeta(codePoint) || isUnderScore(codePoint);
    }

    public static boolean isIdChar(int codePoint) {
        return isAlpahbeta(codePoint) || isUnderScore(codePoint) || isDigit(codePoint);
    }

    public static boolean isUpperCase(int codePoint) {
        if (codePoint >= 'A' && codePoint <= 'Z')
            return true;
        else
            return false;
    }

    public static boolean isLowerCase(int codePoint) {
        if (codePoint >= 'a' && codePoint <= 'z')
            return true;
        else
            return false;
    }

    public static boolean isLineFeed(int codePoint) {
        if (codePoint == '\n')
            return true;
        else
            return false;
    }

    public static boolean isBlankSpace(int codePoint) {
        if (codePoint == ' ')
            return true;
        else
            return false;
    }

    public static boolean isTab(int codePoint) {
        if (codePoint == '\t')
            return true;
        else
            return false;
    }

    public static boolean isBlank(int codePoint) {
        if (codePoint == ' ' || codePoint == '\t')
            return true;
        else
            return false;
    }

    public static boolean isCR(int codePoint) {
        if (codePoint == '\r')
            return true;
        else
            return false;
    }

    public static boolean isNull(int codePoint) {
        if (codePoint == '\0')
            return true;
        else
            return false;
    }

    public static boolean isEOF(int codePoint) {
        if (codePoint == -1)
            return true;
        else
            return false;
    }

    public static boolean isRightClosed(int codePoint) {
        if (codePoint == ')' || codePoint == ']')
            return true;
        else
            return false;
    }

    public static boolean isSemicolon(int codePoint) {
        if (codePoint == ';')
            return true;
        else
            return false;
    }

    public static boolean isComma(int codePoint) {
        if (codePoint == ',')
            return true;
        else
            return false;
    }

    public static boolean isAsterisk(int codePoint) {
        if (codePoint == '*')
            return true;
        else
            return false;
    }

    public static boolean isDoubleQuotation(int codePoint) {
        if (codePoint == '"')
            return true;
        else
            return false;
    }

    public static boolean isSingleQuotation(int codePoint) {
        if (codePoint == '\'')
            return true;
        else
            return false;
    }

    public static boolean isHex(int codePoint) {
        if (codePoint >= '0' && codePoint <= '9' || codePoint >= 'a' && codePoint <= 'f' || codePoint >= 'A' && codePoint <= 'F')
            return true;
        else
            return false;
    }

    public static boolean isBin(int codePoint) {
        if (codePoint == '0' || codePoint == '1')
            return true;
        else
            return false;
    }
}
