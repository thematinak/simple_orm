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
    public int vek;

    @Column("col2")
    public String meno;

    @Column("col3")
    public Timestamp cas;


    @Override
    public String toString() {
        return "AbcExample{" +
                "id=" + id +
                ", vek=" + vek +
                ", meno='" + meno + '\'' +
                ", cas=" + cas +
                '}';
    }
}
