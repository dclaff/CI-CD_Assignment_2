package com.example.microservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;

@Configuration
public class DataSourceConfig {

    private final JdbcTemplate jdbc;

    public DataSourceConfig(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Runs before any bean is created — ensures the SQLite data directory exists
     * so that HikariCP can open the database file on first access.
     */
    @Bean
    static BeanFactoryPostProcessor sqliteDirectoryCreator() {
        return new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory factory) throws BeansException {
                Environment env = factory.getBean(Environment.class);
                String url = env.getProperty("spring.datasource.url");
                if (url != null && url.startsWith("jdbc:sqlite:") && !url.contains(":memory:")) {
                    String path = url.replace("jdbc:sqlite:", "");
                    File parentDir = new File(path).getParentFile();
                    if (parentDir != null && !parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                }
            }
        };
    }

    @PostConstruct
    public void init() {
        // Ensure foreign keys are enforced (belt-and-suspenders alongside HikariCP init SQL)
        jdbc.execute("PRAGMA foreign_keys = ON");
    }
}
