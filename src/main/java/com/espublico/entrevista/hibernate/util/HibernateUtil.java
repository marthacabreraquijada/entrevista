package com.espublico.entrevista.hibernate.util;

import java.util.HashMap;
import java.util.Map;

import com.espublico.entrevista.hibernate.entity.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
  private static StandardServiceRegistry registry;
  private static SessionFactory sessionFactory;

  public static SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      try {
        StandardServiceRegistryBuilder registryBuilder =
            new StandardServiceRegistryBuilder();

        Map<String, String> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/entrevista");
        settings.put("hibernate.connection.username", "sa");
        settings.put("hibernate.connection.password", "ma2020ca");
//        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:59001/entrevista");
//        settings.put("hibernate.connection.username", "root");
//        settings.put("hibernate.connection.password", "root");
        settings.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

        //settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.hbm2ddl.auto", "update");

        registryBuilder.applySettings(settings);

        registry = registryBuilder.build();

        MetadataSources sources = new MetadataSources(registry)
            .addAnnotatedClass(FilmsEntity.class)
            .addAnnotatedClass(PeopleEntity.class)
            .addAnnotatedClass(StarshipsEntity.class)
            .addAnnotatedClass(OrdersEntity.class);

        Metadata metadata = sources.getMetadataBuilder().build();

        sessionFactory = metadata.getSessionFactoryBuilder().build();
      } catch (Exception e) {
        System.out.println("SessionFactory creation failed");
        if (registry != null) {
          StandardServiceRegistryBuilder.destroy(registry);
        }
      }
    }
    return sessionFactory;
  }

  public static void shutdown() {
    if (registry != null) {
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }
}