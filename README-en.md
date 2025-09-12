# 🚀 OOP Course Schedule Project - Polban 2025

Welcome to the Course Schedule Project repository! This project was created to fulfill an assignment for the **Object-Oriented Programming (OOP)** course at Politeknik Negeri Bandung.

This application is a simple Command-Line Interface (CLI) that allows users to view and search for course schedules based on existing data. It's simple, fast, and straight to the point!

## ✨ Cool Features

This application has several main features:

1.  **Search Schedule by Class**: Instantly view the complete schedule for a specific class. You can also filter by day, so you don't have to scroll endlessly.
2.  **View Class List**: Forgot your class code? No worries, there's a list of all available classes in the system.
3.  **Data Summary**: Curious about the lecturers or the courses offered? This feature displays a summary of lecturers, classes, and their students.

## 🛠️ Tech Stack

This project is built with fundamental technologies, focusing on the application of OOP concepts:

-   **Java**: The primary language used, of course.
-   **OOP Principles**: Implements core concepts like *Encapsulation*, *Association*, and *Composition* to model entities such as Mahasiswa (Student), Dosen (Lecturer), and Jadwal (Schedule).
-   **No External Libraries**: Built purely with the standard Java Development Kit (JDK) to maximize understanding of the basics.

## 📂 Project Structure

-   `src/`: Contains all the Java source code, organized into several packages:
    -   `app`: The application's entry point (Main.java).
    -   `model`: Classes that represent the data models (Dosen, Mahasiswa, etc.).
    -   `service`: Business logic, such as loading data from CSVs and search functionality.
-   `data/`: A collection of `.csv` files that act as the "database" for this application.

## ⚠️ A Note on Data

The data used in this project (`dosen.csv`, `mahasiswa.csv`, `jadwal.csv`, `matakuliah.csv`) is **real course data from Polban**. However, to protect privacy, **all personal identities, such as the names of lecturers and students, have been anonymized**. So, don't be surprised if you find some unique or funny names!

## 💻 How to Run

Can't wait to try it out? Follow these steps:

1.  **Compile the Code**
    Open your terminal or command prompt, then run the command below from the project's root directory:

    ```bash
    # For Linux/macOS users
    ./build.sh

    # Or manually
    javac -d out src/id/ac/polban/**/*.java
    ```

2.  **Run the Application**
    Once compiled successfully, run the application with this command:

    ```bash
    # For Linux/macOS users
    ./run.sh

    # Or manually
    java -cp out id.ac.polban.app.Main
    ```

After that, an interactive menu will appear in your terminal. Enjoy!

## 📦 Releases

The compiled JAR file (`AkademikByArief.jar`) will be uploaded to the [Releases](https://github.com/riefproject/25IF2113-OOP-ClassroomTimetable/releases) section of this repository with every update.

## License

This project is licensed under the [MIT License](LICENSE).

---

Made with passion and a little bit of caffeine. Hope you find it useful! 😉
