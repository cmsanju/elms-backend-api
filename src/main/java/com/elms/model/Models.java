package com.elms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

// ─── Lesson ───────────────────────────────────────────
@Entity
@Table(name = "lessons")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class Lesson {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT") private String content;
    private String videoUrl;
    private String documentUrl;
    private int orderIndex;
    private String lessonType; // VIDEO, TEXT, QUIZ, CODING
    @ManyToOne @JoinColumn(name = "course_id") private Course course;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}

// ─── Enrollment ───────────────────────────────────────
@Entity
@Table(name = "enrollments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class Enrollment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    @ManyToOne @JoinColumn(name = "course_id") private Course course;
    private int progressPercent;
    private boolean completed;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    @PrePersist protected void onCreate() { enrolledAt = LocalDateTime.now(); }
}

// ─── Assessment ───────────────────────────────────────
@Entity
@Table(name = "assessments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class Assessment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String skill;
    private String assessmentType; // MCQ, CODING, MIXED
    @Column(columnDefinition = "JSON") private String questions; // AI-generated JSON
    @ManyToOne @JoinColumn(name = "course_id") private Course course;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}

// ─── Assessment Result ────────────────────────────────
@Entity
@Table(name = "assessment_results")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class AssessmentResult {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    @ManyToOne @JoinColumn(name = "assessment_id") private Assessment assessment;
    private int score;
    private int totalMarks;
    private boolean passed;
    @Column(columnDefinition = "JSON") private String answers;
    @Column(columnDefinition = "TEXT") private String aiFeedback;
    private LocalDateTime attemptedAt;
    @PrePersist protected void onCreate() { attemptedAt = LocalDateTime.now(); }
}

// ─── Certificate ──────────────────────────────────────
@Entity
@Table(name = "certificates")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class Certificate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String certificateNumber;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    @ManyToOne @JoinColumn(name = "course_id") private Course course;
    private String pdfPath;
    private LocalDateTime issuedAt;
    @PrePersist protected void onCreate() { issuedAt = LocalDateTime.now(); }
}

// ─── Interview Session ────────────────────────────────
@Entity
@Table(name = "interview_sessions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class InterviewSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    private String role;
    private String skills;
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED
    @Column(columnDefinition = "JSON") private String questions;
    @Column(columnDefinition = "JSON") private String answers;
    @Column(columnDefinition = "JSON") private String emotionData;
    private int overallScore;
    @Column(columnDefinition = "TEXT") private String aiFeedback;
    private String recommendation; // HIRE, CONSIDER, REJECT
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
    @PrePersist protected void onCreate() { scheduledAt = LocalDateTime.now(); }
}

// ─── Chat Message ─────────────────────────────────────
@Entity
@Table(name = "chat_messages")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "user_id") private User user;
    private String messageType; // TEXT, VOICE, DOCUMENT
    @Column(columnDefinition = "TEXT") private String userMessage;
    @Column(columnDefinition = "TEXT") private String aiResponse;
    private String documentUrl;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
