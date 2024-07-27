package org.example.orm.manipulator;

import org.example.orm.annotation.Column;
import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Table;

import java.lang.reflect.Field;

public class SqlAnnotationGetter {

    protected static <T> Table getTableAnnot(Class<T> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("Class " + clazz + "does not have a Table annotation");
        }
        return table;
    }

    protected static Table getTableAnnot(Object entity) {
        Table table = entity.getClass().getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("Class " + entity.getClass() + "does not have a Table annotation");
        }
        return table;
    }

    protected static String getColumnName(Field field, IdColumn idColumn) {
        return idColumn.value();
    }

    protected static String getColumnName(Field field, Column column) {
        if (column.value().isEmpty()) {
            return field.getName();
        } else {
            return column.value();
        }
    }

    protected static Object getDataFromField(Field field, Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
