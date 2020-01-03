package com.hexminds.sparrow.lexer;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TokenSet implements Iterable<Token> {
    protected Set<Token> tokens;

    public TokenSet() {
        tokens = new HashSet<>();
    }

    public Set<Token> getTokens() {
        return tokens;
    }

    public void setTokens(Set<Token> tokens) {
        this.tokens = tokens;
    }

    public Iterator<Token> iterator() {
        return tokens.iterator();
    }

    public int size() {
        return tokens.size();
    }

    public void clear() {
        tokens.clear();
        return;
    }

    public boolean contains(Token token) {
        if (token == null)
            return false;

        for (Token t : tokens) {
            if (token.equals(t))
                return true;
        }

        return false;
    }

    public boolean contains(TokenType type, String lexeme) {
        for (Token token : tokens) {
            if (lexeme != null) {
                if (type == token.getType() && lexeme.equals(token.getLexeme()))
                    return true;
            } else {
                if (type == token.getType() && token.getLexeme() == null)
                    return true;
            }
        }

        return false;
    }

    public Token get(TokenType type, String lexeme) {
        for (Token token : tokens) {
            if (lexeme != null) {
                if (type == token.getType() && lexeme.equals(token.getLexeme()))
                    return token;
            } else {
                if (type == token.getType() && token.getLexeme() == null)
                    return token;
            }
        }

        return null;
    }

    public void add(Token token) {
        if (token == null)
            return;

        tokens.add(token);
        return;
    }

    public void remove(Token token) {
        if (token == null)
            return;

        tokens.remove(token);
        return;
    }

    public void print(PrintStream ps) {
        if (ps == null)
            return;

        for (Token token : tokens) {
            ps.println(token);
        }
    }

    @Override
    public String toString() {
        Iterator<Token> iterator;
        StringBuilder sb;

        iterator = tokens.iterator();
        sb = new StringBuilder();
        sb.append("[");
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            if (iterator.hasNext())
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
