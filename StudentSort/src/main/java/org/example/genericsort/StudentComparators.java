package org.example.genericsort;

import java.time.LocalDate;
import java.util.Comparator;

public final class StudentComparators {
    private StudentComparators() {}

    public static final Comparator<Student> BY_CODE_ASC =
            Comparator.comparing(Student::getStudentCode, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Student> BY_FIRSTNAME_THEN_LASTNAME_ASC =
            Comparator.comparing(Student::getFirstName, String.CASE_INSENSITIVE_ORDER)
                      .thenComparing(Student::getLastName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Student> BY_DOB_ASC =
            Comparator.comparing(s -> LocalDate.parse(s.getDateOfBirth()));
}
