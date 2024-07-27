package org.example;

import org.example.orm.exemple.AbcExampleEntity;
import org.example.orm.exemple.MyTableEntity;
import org.example.orm.selector.SqlSelector;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Optional;

public class Main {

    public static DataSource mysqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3307/test");
        dataSource.setUsername("root");
        dataSource.setPassword("usbw");

        return dataSource;
    }

    public static void main(String[] args) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(mysqlDataSource());
        Optional<MyTableEntity> res = SqlSelector.getEntityById(jdbcTemplate, MyTableEntity.class, 1);
//        Optional<AbcExampleEntity> res = SqlSelector.getEntityById(jdbcTemplate, AbcExampleEntity.class, 2);


        System.out.println(res);
    }

}