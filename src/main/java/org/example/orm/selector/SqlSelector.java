package org.example.orm.selector;

import org.example.orm.annotation.Column;
import org.example.orm.annotation.IdColumn;
import org.example.orm.annotation.Join;
import org.example.orm.annotation.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqlSelector {

    public static <T> Optional<T> getEntityById(JdbcTemplate jdbcTemplate, Class<T> clazz, int id) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null) {
            throw new RuntimeException("Class " + clazz + "does not have a Table annotation");
        }

        List<String> selectPart = new ArrayList<>();
        List<String> fromPart = new ArrayList<>();

        Context ctx = new Context();
        ctx.tableNum = 0;
        fromPart.add(table.value() + " as t0 ");

        buildSqlParts(selectPart, fromPart, ctx, clazz);

        String select = "Select " + String.join(", ", selectPart);
        String from = " From " + String.join(" ", fromPart);
        String where = "";

        for (Field f : clazz.getFields()) {
            IdColumn idColumn = f.getAnnotation(IdColumn.class);
            if (idColumn != null) {
                where = " Where t0." + idColumn.value() + " = ?";
                break;
            }
        }

        String sql = select + from + where;

        RowMapper<T> rowMapper = createRowMapper(clazz);

        List<T> resList = jdbcTemplate.query(sql, rowMapper, id);
        System.out.println(sql);

        return resList.stream().findFirst();

    }

    private static <T> RowMapper<T> createRowMapper(Class<T> clazz) {
        return ((rs, rowNum) -> {
            final Context ctx = new Context();
            ctx.colNum = 1;
            return mapEntity(clazz, rs, ctx);
        });
    }

    private static <T> T mapEntity(Class<T> clazz, ResultSet rs, Context ctx) {
        T dataClass;
        try {
            dataClass = clazz.getConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException("Class: " + clazz + ": need empty public constructor");
        }
        for (Field field : clazz.getFields()) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                setValue(dataClass, field, ctx, rs);
            }
            IdColumn idColumn = field.getAnnotation(IdColumn.class);
            if (idColumn != null) {
                setValue(dataClass, field, ctx, rs);
            }
            Join join = field.getAnnotation(Join.class);
            if (join != null) {
                setValue(dataClass, field, ctx, rs);
            }
        }
        return dataClass;
    }

    private static <T> void buildSqlParts(List<String> select, List<String> from, Context ctx, Class<T> clazz) {
        int tableCount = ctx.tableNum;
        for (Field f : clazz.getFields()) {
            Column column = f.getAnnotation(Column.class);
            if (column != null) {
                select.add("t" + tableCount + '.' + column.value());
                continue;
            }
            IdColumn idColumn = f.getAnnotation(IdColumn.class);
            if (idColumn != null) {
                select.add("t" + tableCount + '.' + idColumn.value());
                continue;
            }
            Join join = f.getAnnotation(Join.class);
            if (join != null) {
                if (join.op().length != join.left().length || join.op().length != join.right().length) {
                    throw new RuntimeException("@Join on field: " + f.getName() + "does not have equal lengths of params");
                }
                int nextTableCount = ctx.tableNum + 1;
                Class<?> nextTableType = f.getType();
                Table nextTable = nextTableType.getAnnotation(Table.class);
                if (nextTable == null) {
                    throw new RuntimeException("Class " + nextTableType + "does not have a Table annotation");
                }

                StringBuilder sqlJoin = new StringBuilder("Join ");
                sqlJoin.append(nextTable.value());
                sqlJoin.append(" as t");
                sqlJoin.append(nextTableCount);
                sqlJoin.append(" On");

                for (int i = 0; i < join.op().length; i++) {
                    sqlJoin.append(' ').append('t').append(tableCount).append('.').append(join.left()[i])
                            .append(' ').append(join.op()[i].opString).append(' ')
                            .append('t').append(nextTableCount).append('.').append(join.right()[i]);
                }
                from.add(sqlJoin.toString());
                ctx.tableNum += 1;
                buildSqlParts(select, from, ctx, nextTableType);
                continue;
            }

        }
    }

    private static <T> void setValue(T entity, Field f, Context ctx, ResultSet rs) {
        try {
            var type = f.getType();
            if (type == String.class) {
                f.set(entity, rs.getString(ctx.colNum));
            } else if (type == Integer.class || type == int.class) {
                f.setInt(entity, rs.getInt(ctx.colNum));
            } else if (type == Long.class || type == long.class) {
                f.setLong(entity, rs.getLong(ctx.colNum));
            } else if (type == Boolean.class || type == boolean.class) {
                f.setBoolean(entity, rs.getBoolean(ctx.colNum));
            } else if (type == Date.class) {
                f.set(entity, rs.getDate(ctx.colNum));
            } else if (type == Timestamp.class) {
                f.set(entity, rs.getTimestamp(ctx.colNum));
            } else {
                if (type.getAnnotation(Table.class) == null) {
                    throw new RuntimeException("Unsuported type: " + f.getType());
                } else {
                    f.set(entity, mapEntity(type, rs, ctx));
                    ctx.colNum -= 1; // need to sub because adding next
                }
            }
            ctx.colNum += 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field must be public: " + f.getName());
        }
    }

    private static class Context {
        public int tableNum;
        public int colNum;
    }

}
