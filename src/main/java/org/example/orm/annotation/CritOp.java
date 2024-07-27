package org.example.orm.annotation;

public enum CritOp {
    EQ("="),LESS("<"), LQ("<="), MORE(">"), MQ(">=");

    public final String opString;
    CritOp(String op) {
        this.opString = op;
    }
}
