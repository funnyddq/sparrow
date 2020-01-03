package com.hexminds.sparrow.lexer;

public enum TokenType {
    UNKNOWN,
    EOF,
    KEYWORD,
    OPERAND,
    REGISTER,
    NUMBER,
    PSEUDO_INSTRUCTION,
    COMMENT,
    LEFT_PARENTHESIS,
    RIGHT_PARENTHESIS,
    LEFT_SQUARE_BRACKET,
    RIGHT_SQUARE_BRACKET,
    LEFT_CURLY_BRACE,
    RIGHT_CURLY_BRACE,
    LITERAL_STRING,
    LITERAL_CHARACTER,
    IDENTIFIER,
    DATA_TYPE,
    OPERATOR,
    SINGLE_LINE_COMMENT,
    MULTI_LINE_COMMENT,
    DOUBLE_QUOTED_STRING,
    COMMA,
    SEMICOLON,
    COLON,
    DOT_MARK,
    AT_SIGN,
    ADD,
    SUB,
    OPCODE,
    NEW_LINE,
    MUL,
    DIV
}
