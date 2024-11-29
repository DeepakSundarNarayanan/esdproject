package com.neu.edu.courseapp.dao;

import com.neu.edu.courseapp.modals.AdminUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AdminUserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Optional<AdminUser> findByUsernameAndPassword(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM AdminUser WHERE username = :username AND password = :password";
            AdminUser adminUser = session.createQuery(hql, AdminUser.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(adminUser);
        }
    }

    public void save(AdminUser adminUser) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(adminUser);
            session.getTransaction().commit();
        }
    }
}
