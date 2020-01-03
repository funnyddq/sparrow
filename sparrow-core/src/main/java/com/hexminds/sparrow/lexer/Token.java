package com.hexminds.sparrow.lexer;

import com.hexminds.util.StringUtils;

public class Token {
    protected TokenType type;
    protected String lexeme;

    public Token() {
        type = TokenType.UNKNOWN;
        lexeme = null;
    }

    public Token(Token token) {
        type = token.type;
        lexeme = token.lexeme;
    }

    public Token(TokenType type) {
        this.type = type;
        lexeme = null;
    }

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
        return;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
        return;
    }

    public void set(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public void copy(Token token) {
        type = token.type;
        lexeme = token.lexeme;
    }

    @Override
    public boolean equals(Object obj) {
        Token token;

        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (!(obj instanceof Token))
            return false;

        token = (Token) obj;
        if (type != token.type)
            return false;
        if (lexeme == null && token.lexeme != null)
            return false;
        if (lexeme.equals(token.lexeme))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        if (lexeme != null)
            return type.hashCode() * 31;
        else
            return type.hashCode() * 31 + lexeme.hashCode();
    }

    @Override
    public String toString() {
        if (lexeme != null) {
            return String.format("%-20s %s", type, StringUtils.encode(lexeme));
        }
        else
            return String.format("%-20s", type);
    }
}
