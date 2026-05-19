package com.elms.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiAIService {
/*
    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
*/
@Value("${groq.api.url}")
private String apiUrl;

  @Value("${groq.api.key}")
  private String apiKey;

  @Value("${groq.model}")
  private String model;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;
/*
    // ── Core Gemini API Call ───────────────────────────────────────────────────
    public String generateContent(String prompt) {
        try {
            WebClient client = webClientBuilder.build();

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of(
                    "parts", List.of(Map.of("text", prompt))
                )),
                "generationConfig", Map.of(
                    "temperature", 0.7,
                    "topK", 40,
                    "topP", 0.95,
                    "maxOutputTokens", 4096
                )
            );

            String response = client.post()
                .uri(geminiApiUrl + "?key=" + geminiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates").get(0)
                       .path("content").path("parts").get(0)
                       .path("text").asText();
        } catch (Exception e) {
            log.error("Gemini AI call failed: {}", e.getMessage());
            return "AI service temporarily unavailable. Please try again.";
        }
    }
*/

  // ── Core Groq API Call ───────────────────────────────────────────────────
  public String generateContent(String prompt) {

    try {

      WebClient client = webClientBuilder.build();

      // Groq/OpenAI compatible request body
      Map<String, Object> requestBody = Map.of(
        "model", model,
        "messages", List.of(
          Map.of(
            "role", "user",
            "content", prompt
          )
        ),
        "temperature", 0.7,
        "max_tokens", 2048
      );

      String response = client.post()
        .uri(apiUrl)
        .header("Authorization", "Bearer " + apiKey)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(requestBody)
        .retrieve()
        .onStatus(
          status -> status.isError(),
          error -> error.bodyToMono(String.class)
            .flatMap(body -> {
              log.error("Groq API Error Response: {}", body);
              return Mono.error(new RuntimeException(body));
            })
        )
        .bodyToMono(String.class)
        .block();

      JsonNode root = objectMapper.readTree(response);

      JsonNode choices = root.path("choices");

      if (!choices.isArray() || choices.size() == 0) {

        log.error("No choices returned from Groq");

        return """
                {
                  "success": false,
                  "message": "AI service returned empty response"
                }
                """;
      }

      String text = choices.get(0)
        .path("message")
        .path("content")
        .asText();

      // Remove markdown wrappers
      text = text.replace("```json", "")
        .replace("```", "")
        .trim();

      return text;

    } catch (Exception e) {

      log.error("Groq AI call failed", e);

      return """
            {
              "success": false,
              "message": "AI service temporarily unavailable"
            }
            """;
    }
  }
    // ── 1. Generate MCQ Questions ─────────────────────────────────────────────
    public String generateMCQQuestions(String skill, String topic, int count, String difficulty) {
        String prompt = String.format("""
            You are an expert educator. Generate %d multiple-choice questions for the topic "%s"
            in the skill area "%s" at %s difficulty level.

            Return ONLY a valid JSON array with this exact structure:
            [
              {
                "id": 1,
                "question": "Question text here",
                "options": ["Option A", "Option B", "Option C", "Option D"],
                "correctAnswer": 0,
                "explanation": "Why this answer is correct",
                "difficulty": "%s",
                "skill": "%s"
              }
            ]

            Make questions practical, real-world, and industry-relevant.
            correctAnswer is the 0-based index of the correct option.
            Return ONLY the JSON array, no markdown, no extra text.
            """, count, topic, skill, difficulty, difficulty, skill);

        return generateContent(prompt);
    }

    // ── 2. Generate Coding Assessment ─────────────────────────────────────────
    public String generateCodingAssessment(String skill, String language, String difficulty) {
        String prompt = String.format("""
            You are a senior software engineer. Create a coding assessment problem for %s using %s
            at %s difficulty level.

            Return ONLY valid JSON with this structure:
            {
              "title": "Problem title",
              "description": "Detailed problem description",
              "constraints": "Input/output constraints",
              "examples": [
                {"input": "example input", "output": "expected output", "explanation": "why"}
              ],
              "starterCode": "// Write your solution here\\nfunction solution(input) {\\n  // your code\\n}",
              "testCases": [
                {"input": "test1", "expectedOutput": "result1", "hidden": false},
                {"input": "test2", "expectedOutput": "result2", "hidden": true}
              ],
              "hints": ["Hint 1", "Hint 2"],
              "timeLimit": 30,
              "skill": "%s",
              "language": "%s",
              "difficulty": "%s"
            }
            Return ONLY the JSON, no markdown.
            """, skill, language, difficulty, skill, language, difficulty);

        return generateContent(prompt);
    }

    // ── 3. Evaluate Coding Submission ─────────────────────────────────────────
    public String evaluateCodeSubmission(String code, String problem, String language) {
        String prompt = String.format("""
            You are an expert code reviewer. Evaluate this %s code submission.

            Problem: %s

            Submitted Code:
            ```%s
            %s
            ```

            Return ONLY valid JSON:
            {
              "score": 85,
              "passed": true,
              "correctness": "Analysis of correctness",
              "efficiency": "Time/space complexity analysis",
              "codeQuality": "Code style and best practices",
              "suggestions": ["Improvement 1", "Improvement 2"],
              "overallFeedback": "Comprehensive feedback",
              "testResults": [
                {"testCase": "input", "passed": true, "output": "actual output"}
              ]
            }
            Return ONLY the JSON.
            """, language, problem, language, code);

        return generateContent(prompt);
    }

    // ── 4. Generate Interview Questions ───────────────────────────────────────
    public String generateInterviewQuestions(String role, String skills, int count) {
        String prompt = String.format("""
            You are an expert technical interviewer. Generate %d interview questions for
            a %s position requiring skills in %s.

            Mix question types: technical (40%%), behavioral (30%%), situational (30%%).

            Return ONLY valid JSON array:
            [
              {
                "id": 1,
                "question": "Interview question",
                "type": "TECHNICAL",
                "expectedKeyPoints": ["key point 1", "key point 2"],
                "followUpQuestions": ["Follow up 1"],
                "difficulty": "MEDIUM",
                "skill": "relevant skill"
              }
            ]
            Return ONLY the JSON array.
            """, count, role, skills);

        return generateContent(prompt);
    }

    // ── 5. Evaluate Interview Answer ──────────────────────────────────────────
    public String evaluateInterviewAnswer(String question, String answer, String expectedKeyPoints) {
        String prompt = String.format("""
            You are an expert interviewer. Evaluate this interview answer.

            Question: %s
            Expected Key Points: %s
            Candidate's Answer: %s

            Return ONLY valid JSON:
            {
              "score": 75,
              "relevance": "How relevant the answer is",
              "completeness": "What key points were covered",
              "communication": "Communication quality assessment",
              "strengths": ["Strength 1"],
              "improvements": ["Area to improve"],
              "feedback": "Detailed constructive feedback"
            }
            Return ONLY the JSON.
            """, question, expectedKeyPoints, answer);

        return generateContent(prompt);
    }

    // ── 6. Analyze Emotion Data ───────────────────────────────────────────────
    public String analyzeEmotionData(String emotionDataJson, String context) {
        String prompt = String.format("""
            You are an expert behavioral psychologist and interview coach.
            Analyze the following emotion detection data from an interview session.

            Emotion Timeline Data: %s
            Interview Context: %s

            Provide psychological insights and return ONLY valid JSON:
            {
              "dominantEmotion": "CONFIDENT",
              "emotionBreakdown": {
                "confident": 45,
                "neutral": 30,
                "nervous": 15,
                "happy": 10
              },
              "stressLevel": "LOW",
              "engagementScore": 85,
              "authenticityScore": 90,
              "insights": ["Candidate appeared confident during technical questions"],
              "concerns": ["Brief nervousness when asked about experience"],
              "recommendation": "HIRE",
              "overallAssessment": "Comprehensive behavioral assessment"
            }
            Return ONLY the JSON.
            """, emotionDataJson, context);

        return generateContent(prompt);
    }

    // ── 7. Generate Final Interview Report ────────────────────────────────────
    public String generateInterviewReport(String answersJson, String emotionAnalysis, String role) {
        String prompt = String.format("""
            You are a senior HR professional. Generate a comprehensive interview report for
            a %s position candidate.

            Interview Answers & Scores: %s
            Emotion Analysis: %s

            Return ONLY valid JSON:
            {
              "overallScore": 82,
              "recommendation": "HIRE",
              "technicalScore": 85,
              "behavioralScore": 78,
              "communicationScore": 80,
              "summary": "Executive summary of the interview",
              "strengths": ["Major strength 1", "Major strength 2"],
              "developmentAreas": ["Area 1", "Area 2"],
              "culturalFit": "Assessment of cultural fit",
              "riskFactors": ["Any concerns"],
              "nextSteps": ["Recommended next steps"],
              "detailedFeedback": "Comprehensive paragraph feedback"
            }
            Return ONLY the JSON.
            """, role, answersJson, emotionAnalysis);

        return generateContent(prompt);
    }

    // ── 8. AI Q&A Chatbot ─────────────────────────────────────────────────────
    public String chatbotResponse(String userQuery, String context, String conversationHistory) {
        String prompt = String.format("""
            You are an intelligent AI assistant for an E-Learning Management System (ELMS).
            You help students, instructors, and professionals with:
            - Learning paths and course recommendations
            - Technical concepts and explanations
            - Programming help and code review
            - Career guidance and interview preparation
            - Document analysis and summarization
            - Platform navigation and support

            Conversation History: %s

            Context (if document/voice provided): %s

            User Query: %s

            Provide a helpful, accurate, and concise response. If it's a coding question,
            include code examples. If it's conceptual, use analogies. Always be encouraging
            and supportive for learners.
            """, conversationHistory, context, userQuery);

        return generateContent(prompt);
    }

    // ── 9. Analyze Uploaded Document ──────────────────────────────────────────
    public String analyzeDocument(String documentContent, String userQuery) {
        String prompt = String.format("""
            You are an expert document analyzer. Analyze the following document content
            and answer the user's query.

            Document Content:
            %s

            User Query: %s

            Provide a comprehensive analysis including:
            1. Document summary
            2. Key insights relevant to the query
            3. Important concepts identified
            4. Recommendations or action items
            5. Related topics for further learning
            """, documentContent, userQuery);

        return generateContent(prompt);
    }

    // ── 10. Generate Course Completion Assessment ──────────────────────────────
    public String generateCompletionAssessment(String courseTitle, String topics, int questionCount) {
        String prompt = String.format("""
            Generate a comprehensive final assessment for the course "%s"
            covering these topics: %s

            Create %d questions mixing MCQ (70%%) and short answer (30%%).

            Return ONLY valid JSON:
            {
              "title": "Final Assessment: %s",
              "totalMarks": %d,
              "passingScore": %d,
              "timeLimit": 60,
              "questions": [
                {
                  "id": 1,
                  "type": "MCQ",
                  "question": "Question text",
                  "options": ["A", "B", "C", "D"],
                  "correctAnswer": 0,
                  "marks": 2,
                  "explanation": "Why this is correct"
                }
              ]
            }
            Return ONLY the JSON.
            """, courseTitle, topics, questionCount, courseTitle, questionCount * 2, (int)(questionCount * 2 * 0.7));

        return generateContent(prompt);
    }

    // ── 11. Transcribe Voice to Text ──────────────────────────────────────────
    public String processVoiceQuery(String transcribedText, String context) {
        String prompt = String.format("""
            The following text was transcribed from a voice message by a user in an E-Learning platform.
            Process it naturally and respond helpfully.

            Context: %s
            Voice Message (transcribed): %s

            Respond conversationally as if in a voice dialogue. Keep response concise and clear.
            """, context, transcribedText);

        return generateContent(prompt);
    }

    // ── 12. Smart Course Recommendation ───────────────────────────────────────
    public String recommendCourses(String userSkills, String goals, String completedCourses) {
        String prompt = String.format("""
            You are a learning path advisor. Recommend courses for a learner.

            Current Skills: %s
            Learning Goals: %s
            Completed Courses: %s

            Return ONLY valid JSON:
            {
              "learningPath": "Recommended career path",
              "recommendations": [
                {
                  "title": "Course title",
                  "reason": "Why this course",
                  "priority": "HIGH",
                  "estimatedTime": "4 weeks",
                  "prerequisites": ["Prereq 1"]
                }
              ],
              "skillGaps": ["Missing skill 1", "Missing skill 2"],
              "estimatedCompletion": "3 months"
            }
            Return ONLY the JSON.
            """, userSkills, goals, completedCourses);

        return generateContent(prompt);
    }
}
