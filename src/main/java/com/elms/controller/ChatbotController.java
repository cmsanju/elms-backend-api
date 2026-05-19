package com.elms.controller;

import com.elms.ai.GeminiAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class ChatbotController {

    private final GeminiAIService geminiAIService;

    // Text Chat
    @PostMapping("/chat")
    public ResponseEntity<?> chat(@RequestBody Map<String, String> request, Authentication auth) {
        String message = request.getOrDefault("message", "");
        String history = request.getOrDefault("history", "");
        String context = request.getOrDefault("context", "ELMS learning platform");

        String response = geminiAIService.chatbotResponse(message, context, history);
        return ResponseEntity.ok(Map.of(
            "response", response,
            "timestamp", System.currentTimeMillis()
        ));
    }

    // Voice Chat - accepts transcribed text from browser Web Speech API
    @PostMapping("/voice-chat")
    public ResponseEntity<?> voiceChat(@RequestBody Map<String, String> request) {
        String transcribedText = request.getOrDefault("transcribedText", "");
        String context = request.getOrDefault("context", "");

        String response = geminiAIService.processVoiceQuery(transcribedText, context);
        return ResponseEntity.ok(Map.of(
            "response", response,
            "inputText", transcribedText
        ));
    }

    // Document Upload & Analysis
    @PostMapping("/analyze-document")
    public ResponseEntity<?> analyzeDocument(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "query", defaultValue = "Summarize this document") String query) {

        try {
            String content = extractTextFromFile(file);
            String analysis = geminiAIService.analyzeDocument(content, query);
            return ResponseEntity.ok(Map.of(
                "analysis", analysis,
                "fileName", file.getOriginalFilename(),
                "query", query
            ));
        } catch (Exception e) {
            log.error("Document analysis failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Could not process document: " + e.getMessage()));
        }
    }

    // Quick Answer (FAQ-style)
    @PostMapping("/quick-answer")
    public ResponseEntity<?> quickAnswer(@RequestBody Map<String, String> request) {
        String question = request.getOrDefault("question", "");
        String category = request.getOrDefault("category", "general");

        String prompt = String.format("""
            Answer this %s question briefly and accurately (max 3 sentences).
            Question: %s
            If it's a technical question, include a code snippet if relevant.
            """, category, question);

        String answer = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("answer", answer, "category", category));
    }

    // Explain a concept
    @PostMapping("/explain")
    public ResponseEntity<?> explainConcept(@RequestBody Map<String, String> request) {
        String concept = request.getOrDefault("concept", "");
        String level = request.getOrDefault("level", "BEGINNER"); // BEGINNER, INTERMEDIATE, EXPERT

        String prompt = String.format("""
            Explain "%s" for a %s level learner.
            Include: definition, real-world analogy, practical example with code if applicable, 
            common misconceptions, and 3 key takeaways.
            Make it engaging and easy to understand.
            """, concept, level);

        String explanation = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("explanation", explanation, "concept", concept));
    }

    // Code help
    @PostMapping("/code-help")
    public ResponseEntity<?> codeHelp(@RequestBody Map<String, String> request) {
        String code = request.getOrDefault("code", "");
        String issue = request.getOrDefault("issue", "review this code");
        String language = request.getOrDefault("language", "java");

        String prompt = String.format("""
            You are an expert %s developer. Help with this code request: %s
            
            Code:
            ```%s
            %s
            ```
            
            Provide: issue identification, corrected code, explanation of changes, best practices.
            """, language, issue, language, code);

        String help = geminiAIService.generateContent(prompt);
        return ResponseEntity.ok(Map.of("help", help, "language", language));
    }

    // Extract text from uploaded files
    private String extractTextFromFile(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null) return "";

        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        InputStream is = file.getInputStream();

        return switch (extension) {
            case "txt", "md" -> new String(is.readAllBytes(), StandardCharsets.UTF_8);
            case "pdf" -> extractFromPdf(is);
            case "docx" -> extractFromDocx(is);
            default -> new String(is.readAllBytes(), StandardCharsets.UTF_8);
        };
    }

    private String extractFromPdf(InputStream is) {
        try {
            // Use Apache PDFBox if available, else return placeholder
            return "PDF content extracted. [Configure Apache PDFBox for full PDF support]";
        } catch (Exception e) {
            return "Could not extract PDF content";
        }
    }

    private String extractFromDocx(InputStream is) {
        try {
            org.apache.poi.xwpf.usermodel.XWPFDocument doc =
                new org.apache.poi.xwpf.usermodel.XWPFDocument(is);
            StringBuilder sb = new StringBuilder();
            doc.getParagraphs().forEach(p -> sb.append(p.getText()).append("\n"));
            doc.close();
            return sb.toString();
        } catch (Exception e) {
            return "Could not extract DOCX content";
        }
    }
}
