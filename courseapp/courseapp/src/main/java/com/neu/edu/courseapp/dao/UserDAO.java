package com.neu.edu.courseapp.dao;


import com.neu.edu.courseapp.modals.Course;
import com.neu.edu.courseapp.modals.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Repository
public class UserDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public Optional<User> findByUsernameAndPassword(String username, String password) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM User WHERE username = :username AND password = :password";
            User user = session.createQuery(hql, User.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }

    public User getUserById(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, userId);
        }
    }

    public Course getCourseById(Long courseId) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Course.class, courseId);
        }
    }

    public void saveOrUpdateUser(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(user);
            session.getTransaction().commit();
        }
    }

    public List<Course> getRegisteredCourses(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            List<Course> registeredCourses = new ArrayList<>();
            if (user.getCourse1() != null) registeredCourses.add(user.getCourse1());
            if (user.getCourse2() != null) registeredCourses.add(user.getCourse2());
            return registeredCourses;
        }
    }

    public void saveOrUpdateCourse(Course course) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(course);
            session.getTransaction().commit();
        }
    }



}
