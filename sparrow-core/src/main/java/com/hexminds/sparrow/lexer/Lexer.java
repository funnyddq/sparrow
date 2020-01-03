package com.hexminds.sparrow.lexer;

import com.hexminds.sparrow.exception.LexerException;
import com.hexminds.sparrow.exception.ScannerException;
import com.hexminds.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Lexer {
    public static final int MAX_SINGLE_LINE_COMMENT_LENGTH = 1024 * 1024;
    public static final int MAX_MULTI_LINE_COMMENT_LENGTH = 1024 * 1024;
    public static final int MAX_SINGLE_QUOTED_STRING_LENGTH = 1024 * 1024;
    public static final int MAX_DOUBLE_QUOTED_STRING_LENGTH = 1024 * 1024;
    public static final int MAX_NUMBER_LENGTH = 32;
    public static final int MAX_IDENTIFIER_LENGTH = 256;
    public static final int MAX_SEPATATOR_LENGTH = 8;
    public static final int MAX_OPERATOR_LENGTH = 8;
    public static final int DEFAULT_TAB_WIDTH = 4;

    protected CharStream stream;
    protected ConcreteTokenList tokens;
    protected ConcreteToken returnedToken;
    Position basePosition;
    Position currentPosition;
    protected int tabWidth;

    public Lexer(CharStream stream) {
        this.stream = stream;
        basePosition = new Position();
        currentPosition = new Position();
        tokens = new ConcreteTokenList();
        tabWidth = DEFAULT_TAB_WIDTH;
    }

    public ConcreteToken getToken() {
        return returnedToken;
    }

    public ConcreteTokenList getTokens() {
        return tokens;
    }

    public int getTabWidth() {
        return tabWidth;
    }

    public void setTabWidth() {
        this.tabWidth = tabWidth;
    }

    public void pushBack(ConcreteToken ctoken) {
        if (ctoken == null)
            return;
        tokens.add(ctoken);
        return;
    }

    public void pushBack(ConcreteToken[] ctokens) {
        if (ctokens == null || ctokens.length == 0)
            return;

        for (int i = ctokens.length - 1; i >= 0; i--) {
            tokens.add(ctokens[i]);
        }
        return;
    }

    public void pushBack(ConcreteTokenList ctokens) {
        if (ctokens == null || ctokens.size() == 0)
            return;

        for (int i = ctokens.size() - 1; i >= 0; i--) {
            tokens.add(ctokens.get(i));
        }
        return;
    }

    public int nextToken() throws ScannerException, LexerException {
        int read;
        int ret;
        Token token;
        TokenType type;
        ArrayList<Integer> lexeme;
        ConcreteToken concreteToken;
        String message;

//        if (tokens == null || stream == null)
//            return -1;

        if (stream == null)
            throw new NullPointerException();

        if (tokens.size() != 0) {
            tokens.remove(tokens.size() - 1);
            return 0;
        }

        // 清空函数返回的token列表
        //tokens.clear();

        // 初始化

        lexeme = new ArrayList<>();

        try {
            // 词法分析第一循环
            while (true) {
                lexeme.clear();
                basePosition.set(currentPosition);
                read = stream.read();
                currentPosition.incColumn();
                if (read == -1) {
                    // 结束
                    return -1;
                } else if (Char.isBlank(read)) {
                    // 去除空格和制表符
                    if (Char.isTab(read))
                        currentPosition.incColumn(tabWidth - 1);
                    continue;
                }
                lexeme.add(read);
                if (Char.isCR(read)) {
                    parseCR(basePosition, currentPosition, lexeme);
                    return 0;

                } else if (Char.isLineFeed(read)) {
                    parseLineFeed(basePosition, currentPosition, lexeme);
                    return 0;
                } else if (Char.isSlash(read)) {
                    // 注释判断
                    ret = parseComment(basePosition, currentPosition, lexeme);
                    // 只会返回0或者1
                    if (ret == 0)
                        return 0;
                }

                // ret == 1
                currentPosition.set(basePosition);
                currentPosition.incColumn();
                if (Char.isDigit(read)) {
                    // 数字判断
                    ret = parseNumber(basePosition, currentPosition, lexeme);
                    // 只会返回0或者1
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else if (Char.isIdStartChar(read)) {
                    // 标识符、关键字、寄存器判断、操作码
                    ret = parseIdentifier(basePosition, currentPosition, lexeme);
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else if (Operator.getFirst(StringUtils.codePointsToString(read)).size() != 0) {
                    // 操作符判断
                    ret = parseOperator(basePosition, currentPosition, lexeme);
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else if (Separator.getFirst(StringUtils.codePointsToString(read)).size() != 0) {
                    // 分隔符号判断
                    ret = parseSeparator(basePosition, currentPosition, lexeme);
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else if (Char.isDoubleQuotation(read)) {
                    // 字符串
                    ret = parseDoubleQuotedString(basePosition, currentPosition, lexeme);
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else if (Char.isSingleQuotation(read)) {
                    // 字符
                    ret = parseLiteralCharacter(basePosition, currentPosition, lexeme);
                    if (ret == -1)
                        return -1;
                    else if (ret == 0)
                        return 0;
                } else {
                    message = String.format(
                            "行：%d 列：%d 错误：无效的字符",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int nextValidToken() throws ScannerException, LexerException {
        int ret;
        Token token;

        while (true) {
            ret = nextToken();
            if (ret != 0)
                return ret;
            token = returnedToken.getToken();
            if (token.getType() == TokenType.SINGLE_LINE_COMMENT ||
                    token.getType() == TokenType.MULTI_LINE_COMMENT)
                continue;
            return ret;
        }
    }

    // 功能：
    //     分析回车符
    private void parseCR(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int next;

        type = TokenType.NEW_LINE;

        next = stream.read();
        currentPosition.incColumn();
        if (next == -1) {
            currentPosition.setColumn(0);
            currentPosition.incLine();
            addToken(type, lexeme);
            stream.pushBack(next);
            return;
        } else if (Char.isLineFeed(next)){
            lexeme.add(next);
            addToken(type, lexeme);
            currentPosition.setColumn(0);
            currentPosition.incLine();
            return;
        }
        currentPosition.setColumn(0);
        currentPosition.incLine();
        addToken(type, lexeme);
        stream.pushBack(next);
        return;
    }

    // 功能：
    //     分析换行符
    private void parseLineFeed(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;

        type = TokenType.NEW_LINE;
        addToken(type, lexeme);
        currentPosition.setColumn(0);
        currentPosition.incLine();
        return;
    }

    // 功能：
    //     分析数字
    // 返回值：
    //     0: 分析成功
    //     -1: 失败
    //     1: 继续分析
    private int parseNumber(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        int read, next, third;
        TokenType type;
        int status;
        String message;

        read = lexeme.get(lexeme.size() - 1);
        status = 0;
        type = TokenType.NUMBER;

        if (read >= '1' && read <= '9') {
            status = 1;
            // [1-9]
            while (true) {
                next = stream.read();
                currentPosition.incColumn();
                if (next == -1) {
                    addToken(type, lexeme);
                    stream.pushBack(next);
                    currentPosition.decColumn();
                    return 0;
                } else if (Char.isLineFeed(next) || Char.isCR(next)) {
                    currentPosition.decColumn();
                    stream.pushBack(next);
                    addToken(type, lexeme);
                    return 0;
                }
                if (Char.isDigit(next)) {
                    if (lexeme.size() >= MAX_NUMBER_LENGTH) {
                        message = String.format(
                                "行：%d 列：%d 错误：数字超长",
                                basePosition.getLine() + 1,
                                basePosition.getColumn());
                        throw new LexerException(message);
                    }
                    lexeme.add(next);
                    continue;
                } else {
                    if (!isNumberEnd(next)) {
                        message = String.format(
                                "行：%d 列：%d 错误：无效的数字",
                                currentPosition.getLine() + 1,
                                currentPosition.getColumn() + 1);
                        throw new LexerException(message);
                    }
//                    status = 2;
//                    break;
                    addToken(type, lexeme);
                    stream.pushBack(next);
                    return 0;
                }
            }

//            if (status == 2) {
//                stream.pushBack(next);
//                currentPosition.subColumn();
//                addToken(type, lexeme);
//                return 1;
//            } else {
//                return -1;
//            }

        } else {
            // 0
            status = 3;
            next = stream.read();
            currentPosition.incColumn();
            if (next == 'x' || next == 'X') {
                // 0x
                status = 4;
                lexeme.add(next);
                while (true) {
                    third = stream.read();
                    currentPosition.incColumn();
                    if (Char.isHex(third)) {
                        if (lexeme.size() >= MAX_NUMBER_LENGTH) {
                            message = String.format(
                                    "行：%d 列：%d 错误：数字超长",
                                    basePosition.getLine() + 1,
                                    basePosition.getColumn() + 1);
                            throw new LexerException(message);
                        }
                        lexeme.add(third);
                        continue;
                    } else {
                        if (!isNumberEnd(third)) {
                            message = String.format(
                                    "行：%d 列：%d 错误：无效的十六进制字符",
                                    currentPosition.getLine() + 1,
                                    currentPosition.getColumn());
                            throw new LexerException(message);
                        }
                        if (lexeme.size() == 2) {
                            message = String.format(
                                    "行：%d 列：%d 错误：期待十六进制字符",
                                    currentPosition.getLine() + 1,
                                    currentPosition.getColumn());
                            throw new LexerException(message);
                        }
                        addToken(type, lexeme);
                        stream.pushBack(third);
                        currentPosition.decColumn();
                        return 0;
                    }
                }
            } else if (next == 'b' || next == 'B') {
                // 0b
                status = 5;
                lexeme.add(next);
                while (true) {
                    third = stream.read();
                    currentPosition.incColumn();
                    if (Char.isBin(third)) {
                        if (lexeme.size() >= MAX_NUMBER_LENGTH) {
                            message = String.format(
                                    "行：%d 列：%d 错误：数字超长",
                                    basePosition.getLine() + 1,
                                    basePosition.getColumn());
                            throw new LexerException(message);
                        }
                        lexeme.add(third);
                        continue;
                    } else {
                        if (!isNumberEnd(third)) {
                            message = String.format(
                                    "行：%d 列：%d 错误：无效的二进制字符",
                                    currentPosition.getLine() + 1,
                                    currentPosition.getColumn());
                            throw new LexerException(message);
                        }
                        if (lexeme.size() == 2) {
                            message = String.format(
                                    "行：%d 列：%d 错误：期待二进制字符",
                                    currentPosition.getLine() + 1,
                                    currentPosition.getColumn());
                            throw new LexerException(message);
                        }
                        addToken(type, lexeme);
                        stream.pushBack(third);
                        currentPosition.decColumn();
                        return 0;
//                        status = 5;
//                        break;
                    }
                }

//                if (status == 5) {
//                    stream.pushBack(third);
//                    currentPosition.subColumn();
//                    if (lexeme.size() == 2) {
//                        message = String.format(
//                                "行：%d 列：%d 错误：无效的二进制字符",
//                                basePosition.getLine() + 1,
//                                basePosition.getColumn() + 1);
//                        throw new LexerException(message);
//                    }
//                    addToken(type, lexeme);
//                    return 1;
//                } else {
//                    return -1;
//                }
            } else if (!isNumberEnd(next)) {
                message = String.format(
                        "行：%d 列：%d 错误：无效的数字",
                        basePosition.getLine() + 1,
                        basePosition.getColumn() + 1);
                throw new LexerException(message);
            }
            addToken(type, lexeme);
            stream.pushBack(next);
            currentPosition.decColumn();
            return 0;
        }
    }

    // 功能：
    //     分析标识符
    // 返回值：
    //     0: 分析成功
    //     -1: 失败
    //     1: 继续分析
    private int parseIdentifier(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        int next;
        TokenType type;
        String message;

        type = TokenType.IDENTIFIER;
        while (true) {
            next = stream.read();
            currentPosition.incColumn();
            if (Char.isIdChar(next)) {
                if (lexeme.size() >= MAX_IDENTIFIER_LENGTH) {
                    message = String.format(
                            "行：%d 列：%d 错误：标识符超长",
                            basePosition.getLine() + 1,
                            basePosition.getColumn());
                    throw new LexerException(message);
                }
                lexeme.add(next);
                continue;
            } else {
                if (!isNumberEnd(next)) {
                    message = String.format(
                            "行：%d 列：%d 错误：无效的标识符字符",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                }
                String s = StringUtils.codePointsToString(lexeme);
                if (Keyword.isKeyword(s))
                    type = TokenType.KEYWORD;
                else if (Register.isRegister(s))
                    type = TokenType.REGISTER;
                else if (Opcode.isOpcode(s))
                    type = TokenType.OPCODE;
                else if (DataType.isDataType(s))
                    type = TokenType.DATA_TYPE;
                addToken(type, lexeme);
                stream.pushBack(next);
                currentPosition.decColumn();
                return 0;
            }
            /*if (next == -1) {
                String s = StringUtil.codePointsToString(lexeme);
                if (Keyword.isKeyword(s))
                    type = TokenType.KEYWORD;
                else if (Register.isRegister(s))
                    type = TokenType.REGISTER;
                else if (Opcode.isOpcode(s))
                    type = TokenType.OPCODE;
                addToken(type, lexeme);
                return 0;
            }
            if (Char.isIdChar(next)) {
                lexeme.add(next);
                continue;
            } else {
                if (!isNumberEnd(next)) {
                    message = String.format(
                            "行：%d 列：%d 错误：无效的标识符字符",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                }
                String s = StringUtil.codePointsToString(lexeme);
                if (Keyword.isKeyword(s))
                    type = TokenType.KEYWORD;
                else if (Register.isRegister(s))
                    type = TokenType.REGISTER;
                else if (Opcode.isOpcode(s))
                    type = TokenType.OPCODE;
                addToken(type, lexeme);
                stream.pushBack(next);
                currentPosition.subColumn();
                return 0;
            }*/
        }
    }

    // 功能：
    //     分析操作符
    // 返回值：
    //     0: 分析成功
    //     -1: 失败
    //     1: 继续分析
    private int parseOperator(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int next;
        String message;

        type = TokenType.OPERATOR;
        while (true) {
            String s;
            s = StringUtils.codePointsToString(lexeme);
            next = stream.read();
            currentPosition.incColumn();
            if (next == -1) {
                addToken(type, lexeme);
                return 0;
            }
            if (lexeme.size() >= MAX_OPERATOR_LENGTH) {
                message = String.format(
                        "行：%d 列：%d 错误：操作符超长",
                        basePosition.getLine() + 1,
                        basePosition.getColumn());
                throw new LexerException(message);
            }
            lexeme.add(next);
            s = StringUtils.codePointsToString(lexeme);
            if (Operator.getFirst(s).size() != 0) {
                continue;
            } else {
                while (lexeme.size() > 0) {
                    lexeme.remove(lexeme.size() - 1);
                    stream.pushBack(next);
                    currentPosition.decColumn();
                    if (Operator.isOperator(StringUtils.codePointsToString(lexeme))) {
                        addToken(type, lexeme);
                        return 0;
                    }
                }
                return 1;
            }
        }
    }

    // 功能：
    //     分析操作符
    // 返回值：
    //     0: 分析成功
    //     -1: 失败
    //     1: 继续分析
    private int parseSeparator(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int next;
        ArrayList<Token> types;
        int read;
        String message;

        read = lexeme.get(lexeme.size() - 1);
        types = Separator.getFirst(StringUtils.codePointsToString(read));
        if (types == null || types.size() == 0)
            return 1;
        type = types.get(0).getType();
        while (true) {
            String s;
            s = StringUtils.codePointsToString(read);
            next = stream.read();
            currentPosition.incColumn();
            if (next == -1) {
                addToken(type, lexeme);
                return 0;
            }
            if (lexeme.size() >= MAX_SEPATATOR_LENGTH) {
                message = String.format(
                        "行：%d 列：%d 错误：分割符超长",
                        basePosition.getLine() + 1,
                        basePosition.getColumn());
                throw new LexerException(message);
            }
            lexeme.add(next);
            s = StringUtils.codePointsToString(lexeme);
            types = Separator.getFirst(s);
            if (types.size() != 0) {
                type = types.get(0).getType();
                continue;
            } else {
                while (lexeme.size() > 0) {
                    lexeme.remove(lexeme.size() - 1);
                    stream.pushBack(next);
                    currentPosition.decColumn();
                    if (Separator.isSeparator(StringUtils.codePointsToString(lexeme))) {
                        addToken(type, lexeme);
                        return 0;
                    }
                }
                return 1;
            }
        }
    }

    private int parseDoubleQuotedString(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int next;
        String message;
        int third;

        type = TokenType.DOUBLE_QUOTED_STRING;
        //lexeme.add(lexeme.get(lexeme.size() - 1));
        while (true) {
            next = stream.read();
            currentPosition.incColumn();
            if (next == -1) {
                message = String.format(
                        "行：%d 列：%d 错误：期待字符串结束标记，遇到EOF标记",
                        currentPosition.getLine() + 1,
                        currentPosition.getColumn());
                throw new LexerException(message);
            } else if (lexeme.size() >= MAX_DOUBLE_QUOTED_STRING_LENGTH) {
                message = String.format(
                        "行：%d 列：%d 错误：字符串超长",
                        basePosition.getLine() + 1,
                        basePosition.getColumn() + 1);
                throw new LexerException(message);
            } else if (Char.isDoubleQuotation(next)) {
                lexeme.add(next);
                addToken(type, lexeme);
                return 0;
            } else if (Char.isCR(next)) {
                third = stream.read();
                currentPosition.incColumn();
                if (third == -1) {
                    message = String.format(
                            "行：%d 列：%d 错误：期待字符串结束标记，遇到EOF标记",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                } else if (Char.isLineFeed(third)) {
                    lexeme.add(third);
                    currentPosition.setColumn(0);
                    currentPosition.incLine();
                    continue;
                } else {
                    stream.pushBack(third);
                    currentPosition.setColumn(0);
                    currentPosition.incLine();
                    continue;
                }
            } else if (Char.isLineFeed(next)) {
                lexeme.add(next);
                currentPosition.setColumn(0);
                currentPosition.incLine();
                continue;
            } else if (Char.isTab(next)) {
                lexeme.add(next);
                currentPosition.incColumn(tabWidth - 1);
                continue;
            } else if (Char.isBackslash(next)) {
                third = stream.read();
                currentPosition.incColumn();
                if (third == -1) {
                    message = String.format(
                            "行：%d 列：%d 错误：期待转义字符，遇到EOF标记",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                } else if (Char.isBackslash(third)) {
                    lexeme.add(next);
                    continue;
                } else if (third == '"') {
                    lexeme.add(third);
                    continue;
                } else if (third == 'r') {
                    lexeme.add((int) '\r');
                    continue;
                } else if (third == 'n') {
                    lexeme.add((int) '\n');
                    continue;
                } else if (third == 'f') {
                    lexeme.add((int) '\f');
                    continue;
                } else if (third == 't') {
                    lexeme.add((int) '\t');
                    continue;
                } else if (third == '0') {
                    lexeme.add(0);
                    continue;
                } else {
                    message = String.format(
                            "行：%d 列：%d 错误：期待转义字符",
                            currentPosition.getLine() + 1,
                            currentPosition.getColumn());
                    throw new LexerException(message);
                }
            } else {
                lexeme.add(next);
                continue;
            }
        }
    }

    private int parseLiteralCharacter(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int next;
        String message;

        type = TokenType.LITERAL_CHARACTER;
        while (true) {
            next = stream.read();
            currentPosition.incColumn();
            if (next == -1) {
                message = String.format(
                        "行：%d 列：%d 错误：期待字符，遇到EOF标记",
                        currentPosition.getLine() + 1,
                        currentPosition.getColumn());
                throw new LexerException(message);
            } else if (lexeme.size() > 3) {
                message = String.format(
                        "行：%d 列：%d 错误：字符常量超长",
                        basePosition.getLine() + 1,
                        basePosition.getColumn() + 1);
                throw new LexerException(message);
            } else if (Char.isSingleQuotation(next)) {
                if (lexeme.size() != 2) {
                    message = String.format(
                            "行：%d 列：%d 错误：无效的字符常量",
                            basePosition.getLine() + 1,
                            basePosition.getColumn() + 1);
                    throw new LexerException(message);
                }
                lexeme.add(next);
                addToken(type, lexeme);
                return 0;
            } else {
                lexeme.add(next);
                continue;
            }
        }
    }

    private int parseComment(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        int next;

        next = stream.read();
        lexeme.add(next);
        currentPosition.incColumn();
        if (Char.isSlash(next)) {
            // //
            return parseSingleLineComment(basePosition, currentPosition, lexeme);
        } else if (Char.isAsterisk(next)) {
            // /*
            return parseMultipleLineComment(basePosition, currentPosition, lexeme);
        }
        // /others
        return 1;
    }

    private int parseSingleLineComment(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme
    ) throws IOException, ScannerException, LexerException {
        TokenType type;
        int read;

        type = TokenType.SINGLE_LINE_COMMENT;
        while (true) {
            read = stream.read();
            currentPosition.incColumn();
            if (read == -1) {
                // 单行注释结束
                addToken(type, lexeme);
                return 0;
            } else if (Char.isLineFeed(read) || Char.isCR(read)) {
                // 单行注释结束
                stream.pushBack(read);
                currentPosition.decColumn();
                addToken(type, lexeme);
                return 0;
            } else {
                lexeme.add(read);
                if (lexeme.size() > MAX_SINGLE_LINE_COMMENT_LENGTH) {
                    String message;
                    message = String.format(
                            "行：%d 列：%d 错误：单行注释超长",
                            basePosition.getLine() + 1,
                            basePosition.getColumn() + 1);
                    throw new LexerException(message);
                }
            }
        }
    }

    private int parseMultipleLineComment(
            Position basePosition,
            Position currentPosition,
            ArrayList<Integer> lexeme) throws IOException, ScannerException, LexerException {
        TokenType type;
        int read;
        int next;
        String message;

        type = TokenType.MULTI_LINE_COMMENT;
        while (true) {
            read = stream.read();
            currentPosition.incColumn();
            if (read == -1) {
                return -1;
            } else if (Char.isAsterisk(read)) {
                // /* *
                next = stream.read();
                currentPosition.incColumn();
                if (Char.isAsterisk(next)) {
                    lexeme.add(read);
                    if (lexeme.size() >= MAX_MULTI_LINE_COMMENT_LENGTH) {
                        message = String.format(
                                "行：%d 列：%d 错误：多行注释超长",
                                basePosition.getLine() + 1,
                                basePosition.getColumn() + 1);
                        throw new LexerException(message);
                    }
                    stream.pushBack(next);
                    continue;
                } else if (Char.isSlash(next)) {
                    //
                    lexeme.add(read);
                    lexeme.add(next);
                    if (lexeme.size() > MAX_MULTI_LINE_COMMENT_LENGTH) {
                        message = String.format(
                                "行：%d 列：%d 错误：多行注释超长",
                                basePosition.getLine() + 1,
                                basePosition.getColumn() + 1);
                        throw new LexerException(message);
                    }
                    addToken(type, lexeme);
                    return 0;
                } else if (Char.isLineFeed(next)) {
                    // /* * lf
                    currentPosition.setColumn(0);
                    currentPosition.incLine();
                    lexeme.add(read);
                    lexeme.add(next);
                    if (lexeme.size() >= MAX_MULTI_LINE_COMMENT_LENGTH) {
                        message = String.format(
                                "行：%d 列：%d 错误：多行注释超长",
                                basePosition.getLine() + 1,
                                basePosition.getColumn() + 1);
                        throw new LexerException(message);
                    }
                    continue;
                } else {
                    lexeme.add(read);
                    lexeme.add(next);
                    if (lexeme.size() >= MAX_MULTI_LINE_COMMENT_LENGTH) {
                        message = String.format(
                                "行：%d 列：%d 错误：多行注释超长",
                                basePosition.getLine() + 1,
                                basePosition.getColumn() + 1);
                        throw new LexerException(message);
                    }
                    continue;
                }
            } else if (Char.isLineFeed(read)) {
                // /* lf
                lexeme.add(read);
                currentPosition.setColumn(0);
                currentPosition.incLine();
                if (lexeme.size() >= MAX_MULTI_LINE_COMMENT_LENGTH) {
                    message = String.format(
                            "行：%d 列：%d 错误：多行注释超长",
                            basePosition.getLine() + 1,
                            basePosition.getColumn() + 1);
                    throw new LexerException(message);
                }
                continue;
            } else {
                lexeme.add(read);
                if (lexeme.size() >= MAX_MULTI_LINE_COMMENT_LENGTH) {
                    message = String.format(
                            "行：%d 列：%d 错误：多行注释超长",
                            basePosition.getLine() + 1,
                            basePosition.getColumn() + 1);
                    throw new LexerException(message);
                }
            }
        }
    }

//    public static void addColumn(Position... positions) {
//        for (Position position : positions) {
//            position.addColumn();
//        }
//    }
//
//    public static void subColumn(Position... positions) {
//        for (Position position : positions) {
//            position.subColumn();
//        }
//    }

    public static boolean parseFile(String fileName, ConcreteTokenList tokens) {
        File file;

        if (fileName == null || tokens == null)
            return false;

        file = new File(fileName);
        //return parseFile(file, tokens);
        return true;
    }

    protected ConcreteToken addToken(
            TokenType type,
            ArrayList<Integer> lexeme) {
        Token token;
        String str;
        Position p;
        ConcreteToken t;

        if (type == null || lexeme == null)
            throw new NullPointerException();

        str = StringUtils.codePointsToString(lexeme);
        token = new Token(type, str);
        p = new Position(basePosition);
        t = new ConcreteToken(token, p);
        returnedToken = t;
        return t;
    }

    public static void printTokens(ArrayList<String> tokens) {
        String str;

        if (tokens == null)
            throw new NullPointerException();

        for (String token : tokens) {
            System.out.println(token);
        }
        return;
    }

    private static boolean isNumberEnd(int codePoint) {
        if (!Char.isDigit(codePoint) && !Char.isAlpahbeta(codePoint) && !Char.isUnderScore(codePoint))
            return true;
        else
            return false;
    }
}
