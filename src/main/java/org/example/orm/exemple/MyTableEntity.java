package org.example.orm.exemple;

import org.example.orm.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;

@Table("my_table")
public class MyTableEntity {

    @IdColumn
    public int id;

    @Column("int_col")
    public int anInt;

    @Column("str_col")
    public String astring;

    @Column("boolean_col")
    public boolean aBoolean;

    @Column("abc_id")
    public int abc1;

    @Column("abc_id2")
    public int abc2;

    @Join(op = {CritOp.EQ}, left = {"abc_id"}, right = {"id"})
    public AbcExampleEntity abcExampleEntity;

    @Column("date_col")
    public Date date;

    @Join(op = {CritOp.EQ}, left = {"abc_id2"}, right = {"id"})
    public AbcExampleEntity abcExampleEntity2;

    @Column("timestamp_col")
    public Timestamp timestamp;

    @Override
    public String toString() {
        return "MyTableEntity{" +
                "id=" + id +
                ", anInt=" + anInt +
                ", astring='" + astring + '\'' +
                ", aBoolean=" + aBoolean +
                ", abcExampleEntity=" + abcExampleEntity +
                ", date=" + date +
                ", abcExampleEntity2=" + abcExampleEntity2 +
                ", timestamp=" + timestamp +
                '}';
    }
}
