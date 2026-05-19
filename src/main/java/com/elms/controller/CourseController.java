package com.elms.controller;

import com.elms.ai.GeminiAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final GeminiAIService geminiAIService;

    @GetMapping
    public ResponseEntity<?> getAllCourses() {
        // In production, query CourseRepository
        return ResponseEntity.ok(Map.of(
            "courses", List.of(
                Map.of("id", 1, "title", "Full Stack Java with Spring Boot & Angular",
                    "category", "Backend", "level", "Intermediate", "rating", 4.8, "students", 12400),
                Map.of("id", 2, "title", "AI & Machine Learning with Python",
                    "category", "AI/ML", "level", "Advanced", "rating", 4.9, "students", 8900),
                Map.of("id", 3, "title", "Angular 20 Complete Guide",
                    "category", "Frontend", "level", "Intermediate", "rating", 4.7, "students", 6200)
            )
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(Map.of(
            "id", id,
            "title", "Full Stack Java with Spring Boot & Angular",
            "description", "Master enterprise Java development",
            "category", "Backend",
            "instructor", "Dr. Ravi Kumar",
            "duration", "40 hours",
            "level", "Intermediate",
            "totalLessons", 85
        ));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<?> enrollInCourse(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(Map.of(
            "message", "Successfully enrolled",
            "courseId", id,
            "studentEmail", auth.getName()
        ));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<?> getCourseProgress(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(Map.of(
            "courseId", id,
            "progressPercent", 65,
            "completedLessons", 55,
            "totalLessons", 85
        ));
    }

    @PostMapping("/{id}/generate-content")
    public ResponseEntity<?> generateCourseContent(@PathVariable Long id,
                                                    @RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "");
        String prompt = String.format("""
            Create detailed course content/notes for the topic: "%s"
            Format it as structured learning material with:
            - Introduction
            - Key concepts (explained clearly)
            - Code examples (if applicable)
            - Best practices
            - Common pitfalls
            - Summary & next steps
            """, topic);

        String content = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("content", content, "topic", topic));
    }
}
