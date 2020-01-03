package com.hexminds.sparrow.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Node {
    protected String name;
    protected List<Node> nodes;

    public Node() {
        name = null;
        nodes = null;
    }

    public Node(String name) {
        this.name = name;
        nodes = null;
    }

    public void add(Node node) {
        if (node == null)
            return;

        if (nodes == null)
            nodes = new ArrayList<>();

        nodes.add(node);
        return;
    }

    @Override
    public String toString() {
        StringBuilder sb;

        sb = new StringBuilder(name);
        if (nodes == null)
            return sb.toString();
        sb.append("[");
        for (int i = 0; i < nodes.size(); i++) {
            if (i != nodes.size() - 1) {
                if (nodes.get(i) != null)
                    sb.append(nodes.get(i).toString() + ", ");
            } else {
                if (nodes.get(i) != null)
                    sb.append(nodes.get(i).toString());
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public void print(BufferedWriter bw, int level, ArrayList<Boolean> last) throws IOException {
        if (level != 0) {
            bw.write(" "); // 缩进1个空格
            for (int i = 0; i < last.size() - 1; i++) {
                if (!last.get(i)) {
                    bw.write("│");
                    if (i != 0)
                        bw.write("   ");
                } else {
                    if (i != 0)
                        bw.write("    ");
                }
            }
            if (!last.get(last.size() - 1))
                bw.write("├─ ");
            else
                bw.write("└─ ");
        }
        bw.write(name);
        if (nodes == null)
            return;
        for (int i = 0; i < nodes.size(); i++) {
            if (i != nodes.size() - 1) {
                if (nodes.get(i) != null) {
                    last.add(false);
                    nodes.get(i).print(bw, level + 1, last);
                    last.remove(last.size() - 1);
                }
            } else {
                if (nodes.get(i) != null) {
                    last.add(true);
                    nodes.get(i).print(bw, level + 1, last);
                    last.remove(last.size() - 1);
                }
            }
        }
        return;
    }
}
