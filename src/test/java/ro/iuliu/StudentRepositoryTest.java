package ro.iuliu;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StudentRepositoryTest {

  @Test
  void shouldAddStudent() {
    // Given
    StudentRepository repository = new StudentRepository();
    Student student = new Student("Ana", "Pop", LocalDate.of(2000, 5, 10), "F", "cnp-1");

    // When
    repository.addStudent(student);

    // Then
    assertEquals(List.of(student), repository.listStudents("lastName"));
  }

  @Test
  void shouldRejectDuplicateCnpAndKeepOriginalStudent() {
    // Given
    StudentRepository repository = new StudentRepository();
    Student original = new Student("Ana", "Pop", LocalDate.of(2000, 5, 10), "F", "cnp-1");
    Student duplicate = new Student("Ion", "Marin", LocalDate.of(1999, 3, 15), "M", "cnp-1");
    repository.addStudent(original);

    // When
    assertThrows(IllegalArgumentException.class, () -> repository.addStudent(duplicate));

    // Then
    assertEquals(List.of(original), repository.listStudents("lastName"));
  }

  @Test
  void shouldDeleteStudentByCnp() {
    // Given
    StudentRepository repository = new StudentRepository();
    Student student = new Student("Ana", "Pop", LocalDate.of(2000, 5, 10), "F", "cnp-1");
    repository.addStudent(student);

    // When
    repository.deleteStudent("cnp-1");

    // Then
    assertTrue(repository.listStudents("lastName").isEmpty());
  }

  @Test
  void shouldRejectBlankCnpWhenDeleting() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> repository.deleteStudent(" "));

    // Then
    assertEquals("CNP is required.", exception.getMessage());
  }

  @Test
  void shouldThrowWhenStudentDoesNotExist() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    NoSuchElementException exception =
        assertThrows(NoSuchElementException.class, () -> repository.deleteStudent("cnp-99"));

    // Then
    assertEquals("Student not found.", exception.getMessage());
  }

  @Test
  void shouldFindAllStudentsWithRequestedAge() {
    // Given
    StudentRepository repository = new StudentRepository();
    LocalDate today = LocalDate.now();
    Student ana = new Student("Ana", "Pop", today.minusYears(25), "F", "cnp-1");
    Student ion = new Student("Ion", "Marin", today.minusYears(25), "M", "cnp-2");
    Student maria = new Student("Maria", "Dinu", today.minusYears(30), "F", "cnp-3");
    repository.addStudent(ana);
    repository.addStudent(ion);
    repository.addStudent(maria);

    // When
    List<Student> students = repository.findStudentsByAge("25");

    // Then
    assertEquals(List.of(ana, ion), students);
  }

  @Test
  void shouldRejectNonNumericAge() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    Executable search = () -> repository.findStudentsByAge("abc");

    // Then
    assertThrows(NumberFormatException.class, search);
  }

  @Test
  void shouldRejectNegativeAge() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> repository.findStudentsByAge("-1"));

    // Then
    assertEquals("Age cannot be negative.", exception.getMessage());
  }

  @Test
  void shouldSortStudentsByLastName() {
    // Given
    StudentRepository repository = new StudentRepository();
    Student ana = new Student("Ana", "Pop", LocalDate.of(1999, 3, 15), "F", "cnp-1");
    Student ion = new Student("Ion", "andrei", LocalDate.of(2000, 5, 10), "M", "cnp-2");
    repository.addStudent(ana);
    repository.addStudent(ion);

    // When
    List<Student> students = repository.listStudents("lastName");

    // Then
    assertEquals(List.of(ion, ana), students);
  }

  @Test
  void shouldSortStudentsByDateOfBirth() {
    // Given
    StudentRepository repository = new StudentRepository();
    Student younger = new Student("Ana", "Andrei", LocalDate.of(1999, 5, 10), "F", "cnp-1");
    Student older = new Student("Ion", "Pop", LocalDate.of(1999, 3, 15), "M", "cnp-2");
    repository.addStudent(younger);
    repository.addStudent(older);

    // When
    List<Student> students = repository.listStudents("dateOfBirth");

    // Then
    assertEquals(List.of(older, younger), students);
  }

  @Test
  void shouldRejectBlankSortCriterion() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> repository.listStudents(" "));

    // Then
    assertEquals("Sort criterion is required.", exception.getMessage());
  }

  @Test
  void shouldRejectUnknownSortCriterion() {
    // Given
    StudentRepository repository = new StudentRepository();

    // When
    Executable sort = () -> repository.listStudents("firstName");

    // Then
    assertThrows(IllegalArgumentException.class, sort);
  }

  @Test
  void shouldRejectBlankFirstName() {
    // Given
    LocalDate birthDate = LocalDate.of(2000, 5, 10);

    // When
    Executable createStudent = () -> new Student("", "Pop", birthDate, "F", "cnp-1");

    // Then
    assertThrows(IllegalArgumentException.class, createStudent);
  }

  @Test
  void shouldRejectBlankLastName() {
    // Given
    LocalDate birthDate = LocalDate.of(2000, 5, 10);

    // When
    Executable createStudent = () -> new Student("Ana", " ", birthDate, "F", "cnp-1");

    // Then
    assertThrows(IllegalArgumentException.class, createStudent);
  }

  @Test
  void shouldRejectBirthYearBefore1900() {
    // Given
    LocalDate birthDate = LocalDate.of(1899, 12, 31);

    // When
    Executable createStudent = () -> new Student("Ana", "Pop", birthDate, "F", "cnp-1");

    // Then
    assertThrows(IllegalArgumentException.class, createStudent);
  }

  @Test
  void shouldRejectBirthYearAfterMaximum() {
    // Given
    int firstInvalidYear = LocalDate.now().getYear() - 17;
    LocalDate birthDate = LocalDate.of(firstInvalidYear, 1, 1);

    // When
    Executable createStudent = () -> new Student("Ana", "Pop", birthDate, "F", "cnp-1");

    // Then
    assertThrows(IllegalArgumentException.class, createStudent);
  }

  @Test
  void shouldAcceptLatestAllowedBirthYear() {
    // Given
    int latestAllowedYear = LocalDate.now().getYear() - 18;
    LocalDate birthDate = LocalDate.of(latestAllowedYear, 12, 31);

    // When
    Student student = new Student("Ana", "Pop", birthDate, "F", "cnp-1");

    // Then
    assertEquals(birthDate, student.getDateOfBirth());
  }

  @Test
  void shouldRejectInvalidGender() {
    // Given
    LocalDate birthDate = LocalDate.of(2000, 5, 10);

    // When
    Executable createStudent = () -> new Student("Ana", "Pop", birthDate, "X", "cnp-1");

    // Then
    assertThrows(IllegalArgumentException.class, createStudent);
  }

  @ParameterizedTest
  @ValueSource(strings = {"m", "f", "mAlE", "fEmAlE"})
  void shouldAcceptGenderRegardlessOfCase(String gender) {
    // Given
    LocalDate birthDate = LocalDate.of(2000, 5, 10);

    // When
    Student student = new Student("Ana", "Pop", birthDate, gender, "cnp-1");

    // Then
    assertEquals(gender, student.getGender());
  }
}
