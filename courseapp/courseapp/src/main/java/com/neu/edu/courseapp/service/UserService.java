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

//    public void registerUserWithCourse(Long userId, Long courseId) {
//        User user = userDAO.getUserById(userId);
//        Course course = userDAO.getCourseById(courseId);
//
//        // Check if the user is already registered for the course
//        if (user.getCourse1() != null && user.getCourse1().getId().equals(courseId)) {
//            throw new IllegalStateException("You are already registered for this course.");
//        }
//        if (user.getCourse2() != null && user.getCourse2().getId().equals(courseId)) {
//            throw new IllegalStateException("You are already registered for this course.");
//        }
//
//        // Register the course in one of the available slots
//        if (user.getCourse1() == null) {
//            user.setCourse1(course);
//        } else if (user.getCourse2() == null) {
//            user.setCourse2(course);
//        } else {
//            throw new IllegalStateException("You have already registered for two courses.");
//        }
//
//        // Save updated user information
//        userDAO.saveOrUpdateUser(user);
//    }
public void registerUserWithCourse(Long userId, Long courseId) {
    User user = userDAO.getUserById(userId);
    Course course = userDAO.getCourseById(courseId);

    // Check if the course has available seats
    if (course.getNumberOfSeats() <= 0) {
        throw new IllegalStateException("No seats available for this course.");
    }

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

    // Decrement the number of seats
    course.setNumberOfSeats(course.getNumberOfSeats() - 1);

    // Save updated user and course information
    userDAO.saveOrUpdateUser(user);
    userDAO.saveOrUpdateCourse(course); // Add this method in UserDAO
}


//    public void removeCourseFromUser(Long userId, Long courseId) {
//        User user = userDAO.getUserById(userId);
//
//        // Check and remove the course
//        if (user.getCourse1() != null && user.getCourse1().getId().equals(courseId)) {
//            user.setCourse1(null);
//        } else if (user.getCourse2() != null && user.getCourse2().getId().equals(courseId)) {
//            user.setCourse2(null);
//        } else {
//            throw new IllegalStateException("Course not registered by the user.");
//        }
//
//        // Save updated user
//        userDAO.saveOrUpdateUser(user);
//    }

    public void removeCourseFromUser(Long userId, Long courseId) {
        User user = userDAO.getUserById(userId);
        Course course = userDAO.getCourseById(courseId);

        // Check and remove the course
        if (user.getCourse1() != null && user.getCourse1().getId().equals(courseId)) {
            user.setCourse1(null);
        } else if (user.getCourse2() != null && user.getCourse2().getId().equals(courseId)) {
            user.setCourse2(null);
        } else {
            throw new IllegalStateException("Course not registered by the user.");
        }

        // Increment the number of seats
        course.setNumberOfSeats(course.getNumberOfSeats() + 1);

        // Save updated user and course
        userDAO.saveOrUpdateUser(user);
        userDAO.saveOrUpdateCourse(course); // Add this method in UserDAO
    }

}
