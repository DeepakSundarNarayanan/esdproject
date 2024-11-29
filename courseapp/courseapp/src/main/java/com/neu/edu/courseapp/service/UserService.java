package com.neu.edu.courseapp.service;

import com.neu.edu.courseapp.dao.UserDAO;
import com.neu.edu.courseapp.modals.Course;
import com.neu.edu.courseapp.modals.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    public void registerUserWithCourse(Long userId, Long courseId) {
        // Fetch user and course details
        User user = userDAO.getUserById(userId);
        Course course = userDAO.getCourseById(courseId);

        // Check if the user is already registered for the course
        if (user.getCourse1() != null && user.getCourse1().getId().equals(courseId)) {
            throw new IllegalStateException("You are already registered for this course.");
        }
        if (user.getCourse2() != null && user.getCourse2().getId().equals(courseId)) {
            throw new IllegalStateException("You are already registered for this course.");
        }

        // Register the course in one of the available slots
        if (user.getCourse1() == null) {
            user.setCourse1(course);
        } else if (user.getCourse2() == null) {
            user.setCourse2(course);
        } else {
            throw new IllegalStateException("You have already registered for two courses.");
        }

        // Save updated user information
        userDAO.saveOrUpdateUser(user);
    }
}
