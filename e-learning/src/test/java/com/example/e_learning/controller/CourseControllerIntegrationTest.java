package com.example.e_learning.controller;

import com.example.e_learning.model.Course;
import com.example.e_learning.service.CourseService;
import com.example.e_learning.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this specific controller test
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtUtils jwtUtils; // Needed because WebSecurityConfig is loaded

    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        course1 = new Course();
        course1.setId(1L);
        course1.setTitle("Java Spring Boot Masterclass");
        course1.setPrice(99.99);

        course2 = new Course();
        course2.setId(2L);
        course2.setTitle("React JS for Beginners");
        course2.setPrice(49.99);
    }

    @Test
    @WithMockUser
    void getAllCourses_ShouldReturnListOfCourses() throws Exception {
        when(courseService.getAllCourses()).thenReturn(Arrays.asList(course1, course2));

        mockMvc.perform(get("/api/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Java Spring Boot Masterclass"))
                .andExpect(jsonPath("$[1].title").value("React JS for Beginners"));
    }

    @Test
    @WithMockUser
    void getCourseById_WhenCourseExists_ShouldReturnCourse() throws Exception {
        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course1));

        mockMvc.perform(get("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Java Spring Boot Masterclass"))
                .andExpect(jsonPath("$.price").value(99.99));
    }

    @Test
    @WithMockUser
    void getCourseById_WhenCourseNotFound_ShouldReturn404() throws Exception {
        when(courseService.getCourseById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/courses/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
