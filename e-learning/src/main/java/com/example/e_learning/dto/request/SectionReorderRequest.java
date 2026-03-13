package com.example.e_learning.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SectionReorderRequest {

    @NotEmpty(message = "Section IDs list must not be empty")
    private List<Long> sectionIds;
}

