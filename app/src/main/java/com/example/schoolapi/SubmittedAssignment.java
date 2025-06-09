package com.example.schoolapi;

public class SubmittedAssignment {
    String studentName;
    String fileUrl;
    String submittedAt;

    public SubmittedAssignment(String studentName, String fileUrl, String submittedAt) {
        this.studentName = studentName;
        this.fileUrl = fileUrl;
        this.submittedAt = submittedAt;
    }
}
