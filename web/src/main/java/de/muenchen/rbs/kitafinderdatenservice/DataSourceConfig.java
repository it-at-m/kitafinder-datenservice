package de.muenchen.rbs.kitafinderdatenservice;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
        logConnectionUrl(dataSourceProperties.getUrl());
        return dataSource;
    }

    private void logConnectionUrl(String url) {
        log.info("Connecting to database at URL: {}", url);
    }
}