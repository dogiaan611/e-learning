package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SectionRequest {

    @NotBlank(message = "Section title is required")
    @Size(min = 1, max = 255, message = "Section title must be between 1 and 255 characters")
    private String title;
}

