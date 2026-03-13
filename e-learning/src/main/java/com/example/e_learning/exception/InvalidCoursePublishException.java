package com.example.e_learning.exception;

import java.util.List;

public class InvalidCoursePublishException extends RuntimeException {

    private final List<String> violations;

    public InvalidCoursePublishException(List<String> violations) {
        super("Course cannot be published due to validation errors");
        this.violations = violations;
    }

    public List<String> getViolations() {
        return violations;
    }
}

