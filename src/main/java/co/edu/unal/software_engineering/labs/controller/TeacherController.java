package co.edu.unal.software_engineering.labs.controller;

import co.edu.unal.software_engineering.labs.model.*;
import co.edu.unal.software_engineering.labs.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeacherController{

    private AssociationService associationService;

    private UserService userService;

    private CourseService courseService;

    private PeriodService periodService;


    public TeacherController( UserService userService, CourseService courseService, PeriodService periodService,
                              AssociationService associationService ){
        this.userService = userService;
        this.courseService = courseService;
        this.periodService = periodService;
        this.associationService = associationService;
    }


    @PostMapping( value = { "/profesor/inscribir/periodo/{periodId}/curso/{courseId}" } )
    public ResponseEntity<Void> associateTeacher( @PathVariable Integer periodId, @PathVariable Integer courseId ){
        String username = SecurityContextHolder.getContext( ).getAuthentication( ).getName( );
        User teacher = userService.findByUsername( username );
        Course course = courseService.findById( courseId );
        Period period = periodService.findById( periodId );
        Role role = new Role( );

        if( course == null || period == null ){
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
        }

        role.setId( Role.ROLE_TEACHER );
        associationService.associate( teacher, role, course, period );

        return new ResponseEntity<>( HttpStatus.CREATED  );
    }

}