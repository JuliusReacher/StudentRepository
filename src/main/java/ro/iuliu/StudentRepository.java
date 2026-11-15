package ro.iuliu;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StudentRepository {
  private static final Logger LOGGER = Logger.getLogger(StudentRepository.class.getName());

  private final Map<String, Student> studentsByCnp = new LinkedHashMap<>();

  public void addStudent(Student student) {
    if (student == null) {
      throw invalidInput("Student is required.");
    }

    Student existingStudent = studentsByCnp.putIfAbsent(student.getCnp(), student);
    if (existingStudent != null) {
      throw invalidInput("A student with this CNP already exists.");
    }

    LOGGER.info("Student added.");
  }

  public void deleteStudent(String cnp) {
    if (cnp == null || cnp.isBlank()) {
      throw invalidInput("CNP is required.");
    }

    Student removedStudent = studentsByCnp.remove(cnp);
    if (removedStudent == null) {
      String message = "Student not found.";
      LOGGER.warning(message);
      throw new NoSuchElementException(message);
    }

    LOGGER.info("Student deleted.");
  }

  public List<Student> findStudentsByAge(String ageInput) {
    int age;
    try {
      age = Integer.parseInt(ageInput);
    } catch (NumberFormatException exception) {
      LOGGER.warning("Age must be a whole number.");
      throw exception;
    }

    if (age < 0) {
      throw invalidInput("Age cannot be negative.");
    }

    List<Student> matchingStudents = new ArrayList<>();
    for (Student student : studentsByCnp.values()) {
      if (student.getAge() == age) {
        matchingStudents.add(student);
      }
    }

    LOGGER.info("Students retrieved by age.");
    return matchingStudents;
  }

  public List<Student> listStudents(String sortBy) {
    if (sortBy == null || sortBy.isBlank()) {
      throw invalidInput("Sort criterion is required.");
    }

    Comparator<Student> comparator;
    if ("lastName".equalsIgnoreCase(sortBy)) {
      comparator = Comparator.comparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER);
    } else if ("dateOfBirth".equalsIgnoreCase(sortBy)) {
      comparator = Comparator.comparing(Student::getDateOfBirth);
    } else {
      throw invalidInput("Sort criterion must be lastName or dateOfBirth.");
    }

    List<Student> sortedStudents = new ArrayList<>(studentsByCnp.values());
    sortedStudents.sort(comparator);

    LOGGER.info("Students listed by " + sortBy + ".");
    return sortedStudents;
  }

  private static IllegalArgumentException invalidInput(String message) {
    LOGGER.warning(message);
    return new IllegalArgumentException(message);
  }
}
