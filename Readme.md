# Task Tracker Application

Sebuah aplikasi command-line untuk mengelola task dengan fitur CRUD (Create, Read, Update, Delete) yang menyimpan data dalam format JSON.

## 🔧 Perintah Dasar

### Menjalankan Aplikasi

```bash
mvnw.cmd spring-boot:run
```

Digunakan untuk menjalankan aplikasi Spring Boot.

### Mengkompilasi Aplikasi

```bash
mvnw.cmd clean compile
```

Digunakan untuk mengkompilasi kode sumber aplikasi.

## 📋 Fitur-Fitur

### 1. **Manajemen Task Lengkap**

- **Create**: Membuat task baru dengan judul dan deskripsi
- **Read**: Melihat semua task atau task berdasarkan ID
- **Update**: Mengubah judul, deskripsi, atau status task
- **Delete**: Menghapus task dari sistem

### 2. **Status Task**

- `TODO` - Task belum dimulai
- `IN_PROGRESS` - Task sedang dikerjakan
- `DONE` - Task selesai

### 3. **Penyimpanan Data Otomatis**

- Data tersimpan otomatis di file `tasks.json`
- File dibuat otomatis jika belum ada
- Format JSON yang mudah dibaca

### 4. **Manajemen ID Unik**

- Sistem ID counter otomatis
- Menghindari duplikasi ID
- Melacak ID tertinggi

### 5. **Timestamp Otomatis**

- `createdAt` - Waktu pembuatan task
- `updatedAt` - Waktu terakhir update task
- Format ISO 8601

### 6. **In-Memory Database**

- Menggunakan List sebagai database sementara
- Cepat dan efisien
- Data persisten melalui file JSON

## 📁 Struktur File

```
Task Tracker/
├── tasks.json              # File penyimpanan data
├── pom.xml                 # Konfigurasi Maven
├── Readme.md              # Dokumentasi
└── src/
    └── main/
        └── java/
            └── com/
                └── TaskTracker/
                    └── TaskTracker/
                        ├── Task.java           # Model data
                        ├── TaskService.java    # Logic bisnis
                        └── TaskTrackerApplication.java  # Main class
```

## 🚀 Link Referensi

- [Roadmap.sh - Task Tracker Project](https://roadmap.sh/projects/task-tracker)

## 💻 Teknologi

- Java 17+
- Spring Boot 3.x
- Jackson (JSON processing)
- Maven

## 📝 Contoh Penggunaan

Setelah menjalankan aplikasi, Anda dapat menggunakan command berikut:

- Create task: `create "Judul Task" "Deskripsi"`
- View all tasks: `view`
- View task by ID: `view 1`
- Update task: `update 1 "Judul Baru" "Deskripsi Baru"`
- Update status: `status 1 DONE`
- Delete task: `delete 1`

---

_Project ini dibuat untuk memenuhi requirement dari roadmap.sh_
