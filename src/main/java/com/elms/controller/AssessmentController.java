package com.elms.controller;

import com.elms.ai.GeminiAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assessment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AssessmentController {

    private final GeminiAIService geminiAIService;

    // Generate MCQ Questions via AI
    @PostMapping("/generate-mcq")
    public ResponseEntity<?> generateMCQ(@RequestBody Map<String, String> request) {
        String skill = request.getOrDefault("skill", "Java Programming");
        String topic = request.getOrDefault("topic", "Core Concepts");
        int count = Integer.parseInt(request.getOrDefault("count", "10"));
        String difficulty = request.getOrDefault("difficulty", "MEDIUM");

        String questions = geminiAIService.generateMCQQuestions(skill, topic, count, difficulty);
        return ResponseEntity.ok(Map.of("questions", questions, "skill", skill, "topic", topic));
    }

    // Generate Coding Assessment via AI
    @PostMapping("/generate-coding")
    public ResponseEntity<?> generateCodingAssessment(@RequestBody Map<String, String> request) {
        String skill = request.getOrDefault("skill", "Data Structures");
        String language = request.getOrDefault("language", "Java");
        String difficulty = request.getOrDefault("difficulty", "MEDIUM");

        String assessment = geminiAIService.generateCodingAssessment(skill, language, difficulty);
        return ResponseEntity.ok(Map.of("assessment", assessment));
    }

    // Submit & Evaluate Code
    @PostMapping("/submit-code")
    public ResponseEntity<?> submitCode(@RequestBody Map<String, String> request) {
        String code = request.getOrDefault("code", "");
        String problem = request.getOrDefault("problem", "");
        String language = request.getOrDefault("language", "java");

        String evaluation = geminiAIService.evaluateCodeSubmission(code, problem, language);
        return ResponseEntity.ok(Map.of("evaluation", evaluation));
    }

    // Submit MCQ Answers - AI evaluates
    @PostMapping("/submit-mcq")
    public ResponseEntity<?> submitMCQ(@RequestBody Map<String, Object> request) {
        String answers = request.get("answers").toString();
        String questions = request.get("questions").toString();
        String skill = request.getOrDefault("skill", "General").toString();

        String prompt = String.format("""
            Evaluate these MCQ answers and provide detailed feedback.
            Questions & Correct Answers: %s
            Student Answers: %s
            Skill: %s
            
            Return JSON: {"score": 80, "totalQuestions": 10, "correctAnswers": 8, 
            "incorrectAnswers": [{"questionId": 2, "explanation": "..."}],
            "feedback": "Overall feedback", "passed": true, "grade": "B"}
            """, questions, answers, skill);

        String result = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("result", result));
    }

    // Generate Course Completion Assessment
    @PostMapping("/final-assessment")
    public ResponseEntity<?> generateFinalAssessment(@RequestBody Map<String, String> request) {
        String courseTitle = request.getOrDefault("courseTitle", "");
        String topics = request.getOrDefault("topics", "");
        int count = Integer.parseInt(request.getOrDefault("questionCount", "20"));

        String assessment = geminiAIService.generateCompletionAssessment(courseTitle, topics, count);
        return ResponseEntity.ok(Map.of("assessment", assessment, "courseTitle", courseTitle));
    }

    // AI Q&A for specific skill
    @PostMapping("/ask-ai")
    public ResponseEntity<?> askAI(@RequestBody Map<String, String> request, Authentication auth) {
        String question = request.getOrDefault("question", "");
        String skill = request.getOrDefault("skill", "");
        String context = request.getOrDefault("context", "");

        String response = geminiAIService.chatbotResponse(question,
            "Skill: " + skill + " | Context: " + context, "");
        return ResponseEntity.ok(Map.of("answer", response));
    }

    // Smart Course Recommendation
    @PostMapping("/recommend")
    public ResponseEntity<?> recommendCourses(@RequestBody Map<String, String> request) {
        String skills = request.getOrDefault("skills", "");
        String goals = request.getOrDefault("goals", "");
        String completed = request.getOrDefault("completedCourses", "");

        String recommendations = geminiAIService.recommendCourses(skills, goals, completed);
        return ResponseEntity.ok(Map.of("recommendations", recommendations));
    }
}
