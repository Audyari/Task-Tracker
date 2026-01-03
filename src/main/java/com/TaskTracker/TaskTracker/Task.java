package com.TaskTracker.TaskTracker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    // Struktur Data (Atribut)
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Keamanan Status (Enum)
    public enum TaskStatus {
        TODO,
        IN_PROGRESS,
        DONE
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.TODO; // Default Status
        this.createdAt = LocalDateTime.now(); // Waktu Penciptaan
        this.updatedAt = LocalDateTime.now(); // Waktu Update
    }
}