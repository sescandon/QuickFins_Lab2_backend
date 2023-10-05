package co.edu.unal.software_engineering.labs.controller;

import co.edu.unal.software_engineering.labs.model.Association;
import co.edu.unal.software_engineering.labs.model.Course;
import co.edu.unal.software_engineering.labs.model.Role;
import co.edu.unal.software_engineering.labs.model.User;
import co.edu.unal.software_engineering.labs.pojo.CoursePOJO;
import co.edu.unal.software_engineering.labs.pojo.EnrolledCoursePOJO;
import co.edu.unal.software_engineering.labs.service.AssociationService;
import co.edu.unal.software_engineering.labs.service.CourseService;
import co.edu.unal.software_engineering.labs.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class CourseController{

    private final CourseService courseService;
    private final AssociationService associationService;
    private final UserService userService;

    public CourseController(CourseService courseService, AssociationService associationService, UserService userService) {
        this.courseService = courseService;
        this.associationService = associationService;
        this.userService = userService;
    }

    @PostMapping(value = {"/profesor/crear-curso"})
    public ResponseEntity<Void> createCourse(@RequestBody CoursePOJO coursePojo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User professor = userService.findByUsername(username);
        Course course = courseService.mapperCourseEntity(coursePojo);
        Role role = new Role();  

        if (professor == null || !courseService.isRightCourse(course)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        role.setId(Role.ROLE_TEACHER); 
        courseService.save(course);

        associationService.associate(professor, role, course, null);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/mis-cursos")
    public List<EnrolledCoursePOJO> getCoursesByUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        List<Association> associations = associationService.getAssociationsByUser(user);
        List<EnrolledCoursePOJO> enrolledCourses = new ArrayList<>();
        for (Association association : associations) {
            enrolledCourses.add(new EnrolledCoursePOJO(association));
        }
        return enrolledCourses;
    }
}
