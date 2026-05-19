package com.elms.controller;

import com.elms.ai.GeminiAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/interview")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class InterviewController {

    private final GeminiAIService geminiAIService;

    // Start Interview - Generate AI questions
    @PostMapping("/start")
    public ResponseEntity<?> startInterview(@RequestBody Map<String, String> request, Authentication auth) {
        String role = request.getOrDefault("role", "Software Engineer");
        String skills = request.getOrDefault("skills", "Java, Spring Boot");
        int questionCount = Integer.parseInt(request.getOrDefault("questionCount", "8"));

        String questions = geminiAIService.generateInterviewQuestions(role, skills, questionCount);
        String sessionId = UUID.randomUUID().toString();

        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "questions", questions,
            "role", role,
            "skills", skills,
            "startTime", LocalDateTime.now().toString()
        ));
    }

    // Submit answer for a question
    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(@RequestBody Map<String, String> request) {
        String question = request.getOrDefault("question", "");
        String answer = request.getOrDefault("answer", "");
        String expectedKeyPoints = request.getOrDefault("expectedKeyPoints", "");

        String evaluation = geminiAIService.evaluateInterviewAnswer(question, answer, expectedKeyPoints);
        return ResponseEntity.ok(Map.of("evaluation", evaluation));
    }

    // Submit emotion data from face detection
    @PostMapping("/emotion-data")
    public ResponseEntity<?> submitEmotionData(@RequestBody Map<String, String> request) {
        String emotionData = request.getOrDefault("emotionData", "{}");
        String context = request.getOrDefault("context", "Technical Interview");

        String analysis = geminiAIService.analyzeEmotionData(emotionData, context);
        return ResponseEntity.ok(Map.of("emotionAnalysis", analysis));
    }

    // Generate final interview report
    @PostMapping("/complete")
    public ResponseEntity<?> completeInterview(@RequestBody Map<String, String> request) {
        String answersJson = request.getOrDefault("answers", "[]");
        String emotionAnalysis = request.getOrDefault("emotionAnalysis", "{}");
        String role = request.getOrDefault("role", "Software Engineer");
        String candidateName = request.getOrDefault("candidateName", "Candidate");

        String report = geminiAIService.generateInterviewReport(answersJson, emotionAnalysis, role);

        return ResponseEntity.ok(Map.of(
            "report", report,
            "candidateName", candidateName,
            "role", role,
            "completedAt", LocalDateTime.now().toString()
        ));
    }

    // Quick AI feedback during interview
    @PostMapping("/live-feedback")
    public ResponseEntity<?> getLiveFeedback(@RequestBody Map<String, String> request) {
        String answer = request.getOrDefault("answer", "");
        String question = request.getOrDefault("question", "");

        String prompt = String.format("""
            Give brief real-time coaching feedback (2-3 sentences) for this interview answer.
            Question: %s
            Answer so far: %s
            Focus on: content completeness, structure, and key points missing.
            Be encouraging and actionable.
            """, question, answer);

        String feedback = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("feedback", feedback));
    }

    // Generate practice interview scenario
    @PostMapping("/practice")
    public ResponseEntity<?> generatePracticeScenario(@RequestBody Map<String, String> request) {
        String role = request.getOrDefault("role", "Developer");
        String type = request.getOrDefault("type", "TECHNICAL"); // BEHAVIORAL, TECHNICAL, HR

        String prompt = String.format("""
            Create a realistic %s interview scenario for a %s role.
            Include: scenario description, 5 targeted questions, evaluation criteria.
            Return JSON: {
              "scenario": "description",
              "company": "mock company name",
              "questions": [...],
              "tips": ["tip1", "tip2"],
              "commonMistakes": ["mistake1"]
            }
            """, type, role);

        String scenario = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("scenario", scenario));
    }
}
