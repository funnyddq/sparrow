package com.hexminds.sparrow.lexer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ConcreteTokenList implements Iterable<ConcreteToken> {
    protected List<ConcreteToken> concreteTokens;

    public ConcreteTokenList() {
        concreteTokens = new ArrayList<>();
    }

    public List<ConcreteToken> getTokens() {
        return concreteTokens;
    }

    public void setTokens(ArrayList<ConcreteToken> concreteTokens) {
        this.concreteTokens = concreteTokens;
    }

    public Iterator<ConcreteToken> iterator() {
        return concreteTokens.iterator();
    }

    public int size() {
        return concreteTokens.size();
    }

    public void clear() {
        concreteTokens.clear();
        return;
    }

    public ConcreteToken get(int index) {
        return concreteTokens.get(index);
    }

    public ConcreteToken getLast() {
        return concreteTokens.get(concreteTokens.size() - 1);
    }

    public void add(ConcreteToken concreteToken) {
        if (concreteToken == null)
            return;

        concreteTokens.add(concreteToken);
        return;
    }

    public ConcreteToken remove(int index) {
        return concreteTokens.remove(index);
    }

    public ConcreteToken removeLast() {
        return concreteTokens.remove(concreteTokens.size() - 1);
    }

    public void print(PrintStream ps) {
        if (ps == null)
            return;

        for (ConcreteToken concreteToken : concreteTokens) {
            ps.println(concreteToken);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb;

        sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size(); i++) {
            sb.append(concreteTokens.get(i));
            if (i != size() - 1)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
