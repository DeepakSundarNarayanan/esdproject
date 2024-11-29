package com.neu.edu.courseapp.utils;

import java.util.Map;

import com.neu.edu.courseapp.modals.AdminUser;
import com.neu.edu.courseapp.modals.Course;
import com.neu.edu.courseapp.modals.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class HibernateConfig {
    @Bean
    public SessionFactory getSessionFactory() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/courseapp");
        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "root");
        settings.put("hibernate.hbm2ddl.auto", "update");
        settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        settings.put("hibernate.dialect.storage_engine", "innodb");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        // Create ServiceRegistry
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings)
                .build();

        // Create MetadataSources
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addPackage("com.neu.edu.courseapp.model");

        // Add annotated classes or packages
        metadataSources.addAnnotatedClass(AdminUser.class);
        metadataSources.addAnnotatedClass(Course.class);
        metadataSources.addAnnotatedClass(User.class);

        // Build Metadata
        Metadata metadata = metadataSources.buildMetadata();

        // Build and return SessionFactory
        return metadata.getSessionFactoryBuilder().build();
    }
}
