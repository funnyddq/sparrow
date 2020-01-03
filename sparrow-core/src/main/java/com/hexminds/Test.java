package com.hexminds;

import com.hexminds.util.FileUtil;
import org.apache.commons.io.*;

import java.nio.ByteOrder;

public class Test {
    public static void main(String[] args) {
        ByteOrder bo;
        String base;

        bo = ByteOrderParser.parseByteOrder("BIG_ENDIAN");
        System.out.println(bo.getClass());
        System.out.println(bo.getClass().getSimpleName());
        System.out.println(bo);

        base = FileUtil.getBaseDir();

        //        String s = "a\uD835\uDD46b\uD835\uDD47c\td好的e";
//        int size = s.codePointCount(0, s.length());
//        System.out.println(size);
//        int x;
//        int j;
//        for (int i = 0; i < size; i++) {
//            j = s.offsetByCodePoints(0, i);
//            x = s.codePointAt(j);
//            System.out.println(x);
//        }
//        String xxx = StringUtils.encode(s);
//        String yyy = StringUtils.decode(xxx);
//        System.out.println(xxx);
//        System.out.println(yyy);
        return;
    }
}
