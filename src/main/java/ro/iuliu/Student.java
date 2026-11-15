package ro.iuliu;

import java.time.LocalDate;
import java.time.Period;

public class Student {
  private final String firstName;
  private final String lastName;
  private final LocalDate dateOfBirth;
  private final String gender;
  private final String cnp;

  public Student(
      String firstName, String lastName, LocalDate dateOfBirth, String gender, String cnp) {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name is required.");
    }

    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name is required.");
    }

    if (dateOfBirth == null) {
      throw new IllegalArgumentException("Date of birth is required.");
    }

    int birthYear = dateOfBirth.getYear();
    int latestBirthYear = LocalDate.now().getYear() - 18;
    if (birthYear < 1900 || birthYear > latestBirthYear) {
      throw new IllegalArgumentException(
          "Birth year must be between 1900 and " + latestBirthYear + ".");
    }

    boolean validGender =
        "M".equalsIgnoreCase(gender)
            || "F".equalsIgnoreCase(gender)
            || "Male".equalsIgnoreCase(gender)
            || "Female".equalsIgnoreCase(gender);
    if (!validGender) {
      throw new IllegalArgumentException("Gender must be M, F, Male or Female.");
    }

    if (cnp == null || cnp.isBlank()) {
      throw new IllegalArgumentException("CNP is required.");
    }

    this.firstName = firstName;
    this.lastName = lastName;
    this.dateOfBirth = dateOfBirth;
    this.gender = gender;
    this.cnp = cnp;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  public String getGender() {
    return gender;
  }

  public String getCnp() {
    return cnp;
  }

  public int getAge() {
    return Period.between(dateOfBirth, LocalDate.now()).getYears();
  }
}
