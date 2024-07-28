package org.example.orm.exemple;

import org.example.orm.annotation.Column;
import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Table;

import java.sql.Timestamp;

@Table("abc")
public class AbcExampleEntity {

    @IdColumn
    public int id;

    @Column("col1")
    public int age;

    @Column("col2")
    public String name;

    @Column("col3")
    public Timestamp time;


    @Override
    public String toString() {
        return "AbcExample{" +
                "id=" + id +
                ", vek=" + age +
                ", meno='" + name + '\'' +
                ", cas=" + time +
                '}';
    }
}
