package com.hexminds.sparrow.parser;

import com.hexminds.sparrow.lexer.ConcreteToken;

import java.io.BufferedWriter;
import java.util.ArrayList;

public class Terminal extends Node {
    protected ConcreteToken ctoken;

    public Terminal(String name) {
        super(name);
    }

    public Terminal(String name, ConcreteToken ctoken) {
        this.name = name;
        this.ctoken = ctoken;
    }

    @Override
    public String toString() {
        StringBuilder sb;

        sb = new StringBuilder(name);
        if (ctoken == null)
            return sb.toString();
        sb.append("(");
        sb.append(ctoken.toString());
        sb.append(")");
        return sb.toString();

    }

    @Override
    public void print(BufferedWriter bw, int level, ArrayList<Boolean> last) {
        if (level != 0) {
            System.out.print(" "); // 缩进1个空格
            for (int i = 0; i < last.size() - 1; i++) {
                if (!last.get(i)) {
                    System.out.print("│");
                    if (i != 0)
                        System.out.print("   ");
                }
                else {
                    if (i != 0)
                        System.out.print("    ");
                }
            }
            if (!last.get(last.size() - 1))
                System.out.print("├─ ");
            else
                System.out.print("└─ ");
        }
        System.out.println(toString());
        return;
    }
}
