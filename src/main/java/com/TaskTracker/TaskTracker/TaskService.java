package com.TaskTracker.TaskTracker;

import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private static final String FILE_NAME = "tasks.txt";
    private List<Task> tasks; // 1. Aplikasi: In-Memory Database (List) PENTING
    private AtomicLong idCounter; // 2. Manajemen Identitas Unik (ID Generator) PENTING
    private boolean isInitialized = false; // 3. Inisialisasi

    public TaskService() {
        this.tasks = new ArrayList<>();
        this.idCounter = new AtomicLong(1);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Task task : tasks) {
                writer.write(formatTask(task));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // 8. Memuat Task dari File
    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTask(line);
                if (task != null) {
                    tasks.add(task); // 8.1. Menambahkan Task ke List PENTING
                    if (task.getId() >= idCounter.get()) {
                        idCounter.set(task.getId() + 1); // 8.2. Mengatur ID Counter PENTING
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
        }
    }

    // 9. Format Task PENTING PENTING
    private String formatTask(Task task) {
        return String.format("%d|%s|%s|%s|%s|%s",
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    // 10. Parse Task
    private Task parseTask(String line) {
        try {
            String[] parts = line.split("\\|", 6);
            if (parts.length == 6) {
                Task task = new Task();
                task.setId(Long.parseLong(parts[0]));
                task.setTitle(parts[1]);
                task.setDescription(parts[2]);
                task.setStatus(Task.TaskStatus.valueOf(parts[3]));
                task.setCreatedAt(LocalDateTime.parse(parts[4]));
                task.setUpdatedAt(LocalDateTime.parse(parts[5]));
                return task;
            }
        } catch (Exception e) {
            System.err.println("Error parsing task: " + line);
        }
        return null;
    }
}