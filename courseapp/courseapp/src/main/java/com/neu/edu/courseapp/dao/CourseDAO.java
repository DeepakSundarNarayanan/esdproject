package com.neu.edu.courseapp.dao;

import com.neu.edu.courseapp.modals.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public List<String> getDistinctTerms() {
        try (Session session = sessionFactory.openSession()) {
            Query<String> query = session.createQuery("SELECT DISTINCT c.term FROM Course c", String.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch distinct terms: " + e.getMessage(), e);
        }
    }

    public List<Course> filterCourses(String term, String courseCode, String courseNumber, String courseName) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "FROM Course c WHERE c.term = :term";

            if (courseCode != null && !courseCode.isEmpty()) {
                queryString += " AND c.courseCode = :courseCode";
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                queryString += " AND c.courseNumber = :courseNumber";
            }
            if (courseName != null && !courseName.isEmpty()) {
                queryString += " AND c.courseName LIKE :courseName";
            }

            Query<Course> query = session.createQuery(queryString, Course.class);
            query.setParameter("term", term);

            if (courseCode != null && !courseCode.isEmpty()) {
                query.setParameter("courseCode", courseCode);
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                query.setParameter("courseNumber", courseNumber);
            }
            if (courseName != null && !courseName.isEmpty()) {
                query.setParameter("courseName", "%" + courseName + "%");
            }

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching courses: " + e.getMessage(), e);
        }
    }

    public List<String> findDistinctCourseCodes(String term) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT DISTINCT c.courseCode FROM Course c WHERE c.courseCode LIKE :term";
            return session.createQuery(query, String.class)
                    .setParameter("term", "%" + term + "%")
                    .getResultList();
        }
    }

    public List<String> findDistinctCourseNumbers(String term) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT DISTINCT c.courseNumber FROM Course c WHERE c.courseNumber LIKE :term";
            return session.createQuery(query, String.class)
                    .setParameter("term", "%" + term + "%")
                    .getResultList();
        }
    }

    public List<String> findDistinctCourseNames(String term) {
        try (Session session = sessionFactory.openSession()) {
            String query = "SELECT DISTINCT c.courseName FROM Course c WHERE c.courseName LIKE :term";
            return session.createQuery(query, String.class)
                    .setParameter("term", "%" + term + "%")
                    .getResultList();
        }
    }

    public List<Course> filterCoursesWithPagination(String term, String courseCode, String courseNumber, String courseName, int page, int size) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "FROM Course c WHERE c.term = :term";

            if (courseCode != null && !courseCode.isEmpty()) {
                queryString += " AND c.courseCode = :courseCode";
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                queryString += " AND c.courseNumber = :courseNumber";
            }
            if (courseName != null && !courseName.isEmpty()) {
                queryString += " AND c.courseName LIKE :courseName";
            }

            Query<Course> query = session.createQuery(queryString, Course.class);
            query.setParameter("term", term);

            if (courseCode != null && !courseCode.isEmpty()) {
                query.setParameter("courseCode", courseCode);
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                query.setParameter("courseNumber", courseNumber);
            }
            if (courseName != null && !courseName.isEmpty()) {
                query.setParameter("courseName", "%" + courseName + "%");
            }

            // Set pagination parameters
            System.out.println(page);
            System.out.println(size);
            query.setFirstResult(page * size); // Starting index
            query.setMaxResults(size); // Number of records to fetch

            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching paginated courses: " + e.getMessage(), e);
        }
    }


    public int countFilteredCourses(String term, String courseCode, String courseNumber, String courseName) {
        try (Session session = sessionFactory.openSession()) {
            String queryString = "SELECT COUNT(c) FROM Course c WHERE c.term = :term";

            if (courseCode != null && !courseCode.isEmpty()) {
                queryString += " AND c.courseCode = :courseCode";
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                queryString += " AND c.courseNumber = :courseNumber";
            }
            if (courseName != null && !courseName.isEmpty()) {
                queryString += " AND c.courseName LIKE :courseName";
            }

            Query<Long> query = session.createQuery(queryString, Long.class);
            query.setParameter("term", term);

            if (courseCode != null && !courseCode.isEmpty()) {
                query.setParameter("courseCode", courseCode);
            }
            if (courseNumber != null && !courseNumber.isEmpty()) {
                query.setParameter("courseNumber", courseNumber);
            }
            if (courseName != null && !courseName.isEmpty()) {
                query.setParameter("courseName", "%" + courseName + "%");
            }

            return query.uniqueResult().intValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error counting courses: " + e.getMessage(), e);
        }
    }

}
