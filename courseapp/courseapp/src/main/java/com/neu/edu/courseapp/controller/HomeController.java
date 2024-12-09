package com.neu.edu.courseapp.controller;

import com.neu.edu.courseapp.dao.CourseDAO;
import com.neu.edu.courseapp.dao.UserDAO;
import com.neu.edu.courseapp.modals.Course;
import com.neu.edu.courseapp.modals.User;
import com.neu.edu.courseapp.service.EmailService;
import com.neu.edu.courseapp.service.ExcelService;
import com.neu.edu.courseapp.service.PDFService;
import com.neu.edu.courseapp.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import com.neu.edu.courseapp.dao.AdminUserDAO;
import com.neu.edu.courseapp.modals.AdminUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;



import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    private ExcelService excelService;
    @Autowired
    private AdminUserDAO adminUserDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    UserService userService;
    @Autowired
    private CourseDAO courseDAO;
    @Autowired
    EmailService emailService;
    @Autowired
    PDFService pdfService;

    @GetMapping(value = "/")
    public String home() {
        return "index";
    }

    @GetMapping("/admin")
    public String showAdminLoginPage() {
        return "admin-login";
    }

    @PostMapping("/admin")
    public ModelAndView processAdminLogin(@RequestParam("adminUsername") String username,
                                          @RequestParam("adminPassword") String password) {
        Optional<AdminUser> adminUser = adminUserDAO.findByUsernameAndPassword(username, password);

        if (adminUser.isPresent()) {
            // Redirect to dashboard on successful login
            ModelAndView mav = new ModelAndView("admin-dashboard");
            mav.addObject("adminName", escapeHtml(adminUser.get().getName()));
            return mav;
        } else {
            // Return to login with an error message
            ModelAndView mav = new ModelAndView("admin-login");
            mav.addObject("error", "Invalid username or password");
            return mav;
        }
    }

    @PostMapping("/admin/upload-courses")
    public ModelAndView uploadCourses(@RequestParam("file") MultipartFile file) {
        ModelAndView mav = new ModelAndView("admin-dashboard");
        try {
            excelService.saveCoursesFromExcel(file);
            mav.addObject("successMessage", "File uploaded and courses saved successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("errorMessage", "Failed to process the file: " + e.getMessage());
        }
        return mav;
    }

    @PostMapping("/login")
    public ModelAndView processUserLogin(@RequestParam("username") String username,
                                          @RequestParam("password") String password,
                                         HttpServletResponse response) {
        Optional<User> user = userDAO.findByUsernameAndPassword(username, password);

        if (user.isPresent()) {

            Cookie userIdCookie = new Cookie("userId", user.get().getId().toString());
            userIdCookie.setPath("/");
            userIdCookie.setHttpOnly(true); // Secure cookie to prevent JavaScript access
            response.addCookie(userIdCookie);

//            emailService.sendLoginEmail(username, user.get().getFirstName();

            ModelAndView mav = new ModelAndView("success-login");
            mav.addObject("adminName", escapeHtml(user.get().getFirstname()));
            mav.addObject("userId", user.get().getId()); // Pass userId
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("error", "Invalid username or password");
            return mav;
        }
    }

    private String escapeHtml(String input) {
        return input == null ? null : input.replaceAll("&", "&amp;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;");
    }

//    @GetMapping("/register")
//    public ModelAndView showRegisterPage() {
//        ModelAndView mav = new ModelAndView("register-course");
//        try (Session session = sessionFactory.openSession()) {
//            List<String> terms = session.createQuery("SELECT DISTINCT c.term FROM Course c", String.class).getResultList();
//            mav.addObject("terms", terms);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", "Failed to load terms: " + e.getMessage());
//        }
//        return mav;
//    }

    @GetMapping("/register")
    public ModelAndView showRegisterPage() {
        ModelAndView mav = new ModelAndView("register-course");
        try {

            List<String> terms = courseDAO.getDistinctTerms();
            mav.addObject("terms", terms);
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("error", "Failed to load terms: " + e.getMessage());
        }
        return mav;
    }

    @PostMapping("/search-course")
    public ModelAndView showCourseSearchPage(@RequestParam("term") String term) {
        ModelAndView mav = new ModelAndView("search-course");
        mav.addObject("term", term);
        return mav;
    }

//    @PostMapping("/filter-courses")
//    public ModelAndView filterCourses(@RequestParam("term") String term,
//                                      @RequestParam(value = "courseCode", required = false) String courseCode,
//                                      @RequestParam(value = "courseNumber", required = false) String courseNumber,
//                                      @RequestParam(value = "courseName", required = false) String courseName) {
//        ModelAndView mav = new ModelAndView("filtered-courses");
//
//        try (Session session = sessionFactory.openSession()) {
//            String queryString = "FROM Course c WHERE c.term = :term";
//            if (courseCode != null && !courseCode.isEmpty()) {
//                queryString += " AND c.courseCode = :courseCode";
//            }
//            if (courseNumber != null && !courseNumber.isEmpty()) {
//                queryString += " AND c.courseNumber = :courseNumber";
//            }
//            if (courseName != null && !courseName.isEmpty()) {
//                queryString += " AND c.courseName LIKE :courseName";
//            }
//
//            Query<Course> query = session.createQuery(queryString, Course.class);
//            query.setParameter("term", term);
//
//            if (courseCode != null && !courseCode.isEmpty()) {
//                query.setParameter("courseCode", courseCode);
//            }
//            if (courseNumber != null && !courseNumber.isEmpty()) {
//                query.setParameter("courseNumber", courseNumber);
//            }
//            if (courseName != null && !courseName.isEmpty()) {
//                query.setParameter("courseName", "%" + courseName + "%");
//            }
//
//            List<Course> courses = query.getResultList();
//            mav.addObject("courses", courses);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", "Failed to fetch courses: " + e.getMessage());
//        }
//
//        return mav;
//    }
//    @PostMapping("/filter-courses")
//    public ModelAndView filterCourses(@RequestParam("term") String term,
//                                  @RequestParam(value = "courseCode", required = false) String courseCode,
//                                  @RequestParam(value = "courseNumber", required = false) String courseNumber,
//                                  @RequestParam(value = "courseName", required = false) String courseName) {
//        ModelAndView mav = new ModelAndView("filtered-courses");
//        try {
//
//            List<Course> courses = courseDAO.filterCourses(term, courseCode, courseNumber, courseName);
//            mav.addObject("courses", courses);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", "Failed to fetch courses: " + e.getMessage());
//    }
//        return mav;
//    }

//    @PostMapping("/filter-courses")
//    public ModelAndView filterCourses(@RequestParam("term") String term,
//                                      @RequestParam(value = "courseCode", required = false) String courseCode,
//                                      @RequestParam(value = "courseNumber", required = false) String courseNumber,
//                                      @RequestParam(value = "courseName", required = false) String courseName,
//                                      @CookieValue(value = "userId", required = false) String userIdCookie) {
//        ModelAndView mav = new ModelAndView("filtered-courses");
//
//        if (userIdCookie == null) {
//            mav.setViewName("redirect:/");
//            mav.addObject("error", "User not logged in.");
//            return mav;
//        }
//
//        Long userId = Long.valueOf(userIdCookie);
//
//        try {
//            // Fetch filtered courses
//            List<Course> courses = courseDAO.filterCourses(term, courseCode, courseNumber, courseName);
//            mav.addObject("courses", courses);
//
//            // Fetch registered courses for the user
//            List<Course> registeredCourses = userDAO.getRegisteredCourses(userId);
//            mav.addObject("registeredCourses", registeredCourses);
//
//            mav.addObject("userId", userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", "Failed to fetch courses: " + e.getMessage());
//        }
//
//        return mav;
//    }

    @PostMapping("/filter-courses")
    public ModelAndView filterCourses(@RequestParam("term") String term,
                                      @RequestParam(value = "courseCode", required = false) String courseCode,
                                      @RequestParam(value = "courseNumber", required = false) String courseNumber,
                                      @RequestParam(value = "courseName", required = false) String courseName,
                                      @RequestParam(value = "page", defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                      @CookieValue(value = "userId", required = false) String userIdCookie) {
        ModelAndView mav = new ModelAndView("filtered-courses");

        // Check if the user is logged in
        if (userIdCookie == null) {
            mav.setViewName("redirect:/");
            mav.addObject("error", "User not logged in.");
            return mav;
        }

        Long userId = Long.valueOf(userIdCookie);

        try {
            // Ensure page and size are non-negative
            page = Math.max(0, page);
            size = Math.max(1, size);

            // Fetch paginated filtered courses
            List<Course> courses = courseDAO.filterCoursesWithPagination(term, courseCode, courseNumber, courseName, page, size);
            mav.addObject("courses", courses);

            // Fetch registered courses for the user
            List<Course> registeredCourses = userDAO.getRegisteredCourses(userId);
            mav.addObject("registeredCourses", registeredCourses);

            mav.addObject("userId", userId);
            mav.addObject("currentPage", page);
            mav.addObject("pageSize", size);
            mav.addObject("term", term);

            // Calculate total pages and records
            int totalRecords = courseDAO.countFilteredCourses(term, courseCode, courseNumber, courseName);
            int totalPages = (int) Math.ceil((double) totalRecords / size);

            // Add pagination data to the model
            mav.addObject("totalPages", totalPages);
            mav.addObject("totalRecords", totalRecords);

        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("error", "Failed to fetch courses: " + e.getMessage());
        }

        return mav;
    }




//    @PostMapping("/register-course")
//    public ModelAndView registerCourse(@RequestParam("courseId") Long courseId,
//                                       @RequestParam("userId") Long userId) {
//        ModelAndView mav = new ModelAndView("filtered-courses");
//
//        try {
//            userService.registerUserWithCourse(userId, courseId);
//            mav.addObject("success", "Successfully registered for the course!");
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", e.getMessage());
//        }
//
//        return mav;
//    }

    @PostMapping("/register-course")
    public ResponseEntity<String> registerCourse(@RequestParam("courseId") Long courseId,
                                                 @RequestParam("userId") Long userId) {
        try {
            userService.registerUserWithCourse(userId, courseId);
            return ResponseEntity.ok("Successfully registered for the course!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




//    @GetMapping("/view-courses")
//    public ModelAndView viewRegisteredCourses(@RequestParam("userId") Long userId) {
//        ModelAndView mav = new ModelAndView("view-registered-courses");
//        try {
//            // Fetch registered courses for the user
//            List<Course> registeredCourses = userDAO.getRegisteredCourses(userId);
//            mav.addObject("registeredCourses", registeredCourses);
//            mav.addObject("userId", userId);
//        } catch (Exception e) {
//            e.printStackTrace();
//            mav.addObject("error", "Failed to fetch registered courses: " + e.getMessage());
//        }
//        return mav;
//    }
@GetMapping("/view-courses")
public ModelAndView viewRegisteredCourses(@RequestParam("userId") Long userId) {
    ModelAndView mav = new ModelAndView("view-registered-courses");
    try {
        // Fetch the user and their registered courses
        User user = userDAO.getUserById(userId); // Fetch the user from the database
        List<Course> registeredCourses = userDAO.getRegisteredCourses(userId);

        // Pass data to the model
        mav.addObject("registeredCourses", registeredCourses);
        mav.addObject("userId", userId);
        mav.addObject("adminName", user.getUsername()); // Use the fetched user object
    } catch (Exception e) {
        e.printStackTrace();
        mav.addObject("error", "Failed to fetch registered courses: " + e.getMessage());
    }
    return mav;
}


    @PostMapping("/remove-course")
    public ModelAndView removeCourse(@RequestParam("courseId") Long courseId,
                                     @RequestParam("userId") Long userId) {
        ModelAndView mav = new ModelAndView("redirect:/view-courses?userId=" + userId);
        try {
            userService.removeCourseFromUser(userId, courseId);
            mav.addObject("success", "Successfully removed the course!");
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("error", e.getMessage());
        }
        return mav;
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("userId", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);


        return "redirect:/";
    }

    @GetMapping("/success-login")
    public ModelAndView successLogin(@RequestParam("userId") Long userId) {
        ModelAndView mav = new ModelAndView("success-login");
        try {
            // Fetch the user by ID
            User user = userDAO.getUserById(userId);
            // Add the username to the model
            mav.addObject("adminName", user.getFirstname());
            mav.addObject("userId", userId);
        } catch (Exception e) {
            e.printStackTrace();
            mav.addObject("error", "Failed to load user details: " + e.getMessage());
        }

        return mav;
    }

    @GetMapping("/generate-pdf")
    public void generatePDF(@RequestParam("userId") Long userId, HttpServletResponse response) throws IOException {
        List<Course> registeredCourses = userDAO.getRegisteredCourses(userId);
        pdfService.generateRegisteredCoursesPDF(registeredCourses, response);

    }

//    @GetMapping("/suggestions/course-code")
//    public List<String> getCourseCodeSuggestions(@RequestParam("term") String term) {
//        return courseDAO.findDistinctCourseCodes(term);
//    }

//    @GetMapping("/suggestions/course-number")
//    public List<String> getCourseNumberSuggestions(@RequestParam("term") String term) {
//        return courseDAO.findDistinctCourseNumbers(term);
//    }
//
//    @GetMapping("/suggestions/course-name")
//    public List<String> getCourseNameSuggestions(@RequestParam("term") String term) {
//        return courseDAO.findDistinctCourseNames(term);
//    }

    @GetMapping("/suggestions/course-code")
    public ResponseEntity<List<String>> getCourseCodeSuggestions(@RequestParam("term") String term) {
        try {
            List<String> suggestions = courseDAO.findDistinctCourseCodes(term);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/suggestions/course-number")
    public ResponseEntity<List<String>> getCourseNumberSuggestions(@RequestParam("term") String term) {
        try {
            List<String> suggestions = courseDAO.findDistinctCourseNumbers(term);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/suggestions/course-name")
    public ResponseEntity<List<String>> getCourseNameSuggestions(@RequestParam("term") String term) {
        try {
            List<String> suggestions = courseDAO.findDistinctCourseNames(term);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }









}
