package com.example.e_learning.dto.request;

import lombok.Data;

@Data
public class ProgressUpdateRequest {
    private Boolean isCompleted;
    private Integer lastWatchedTime; // Vị trí video đang xem (giây)
}
