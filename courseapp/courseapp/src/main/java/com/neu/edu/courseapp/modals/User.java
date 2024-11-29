package com.neu.edu.courseapp.modals;

import jakarta.persistence.*;

@Entity
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key for User
    private Long id;

    @Column(nullable = false, unique = true) // Username must be unique and not null
    private String username;

    @Column(nullable = false) // Password must not be null
    private String password;

    @ManyToOne
    @JoinColumn(name = "course1_id")
    private Course course1;

    @ManyToOne
    @JoinColumn(name = "course2_id")
    private Course course2;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Course getCourse1() {
        return course1;
    }

    public void setCourse1(Course course1) {
        this.course1 = course1;
    }

    public Course getCourse2() {
        return course2;
    }

    public void setCourse2(Course course2) {
        this.course2 = course2;
    }
}
