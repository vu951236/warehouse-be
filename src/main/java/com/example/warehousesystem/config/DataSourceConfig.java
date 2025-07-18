package com.example.warehousesystem.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    @Autowired
    private Dotenv dotenv;

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dotenv.get("URL_DB"));
        dataSource.setUsername(dotenv.get("USERNAME_DB"));
        dataSource.setPassword(dotenv.get("PASSWORD_DB"));
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        return dataSource;
    }
}
