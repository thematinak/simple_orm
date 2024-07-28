package org.example.orm.manipulator;

import org.example.orm.annotation.Column;
import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Table;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SqlUpdate {

    public static void update(JdbcTemplate jdbcTemplate, Object entity) {
        Table table = SqlAnnotationGetter.getTableAnnot(entity);

        List<String> fieldsNames = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        Object id = null;
        String idName = null;
        for(Field field : entity.getClass().getFields()) {
            Column column = field.getAnnotation(Column.class);
            Object val = null;
            String columnName = null;
            if (column != null) {
                val = SqlAnnotationGetter.getDataFromField(field, entity);
                columnName = SqlAnnotationGetter.getColumnName(field, column);
            }
            IdColumn idColumn = field.getAnnotation(IdColumn.class);
            if (idColumn != null) {
                id = SqlAnnotationGetter.getDataFromField(field, entity);
                idName = SqlAnnotationGetter.getColumnName(field, idColumn);

            }
            if (val != null) {
                values.add(val);
                fieldsNames.add(columnName);
            }
        }

        String setValues = String.join(",", fieldsNames.stream().map(x -> x + " = ?").toList());
        String sql = "Update " + table.value() + " Set " + setValues + " Where " + idName + " = ?";
        values.add(id);
        jdbcTemplate.update(sql, values.toArray());
    }
}
