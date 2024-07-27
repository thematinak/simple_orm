package org.example.orm.manipulator;

import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Table;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;

import static org.example.orm.manipulator.SqlAnnotationGetter.getDataFromField;

public class SqlDelete {

    public static void delete(JdbcTemplate jdbcTemplate, Object entity) {
        Table table = SqlAnnotationGetter.getTableAnnot(entity);

        String idColumnName = null;
        Object idVal = null;
        for (Field field : entity.getClass().getFields()) {
            IdColumn idColumn = field.getAnnotation(IdColumn.class);
            if (idColumn != null) {
                idColumnName = idColumn.value();
                idVal = getDataFromField(field, entity);
                break;
            }
        }

        if (idColumnName == null) {
            throw new RuntimeException("@IdColumn annotation was not found on object: " + entity.getClass());
        }

        String sql = "Delete from " + table.value() + " where " + idColumnName + " = ?";
        jdbcTemplate.update(sql, idVal);
    }
}
