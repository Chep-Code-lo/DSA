package org.example.genericsort;

public class Student {
    private String studentCode;
    private String firstName;
    private String lastName;
    private String dateOfBirth;

    public Student() {}

    public Student(String studentCode, String firstName, String lastName, String dateOfBirth) {
        this.studentCode = studentCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getStudentCode() { return studentCode; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getDateOfBirth() { return dateOfBirth; }

    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String toString() {
        return String.format("%s | %s %s | %s", studentCode, firstName, lastName, dateOfBirth);
    }
}
