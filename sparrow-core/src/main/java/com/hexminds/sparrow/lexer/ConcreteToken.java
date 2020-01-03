package com.hexminds.sparrow.lexer;

public class ConcreteToken {
    protected Token token;
    protected Position position;

    public ConcreteToken() {
        token = new Token();
        position = new Position();
    }

    public ConcreteToken(ConcreteToken concreteToken) {
        token = new Token(concreteToken.token);
        position = new Position(concreteToken.position);
    }

    public ConcreteToken(Token token, Position position) {
        this.token = new Token(token);
        this.position = new Position(position);
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
        return;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
        return;
    }

    public void set(Token token, Position position) {
        this.token = token;
        this.position = position;
    }

    public void copy(ConcreteToken concreteToken) {
        token = new Token(concreteToken.token);
        position = new Position(concreteToken.position);
    }

    @Override
    public boolean equals(Object obj) {
        ConcreteToken concreteToken;

        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (!(obj instanceof ConcreteToken))
            return false;

        concreteToken = (ConcreteToken) obj;
        if (concreteToken.token.equals(token) && concreteToken.position.equals(position))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        return token.hashCode() * 31 + position.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%-16s %s", position.toString(), token.toString());
    }
}
