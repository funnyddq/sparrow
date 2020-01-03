package com.hexminds.sparrow.parser;

public class NonTerminal extends Node {
    public NonTerminal(String name) {
        super(name);
    }

//    @Override
//    public String toString() {
//        StringBuilder sb;
//
//        sb = new StringBuilder(name);
//        if (nodes == null)
//            return sb.toString();
//        for (int i = 0; i < nodes.size(); i++) {
//            if (i != nodes.size() - 1) {
//                if (nodes.get(i) != null) {
//                    sb.append(nodes.get(i).toString() + "\n");
//                }
//            } else {
//                if (nodes.get(i) != null)
//                    sb.append(nodes.get(i).toString());
//            }
//        }
//        return sb.toString();
//    }
}
