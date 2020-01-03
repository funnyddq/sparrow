package com.hexminds.sparrow.lexer;

import com.hexminds.sparrow.exception.ScannerException;

import java.io.IOException;
import java.util.ArrayList;

public interface CharStream {
    public int read() throws IOException, ScannerException;
    public void pushBack(int codePoint);
    public void pushBack(int[] codePoints);
    public void pushBack(ArrayList<Integer> codePoints);
}
