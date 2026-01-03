package com.TaskTracker.TaskTracker;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final String FILE_NAME = "tasks.json";
    private List<Task> tasks; // 1. Aplikasi: In-Memory Database (List) PENTING
    private AtomicLong idCounter; // 2. Manajemen Identitas Unik (ID Generator) PENTING
    private boolean isInitialized = false; // 3. Inisialisasi
    private ObjectMapper objectMapper; // 4. JSON Serializer

    public TaskService() {
        this.tasks = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private void initialize() {
        if (!isInitialized) {
            loadTasks();
            isInitialized = true;
        }
    }

    public List<Task> getAllTasks() {
        initialize();
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        initialize();
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Optional<Task> getTaskById(Long id) {
        initialize();
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    // 4. Membuat Task Baru PENTING
    public Task createTask(String title, String description) {
        initialize();
        Task task = new Task(title, description);
        task.setId(idCounter.getAndIncrement()); // 4.1. Manajemen Identitas Unik (ID Generator) PENTING
        task.setCreatedAt(LocalDateTime.now()); // 4.2. Waktu Penciptaan PENTING
        task.setUpdatedAt(LocalDateTime.now()); // 4.3. Waktu Update PENTING
        tasks.add(task); // 5. Menambahkan Task ke List PENTING
        saveTasks(); // 6. Menyimpan Task ke File PENTING
        return task;
    }

    // 7. Mengubah Status Task
    public boolean updateTaskStatus(Long id, Task.TaskStatus status) {
        initialize();
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            saveTasks();
            return true;
        }
        return false;
    }

    public boolean updateTask(Long id, String title, String description, Task.TaskStatus status) {
        initialize();
        Optional<Task> taskOpt = getTaskById(id);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            if (title != null && !title.isEmpty())
                task.setTitle(title);
            if (description != null && !description.isEmpty())
                task.setDescription(description);
            if (status != null)
                task.setStatus(status);
            task.setUpdatedAt(LocalDateTime.now());
            saveTasks();
            return true;
        }
        return false;
    }

    public boolean deleteTask(Long id) {
        initialize();
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (removed) {
            saveTasks();
        }
        return removed;
    }

    private void saveTasks() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // 8. Memuat Task dari File JSON
    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try {
            List<Task> loadedTasks = objectMapper.readValue(file, new TypeReference<List<Task>>() {
            });
            if (loadedTasks != null) {
                tasks.addAll(loadedTasks);
                // Update idCounter based on the highest ID found
                for (Task task : loadedTasks) {
                    if (task.getId() >= idCounter.get()) {
                        idCounter.set(task.getId() + 1);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

}