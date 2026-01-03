package com.TaskTracker.TaskTracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class TaskTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskTrackerApplication.class, args);
	}

	@Bean
	// 1. The Trigger: CommandLineRunner PENTING
	public CommandLineRunner commandLineRunner(TaskService taskService) {
		return args -> {
			Scanner scanner = new Scanner(System.in);
			boolean running = true;

			System.out.println("=== Task Tracker CLI ===");
			System.out.println("Aplikasi untuk melacak dan mengelola tugas Anda");
			System.out.println();

			// 2. The Heartbeat: Loop while (running) PENTING
			while (running) {
				printMenu();
				System.out.print("Pilih menu (1-8): ");
				String choice = scanner.nextLine().trim(); // 3. The Input: Scanner PENTING

				switch (choice) { // 4. The Decision: Switch Statement PENTING
					case "1":
						addTask(taskService, scanner);
						break;
					case "2":
						viewAllTasks(taskService);
						break;
					case "3":
						viewTasksByStatus(taskService, scanner);
						break;
					case "4":
						updateTaskStatus(taskService, scanner);
						break;
					case "5":
						updateTask(taskService, scanner);
						break;
					case "6":
						deleteTask(taskService, scanner);
						break;
					case "7":
						viewTaskDetails(taskService, scanner);
						break;
					case "8":
						running = false;
						System.out.println("Terima kasih telah menggunakan Task Tracker!");
						break;
					default:
						System.out.println("Pilihan tidak valid. Silakan coba lagi.");
				}

				if (running) {
					System.out.println("\nTekan Enter untuk melanjutkan...");
					scanner.nextLine();
				}
			}

			scanner.close();
		};
	}

	private static void printMenu() {
		System.out.println("\n--- Menu Utama ---");
		System.out.println("1. Tambah tugas baru");
		System.out.println("2. Lihat semua tugas");
		System.out.println("3. Lihat tugas berdasarkan status");
		System.out.println("4. Ubah status tugas");
		System.out.println("5. Ubah detail tugas");
		System.out.println("6. Hapus tugas");
		System.out.println("7. Lihat detail tugas");
		System.out.println("8. Keluar");
	}

	private static void addTask(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Tambah Tugas Baru ---");
		System.out.print("Judul tugas: ");
		String title = scanner.nextLine().trim();

		if (title.isEmpty()) {
			System.out.println("Judul tidak boleh kosong!");
			return;
		}

		System.out.print("Deskripsi tugas: ");
		String description = scanner.nextLine().trim();

		Task task = taskService.createTask(title, description); // 5. The Action: Create Task PENTING
		System.out.println("✓ Tugas berhasil ditambahkan dengan ID: " + task.getId());
	}

	private static void viewAllTasks(TaskService taskService) {
		System.out.println("\n--- Semua Tugas ---");
		List<Task> tasks = taskService.getAllTasks(); // 6. The Action: Get All Tasks PENTING

		if (tasks.isEmpty()) {
			System.out.println("Belum ada tugas. Tambahkan tugas terlebih dahulu.");
			return;
		}

		printTaskTable(tasks);
	}

	private static void viewTasksByStatus(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Lihat Tugas Berdasarkan Status ---");
		System.out.println("1. TODO");
		System.out.println("2. IN_PROGRESS");
		System.out.println("3. DONE");
		System.out.print("Pilih status (1-3): ");

		String choice = scanner.nextLine().trim();
		Task.TaskStatus status;

		switch (choice) {
			case "1":
				status = Task.TaskStatus.TODO;
				break;
			case "2":
				status = Task.TaskStatus.IN_PROGRESS;
				break;
			case "3":
				status = Task.TaskStatus.DONE;
				break;
			default:
				System.out.println("Pilihan tidak valid!");
				return;
		}

		List<Task> tasks = taskService.getTasksByStatus(status);
		if (tasks.isEmpty()) {
			System.out.println("Tidak ada tugas dengan status " + status);
			return;
		}

		System.out.println("\n--- Tugas dengan Status " + status + " ---");
		printTaskTable(tasks);
	}

	private static void updateTaskStatus(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Ubah Status Tugas ---");
		viewAllTasks(taskService);

		if (taskService.getAllTasks().isEmpty()) {
			return;
		}

		System.out.print("\nMasukkan ID tugas yang ingin diubah: ");
		String idStr = scanner.nextLine().trim();

		try {
			Long id = Long.parseLong(idStr);

			System.out.println("\nPilih status baru:");
			System.out.println("1. TODO");
			System.out.println("2. IN_PROGRESS");
			System.out.println("3. DONE");
			System.out.print("Pilih status (1-3): ");

			String choice = scanner.nextLine().trim();
			Task.TaskStatus status;

			switch (choice) {
				case "1":
					status = Task.TaskStatus.TODO;
					break;
				case "2":
					status = Task.TaskStatus.IN_PROGRESS;
					break;
				case "3":
					status = Task.TaskStatus.DONE;
					break;
				default:
					System.out.println("Pilihan tidak valid!");
					return;
			}

			if (taskService.updateTaskStatus(id, status)) {
				System.out.println("✓ Status tugas berhasil diubah!");
			} else {
				System.out.println("✗ Tugas dengan ID " + id + " tidak ditemukan!");
			}
		} catch (NumberFormatException e) {
			System.out.println("ID harus berupa angka!");
		}
	}

	private static void updateTask(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Ubah Detail Tugas ---");
		viewAllTasks(taskService);

		if (taskService.getAllTasks().isEmpty()) {
			return;
		}

		System.out.print("\nMasukkan ID tugas yang ingin diubah: ");
		String idStr = scanner.nextLine().trim();

		try {
			Long id = Long.parseLong(idStr);

			if (!taskService.getTaskById(id).isPresent()) {
				System.out.println("Tugas dengan ID " + id + " tidak ditemukan!");
				return;
			}

			System.out.print("Judul baru (Enter untuk tetap sama): ");
			String title = scanner.nextLine().trim();
			if (title.isEmpty())
				title = null;

			System.out.print("Deskripsi baru (Enter untuk tetap sama): ");
			String description = scanner.nextLine().trim();
			if (description.isEmpty())
				description = null;

			System.out.println("\nPilih status baru:");
			System.out.println("1. TODO");
			System.out.println("2. IN_PROGRESS");
			System.out.println("3. DONE");
			System.out.println("4. Tetap sama");
			System.out.print("Pilih status (1-4): ");

			String choice = scanner.nextLine().trim();
			Task.TaskStatus status = null;

			switch (choice) {
				case "1":
					status = Task.TaskStatus.TODO;
					break;
				case "2":
					status = Task.TaskStatus.IN_PROGRESS;
					break;
				case "3":
					status = Task.TaskStatus.DONE;
					break;
				case "4":
					break;
				default:
					System.out.println("Pilihan tidak valid!");
					return;
			}

			if (taskService.updateTask(id, title, description, status)) {
				System.out.println("✓ Tugas berhasil diubah!");
			} else {
				System.out.println("✗ Gagal mengubah tugas!");
			}
		} catch (NumberFormatException e) {
			System.out.println("ID harus berupa angka!");
		}
	}

	private static void deleteTask(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Hapus Tugas ---");
		viewAllTasks(taskService);

		if (taskService.getAllTasks().isEmpty()) {
			return;
		}

		System.out.print("\nMasukkan ID tugas yang ingin dihapus: ");
		String idStr = scanner.nextLine().trim();

		try {
			Long id = Long.parseLong(idStr);

			if (taskService.deleteTask(id)) {
				System.out.println("✓ Tugas berhasil dihapus!");
			} else {
				System.out.println("✗ Tugas dengan ID " + id + " tidak ditemukan!");
			}
		} catch (NumberFormatException e) {
			System.out.println("ID harus berupa angka!");
		}
	}

	private static void viewTaskDetails(TaskService taskService, Scanner scanner) {
		System.out.println("\n--- Lihat Detail Tugas ---");
		viewAllTasks(taskService);

		if (taskService.getAllTasks().isEmpty()) {
			return;
		}

		System.out.print("\nMasukkan ID tugas yang ingin dilihat: ");
		String idStr = scanner.nextLine().trim();

		try {
			Long id = Long.parseLong(idStr);
			Task task = taskService.getTaskById(id).orElse(null);

			if (task != null) {
				System.out.println("\n--- Detail Tugas ID: " + id + " ---");
				System.out.println("Judul: " + task.getTitle());
				System.out.println("Deskripsi: " + task.getDescription());
				System.out.println("Status: " + task.getStatus());
				System.out.println("Dibuat: " + task.getCreatedAt());
				System.out.println("Diupdate: " + task.getUpdatedAt());
			} else {
				System.out.println("✗ Tugas dengan ID " + id + " tidak ditemukan!");
			}
		} catch (NumberFormatException e) {
			System.out.println("ID harus berupa angka!");
		}
	}

	private static void printTaskTable(List<Task> tasks) {
		System.out.println("ID  | Status       | Judul");
		System.out.println("----|--------------|---------------------------");
		for (Task task : tasks) {
			String statusStr = String.format("%-12s", task.getStatus());
			String titleStr = task.getTitle().length() > 25 ? task.getTitle().substring(0, 22) + "..."
					: task.getTitle();
			System.out.printf("%-3d | %-12s | %s%n", task.getId(), statusStr, titleStr);
		}
	}
}
