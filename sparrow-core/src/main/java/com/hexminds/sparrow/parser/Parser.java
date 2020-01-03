package com.hexminds.sparrow.parser;

import com.hexminds.sparrow.exception.LexerException;
import com.hexminds.sparrow.exception.ScannerException;
import com.hexminds.sparrow.lexer.ConcreteToken;
import com.hexminds.sparrow.lexer.ConcreteTokenList;
import com.hexminds.sparrow.lexer.Lexer;
import com.hexminds.sparrow.lexer.Position;
import com.hexminds.sparrow.lexer.Token;
import com.hexminds.sparrow.lexer.TokenType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    protected Lexer lexer;
    protected Node compilationUnit;
    protected BufferedWriter bw;

    public Parser(Lexer lexer, BufferedWriter bw) {
        this.lexer = lexer;
        this.bw = bw;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public void setLexer(Lexer lexer, BufferedWriter bw) {
        this.lexer = lexer;
        this.bw = bw;
        return;
    }

    public boolean parse() throws IOException {
        boolean ret;
        Node CompilationUnit;
        Node node = null;

        compilationUnit = new NonTerminal("CompilationUnit");

        try {
            ret = parseMode(compilationUnit);
            ret = parseEntry(compilationUnit);
            ret = parseFunctionDeclaration(compilationUnit);
            do {
                ret = parseSegment(compilationUnit);
            } while (ret);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
        ArrayList<Boolean> last = new ArrayList<>();
        last.add(true);
        compilationUnit.print(bw, 0, last);
        System.out.println(compilationUnit);
        return true;
    }

    public boolean parseMode(Node node) throws LexerException, ScannerException {
        int result;
        ConcreteToken ctoken;
        Token token;
        Position position;
        String message;
        ConcreteTokenList alreadyRead = new ConcreteTokenList();
        Node n0, n1, n2;
        boolean ret;

        ignoreNewLine();
        ret = expectMay(TokenType.KEYWORD, "mode", alreadyRead);
        if (!ret)
            return false;
        n0 = new NonTerminal("ModeDeclaration");
        n1 = new Terminal("Mode", lexer.getToken());
        n0.add(n1);
        expectMust(TokenType.NUMBER, null, alreadyRead);
        n2 = new Terminal("ModeValue", lexer.getToken());
        n0.add(n2);
        node.add(n0);
        return true;
    }

    public boolean parseEntry(Node node) throws LexerException, ScannerException {
        int result;
        ConcreteToken ctoken;
        Token token;
        Position position;
        String message;
        ConcreteTokenList alreadyRead = new ConcreteTokenList();
        Node n0, n1, n2;
        boolean ret;

        ignoreNewLine();
        ret = expectMay(TokenType.KEYWORD, "entry", alreadyRead);
        if (!ret)
            return false;
        n0 = new NonTerminal("EntryDeclaration");
        n1 = new Terminal("Entry", lexer.getToken());
        n0.add(n1);
        expectMust(TokenType.IDENTIFIER, null, alreadyRead);
        n2 = new Terminal("EntryValue", lexer.getToken());
        n0.add(n2);
        node.add(n0);
        return true;
    }

    public boolean parseFunctionDeclaration(Node node) throws LexerException, ScannerException {
        int result;
        ConcreteToken ctoken;
        Token token;
        Position position;
        String message;
        ConcreteTokenList alreadyRead = new ConcreteTokenList();
        Node n0, n1, n2;
        boolean ret;

        ignoreNewLine();
        ret = expectMay(TokenType.KEYWORD, "function", alreadyRead);
        if (!ret)
            return false;
        n0 = new NonTerminal("FunctionDeclaration");
        n1 = new Terminal("Function", lexer.getToken());
        n0.add(n1);
        expectMust(TokenType.IDENTIFIER, null, alreadyRead);
        n2 = new Terminal("FunctionValue", lexer.getToken());
        n0.add(n2);
        node.add(n0);
        return true;
    }

    public boolean parseSegment(Node node) throws LexerException, ScannerException {
        int result;
        ConcreteToken ctoken;
        Token token;
        Position position;
        String message;
        ConcreteTokenList alreadyRead = new ConcreteTokenList();
        Node n0, n1, n2;
        boolean ret;

        ignoreNewLine();
        ret = expectMay(TokenType.KEYWORD, "segment", alreadyRead);
        if (!ret)
            return false;
        n0 = new NonTerminal("SegmentDefinition");
        n1 = new Terminal("Segment", lexer.getToken());
        n0.add(n1);
        expectMust(TokenType.IDENTIFIER, null, alreadyRead);
        n2 = new Terminal("SegmentValue", lexer.getToken());
        n0.add(n2);
        ignoreNewLine();
        expectMust(TokenType.LEFT_CURLY_BRACE, null, alreadyRead);
        n2 = new Terminal("LEFT_CURLY_BRACE", lexer.getToken());
        n0.add(n2);
        ignoreNewLine();

        n2 = new NonTerminal("SegmentBody");
        n0.add(n2);
        while (true) {
            ret = parseInstruction(n2);
            if (!ret)
                break;
            lexer.getTokens().clear();
        }

        ignoreNewLine();
        expectMust(TokenType.RIGHT_CURLY_BRACE, null, alreadyRead);
        n2 = new Terminal("RIGHT_CURLY_BRACE", lexer.getToken());
        n0.add(n2);
        node.add(n0);
        return true;
    }

    public boolean parseInstruction(Node node) throws LexerException, ScannerException {
        boolean ret;
        ConcreteToken ctoken;
        Token token;
        String s1;
        ConcreteTokenList alreadRead;
        Node n0, n1, nodeLabel;
        ConcreteToken label;

        nodeLabel = null;
        ignoreNewLine();
        alreadRead = new ConcreteTokenList();
        ret = expectMay(TokenType.IDENTIFIER, null, alreadRead);
        if (ret) {
            label = lexer.getToken();
            token = label.getToken();
            s1 = token.getLexeme();
            ret = expectMust(TokenType.COLON, null, alreadRead);
            if (!ret)
                return false;
            if (ret) {
                nodeLabel = new NonTerminal("Label");
                n0 = new Terminal(token.getType().toString(), label);
                nodeLabel.add(n0);
                ctoken = lexer.getToken();
                n0 = new Terminal(TokenType.SEMICOLON.toString(), ctoken);
                nodeLabel.add(n0);
            }
        }
        ignoreNewLine();
        ret = expectMay(TokenType.OPCODE, null, alreadRead);
        if (!ret) {
            if (nodeLabel != null) {
                n0 = new NonTerminal("Instruction");
                n0.add(nodeLabel);
                node.add(n0);
                return true;
            }
            return false;
        }
        ctoken = lexer.getToken();
        token = ctoken.getToken();
        s1 = token.getLexeme();
        n0 = new NonTerminal("Instruction");
        if (nodeLabel != null)
            n0.add(nodeLabel);
        n1 = new Terminal(token.getType().toString(), ctoken);
        n0.add(n1);
        if (s1.equals("mov")) {
            ret = expectMay(TokenType.REGISTER, null, alreadRead);
            if (!ret) {
                ret = expectMay(TokenType.NUMBER, null, alreadRead);
                if (!ret) {
                    ret = parseMemory(n0);
                    if (!ret) {
                        if (nodeLabel != null) {
                            n0 = new NonTerminal("Instruction");
                            n0.add(nodeLabel);
                            node.add(n0);
                            return true;
                        }
                        return false;
                    }
                }
                ctoken = lexer.getToken();
                token = ctoken.getToken();
                n1 = new Terminal(token.getType().toString(), ctoken);
                n0.add(n1);
            }

            ctoken = lexer.getToken();
            token = ctoken.getToken();
            n1 = new Terminal(token.getType().toString(), ctoken);
            n0.add(n1);


            ret = expectMust(TokenType.COMMA, null, alreadRead);
            ctoken = lexer.getToken();
            token = ctoken.getToken();
            n1 = new Terminal(token.getType().toString(), ctoken);
            n0.add(n1);

            ret = expectMay(TokenType.REGISTER, null, alreadRead);
            if (!ret) {
                ret = expectMay(TokenType.NUMBER, null, alreadRead);
                if (!ret) {
                    if (nodeLabel != null) {
                        n0 = new NonTerminal("Instruction");
                        n0.add(nodeLabel);
                        node.add(n0);
                        return true;
                    }
                    return false;
                }
                ctoken = lexer.getToken();
                token = ctoken.getToken();
                n1 = new Terminal(token.getType().toString(), ctoken);
                n0.add(n1);
                node.add(n0);
                return true;
            }
        }
        return false;
    }

    public boolean parseIdentifier(ConcreteTokenList alreadyRead) throws LexerException, ScannerException {
        boolean ret;

        //ignoreNewLine();
        ret = expectMay(TokenType.IDENTIFIER, null, alreadyRead);
        if (!ret)
            return false;
        return true;
    }

    public boolean parseMemory(Node node) throws LexerException, ScannerException {
        boolean ret;
        Token token;
        ArrayList<Token> tokens;

        /*tokens = new ArrayList<>();
        token

                ret = expectMay(TokenType.KEYWORD, "byte", alreadyRead);

        ret = expectMay(TokenType.LEFT_SQUARE_BRACKET, null, alreadyRead);
        if (!ret)
            return false;*/
        return true;
    }

    public boolean parseMov() throws LexerException, ScannerException {
        ignoreNewLine();
        return true;
    }

    public boolean expectMust(TokenType tokenType, String lexeme, ConcreteTokenList alreadyRead) throws
            LexerException, ScannerException {
        int result;
        String message;
        ConcreteToken ctoken;
        Token token;
        String s;
        Position position;

        while (true) {
            result = lexer.nextToken();
            if (result == -1) {
                return false;
            }
            ctoken = lexer.getToken();
            alreadyRead.add(ctoken);
            token = ctoken.getToken();
            position = ctoken.getPosition();
            if (token.getType() == TokenType.SINGLE_LINE_COMMENT || token.getType() == TokenType.MULTI_LINE_COMMENT)
                continue;
            break;
        }

        if (token.getType() != tokenType) {
            message = String.format(
                    "行：%d 列：%d 错误：期待%s",
                    position.getLine() + 1,
                    position.getColumn(),
                    tokenType);
            throw new LexerException(message);
        }
        s = token.getLexeme();
        if (lexeme == null) {
            return true;
        } else {
            if (s == null) {
                message = String.format(
                        "行：%d 列：%d 错误：期待%s",
                        position.getLine() + 1,
                        position.getColumn(),
                        tokenType);
                throw new LexerException(message);
            }
            if (!s.equals(lexeme)) {
                message = String.format(
                        "行：%d 列：%d 错误：期待%s",
                        position.getLine() + 1,
                        position.getColumn(),
                        tokenType);
                throw new LexerException(message);
            }
        }
        return true;
    }

    public boolean expectMust(ArrayList<Token> tokens, ConcreteTokenList alreadyRead) throws
            LexerException, ScannerException {
        int result;
        String message;
        ConcreteToken ctoken;
        Token token;
        String s;
        Position position;
        Token current;
        String lexeme;

        if (tokens == null || tokens.size() == 0)
            return false;
        for (int i = 0; i < tokens.size(); i++) {
            current = tokens.get(i);
            lexeme = current.getLexeme();
            while (true) {
                result = lexer.nextToken();
                if (result == -1) {
                    return false;
                }
                ctoken = lexer.getToken();
                alreadyRead.add(ctoken);
                token = ctoken.getToken();
                position = ctoken.getPosition();
                if (token.getType() == TokenType.SINGLE_LINE_COMMENT || token.getType() == TokenType.MULTI_LINE_COMMENT)
                    continue;
                break;
            }

            if (token.getType() != current.getType()) {
                message = String.format(
                        "行：%d 列：%d 错误：期待%s",
                        position.getLine() + 1,
                        position.getColumn(),
                        current.getType());
                throw new LexerException(message);
            }
            s = token.getLexeme();
            if (lexeme == null) {
                continue;
            } else {
                if (s == null) {
                    message = String.format(
                            "行：%d 列：%d 错误：期待%s",
                            position.getLine() + 1,
                            position.getColumn(),
                            current.getType());
                    throw new LexerException(message);
                }
                if (!s.equals(lexeme)) {
                    message = String.format(
                            "行：%d 列：%d 错误：期待%s",
                            position.getLine() + 1,
                            position.getColumn(),
                            current.getType());
                    throw new LexerException(message);
                }
            }
        }
        return true;
    }

    public boolean expectMay(TokenType tokenType, String lexeme, ConcreteTokenList alreadyRead) throws
            LexerException, ScannerException {
        int result;
        String message;
        ConcreteToken ctoken;
        Token token;
        String s;
        Position position;

        while (true) {
            result = lexer.nextToken();
            if (result == -1) {
                return false;
            }
            ctoken = lexer.getToken();
            alreadyRead.add(ctoken);
            token = ctoken.getToken();
            position = ctoken.getPosition();
            if (token.getType() == TokenType.SINGLE_LINE_COMMENT || token.getType() == TokenType.MULTI_LINE_COMMENT)
                continue;
            break;
        }

        if (token.getType() != tokenType) {
            lexer.pushBack(alreadyRead);
            alreadyRead.clear();
            return false;
        }
        s = token.getLexeme();
        if (lexeme == null) {
            return true;
        } else {
            if (s == null) {
                lexer.pushBack(alreadyRead);
                alreadyRead.clear();
                return false;
            }
            if (!s.equals(lexeme)) {
                lexer.pushBack(alreadyRead);
                alreadyRead.clear();
                return false;
            }
        }
        return true;
    }

    public boolean expectMay(ArrayList<Token> tokens, ConcreteTokenList alreadyRead) throws
            LexerException, ScannerException {
        int result;
        String message;
        ConcreteToken ctoken;
        Token token;
        String s;
        Position position;
        Token current;
        String lexeme;
        ConcreteTokenList list = new ConcreteTokenList();

        if (tokens == null || tokens.size() == 0)
            return false;
        for (int i = 0; i < tokens.size(); i++) {
            current = tokens.get(i);
            lexeme = current.getLexeme();
            while (true) {
                result = lexer.nextToken();
                if (result == -1) {
                    return false;
                }
                ctoken = lexer.getToken();
                list.add(ctoken);
                token = ctoken.getToken();
                position = ctoken.getPosition();
                if (token.getType() == TokenType.SINGLE_LINE_COMMENT || token.getType() == TokenType.MULTI_LINE_COMMENT)
                    continue;
                break;
            }

            if (token.getType() != current.getType()) {
                lexer.pushBack(list);
                list.clear();
                continue;
            }
            s = token.getLexeme();
            if (lexeme == null) {
                return true;
            } else {
                if (s == null) {
                    lexer.pushBack(list);
                    list.clear();
                    continue;
                }
                if (!s.equals(lexeme)) {
                    lexer.pushBack(list);
                    list.clear();
                    continue;
                }
            }
        }
        return false;
    }

    public void ignoreNewLine() throws LexerException, ScannerException {
        int result;
        String message;
        ConcreteToken ctoken;
        Token token;
        String s;
        Position position;
        ConcreteTokenList alreadyRead;

        alreadyRead = new ConcreteTokenList();

        while (true) {
            while (true) {
                result = lexer.nextToken();
                if (result == -1) {
                    return;
                }
                ctoken = lexer.getToken();
                alreadyRead.add(ctoken);
                token = ctoken.getToken();
                position = ctoken.getPosition();
                if (token.getType() == TokenType.SINGLE_LINE_COMMENT || token.getType() == TokenType.MULTI_LINE_COMMENT)
                    continue;
                break;
            }

            if (token.getType() != TokenType.NEW_LINE) {
                lexer.pushBack(ctoken);
                return;
            }
        }
    }
}
