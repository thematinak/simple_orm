package org.example.orm.manipulator;

import org.example.orm.annotation.Column;
import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Join;
import org.example.orm.annotation.Table;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SqlInsert {

    public static void save(JdbcTemplate jdbcTemplate, Object entity) {
        Table table = SqlAnnotationGetter.getTableAnnot(entity);

        List<String> fieldsNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for(Field field : entity.getClass().getFields()) {
            Column column = field.getAnnotation(Column.class);
            Object val = null;
            String columnName = null;
            if (column != null) {
                val = SqlAnnotationGetter.getDataFromField(field, entity);
                columnName = SqlAnnotationGetter.getColumnName(field, column);
            }
            IdColumn idColumn = field.getAnnotation(IdColumn.class);
            if (idColumn != null && !idColumn.autoIncrement()) {
                val = SqlAnnotationGetter.getDataFromField(field, entity);
                columnName = SqlAnnotationGetter.getColumnName(field, idColumn);
            }
            if (val != null) {
                values.add(val);
                fieldsNames.add(columnName);
            }
        }

        String questions = String.join(",", values.stream().map(x-> "?").toList());
        String sql = "Insert into "  + table.value() + "(" + String.join(", ", fieldsNames)+") values (" + questions + ")";
        jdbcTemplate.update(sql, values.toArray());
    }
}
