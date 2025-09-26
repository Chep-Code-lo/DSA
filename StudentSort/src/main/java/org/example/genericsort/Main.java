package org.example.genericsort;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Main {
    public static void main(String[] args) throws IOException {
        String json;
        System.out.println("URL = " + Main.class.getResource("/students.json"));
        try (InputStream in = Main.class.getResourceAsStream("/students.json")) {
            if (in == null) throw new IOException("Không tìm thấy students.json trong resources!");
            json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }

        Type listType = new TypeToken<ArrayList<Student>>() {}.getType();
        List<Student> students = new Gson().fromJson(json, listType);

        System.out.println("== Raw ==");
        print(students);

        List<Student> byCode = new ArrayList<>(students);
        InsertionSort.sort(byCode, StudentComparators.BY_CODE_ASC);
        System.out.println("\n== Insertion: By studentCode ASC ==");
        print(byCode);

        List<Student> byName = new ArrayList<>(students);
        InsertionSort.sort(byName, StudentComparators.BY_FIRSTNAME_THEN_LASTNAME_ASC);
        System.out.println("\n== Insertion: By firstName ASC, then lastName ASC ==");
        print(byName);

        List<Student> byDob = new ArrayList<>(students);
        InsertionSort.sort(byDob, StudentComparators.BY_DOB_ASC);
        System.out.println("\n== Insertion: By dateOfBirth ASC ==");
        print(byDob);
    }

    private static void print(List<Student> list) {
        list.forEach(s -> System.out.println("- " + s));
    }
}
