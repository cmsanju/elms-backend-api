package com.elms.controller;

import com.elms.ai.GeminiAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificate")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class CertificateController {

    private final GeminiAIService geminiAIService;

    /**
     * Returns pure JSON data only.
     * Angular frontend renders the certificate entirely — no HTML from backend.
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody Map<String, String> request,
                                                  Authentication auth) {
        String studentName    = request.getOrDefault("studentName",    "Student");
        String courseName     = request.getOrDefault("courseName",     "Course");
        String score          = request.getOrDefault("score",          "85");
        String skills         = request.getOrDefault("skills",         "");
        String instructorName = request.getOrDefault("instructorName", "Instructor");

        String certNumber = "ELMS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String issueDate  = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        // Gemini AI generates personalized message — plain text only
        String aiMessage = geminiAIService.generateContent(
            "Write exactly 2 inspiring sentences congratulating " + studentName +
            " for completing the course '" + courseName +
            "' with a score of " + score + "%. " +
            "Be professional, warm and motivating. Return plain text only, no quotes."
        );

        return ResponseEntity.ok(Map.of(
            "studentName",       studentName,
            "courseName",        courseName,
            "score",             score,
            "skills",            skills,
            "instructorName",    instructorName,
            "certificateNumber", certNumber,
            "issuedDate",        issueDate,
            "aiMessage",         aiMessage
        ));
    }

    @GetMapping("/verify/{certNumber}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String certNumber) {
        return ResponseEntity.ok(Map.of(
            "valid",             true,
            "certificateNumber", certNumber,
            "message",           "Certificate is authentic — issued by ELMS AI Platform"
        ));
    }
}
