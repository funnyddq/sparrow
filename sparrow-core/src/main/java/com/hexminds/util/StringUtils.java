package com.hexminds.util;

import java.util.ArrayList;

public class StringUtils {
    public static String codePointsToString(int[] codePoints, int count) {
        if (codePoints == null || count <= 0)
            throw new IllegalArgumentException();

        return new String(codePoints, 0, count);
    }

    public static String codePointsToString(int codePoint) {
        int[] c = new int[1];
        c[0] = codePoint;
        return codePointsToString(c, 1);
    }

    public static String codePointsToString(ArrayList<Integer> codePoints) {
        if (codePoints == null)
            throw new IllegalArgumentException();

        if (codePoints.size() == 0)
            return "";
        int[] buff = new int[codePoints.size()];
        for (int i = 0; i < buff.length; i++)
            buff[i] = codePoints.get(i);
        return new String(buff, 0, buff.length);
    }

    public static int[] StringToCodePoints(String s) {
        int size = 0;
        int[] result;
        if (s == null)
            throw new IllegalArgumentException();

        if (s.length() == 0)
            return new int[0];
        size = s.codePointCount(0, s.length());
        result = new int[size];
        int pos;
        for (int i = 0; i < size; i++) {
            pos = s.offsetByCodePoints(0, i);
            result[i] = s.codePointAt(pos);
        }
        return result;
    }

    public static String encode(String s) {
        int[] result;
        StringBuilder sb;

        if (s == null)
            return null;
        if (s.length() == 0)
            return "";

        sb = new StringBuilder();
        result = StringToCodePoints(s);
        sb.append("\"");
        for (int i = 0; i < result.length; i++) {
            if (result[i] == '\\')
                sb.append("\\\\");
            else if (result[i] == '\"')
                sb.append("\\\"");
            else if (result[i] == '\'')
                sb.append("\\\'");
            else if (result[i] == '\r')
                sb.append("\\r");
            else if (result[i] == '\n')
                sb.append("\\n");
            else if (result[i] == '\f')
                sb.append("\\f");
            else if (result[i] == '\t')
                sb.append("\\t");
            else if (result[i] == '\b')
                sb.append("\\b");
            else if (result[i] == '\0')
                sb.append("\\0");
            else if (result[i] >= 0 && result[i] <= 0x1f || result[i] == 0x7f) {
                sb.append("\\u");
                sb.append(String.format("%08x", result[i]));
            } else if (result[i] <= 0x7f)
                sb.append(new String(result, i, 1));
            else {
                //sb.append(new String(result, i, 1));
                sb.append("\\u");
                sb.append(String.format("%08x", result[i]));
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String decode(String s) {
        String out;
        int[] result;
        StringBuilder sb;

        if (s == null)
            return null;
        if (s.length() < 2)
            return "";

        sb = new StringBuilder();
        result = StringToCodePoints(s);
        for (int i = 1; i < result.length - 1; i++) {
            if (result[i] == '\\') {
                if (result[i + 1] == '\\') {
                    sb.append("\\");
                    i++;
                }
                else if (result[i + 1] == '\"') {
                    sb.append("\"");
                    i++;
                }
                else if (result[i + 1] == '\'') {
                    sb.append("\'");
                    i++;
                }
                else if (result[i + 1] == 'r') {
                    sb.append("\r");
                    i++;
                }
                else if (result[i + 1] == 'n') {
                    sb.append("\n");
                    i++;
                }
                else if (result[i + 1] == 'f') {
                    sb.append("\f");
                    i++;
                }
                else if (result[i + 1] == 't') {
                    sb.append("\t");
                    i++;
                }
                else if (result[i + 1] == 'b') {
                    sb.append("\b");
                    i++;
                }
                else if (result[i + 1] == '0') {
                    sb.append("\0");
                    i++;
                } else if (result[i + 1] == 'u') {
                    int x[] = new int[1];
                    x[0] = Integer.parseInt(new String(result, i + 2, 8), 16);
                    sb.append(new String(x, 0, 1));
                    i += 9;
                }
            } else {
                sb.append(new String(result, i, 1));
            }
        }
        return sb.toString();
    }
}
