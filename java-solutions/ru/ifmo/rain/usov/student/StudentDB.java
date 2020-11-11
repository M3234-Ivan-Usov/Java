package ru.ifmo.rain.usov.student;
//java -cp . -p . -m info.kgeorgiy.java.advanced.student StudentQuery ru.ifmo.rain.usov.student.StudentDB
import info.kgeorgiy.java.advanced.student.*;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {
    private static final Comparator<Student> LFID = Comparator.comparing(Student::getLastName)
            .thenComparing(Student::getFirstName).thenComparingInt(Student::getId);

    private List<Student> Find(Collection<Student> students, Predicate<Student> param) {
        return students.stream().filter(param).sorted(LFID).collect(Collectors.toList());
    }

    private <T> List<T> Get(List<Student> students, Function<Student, T> param) {
        return students.stream().map(param).collect(Collectors.toList());
    }

    private List<Student> Sort(Collection<Student> students, Comparator<Student> comp) {
        return students.stream().sorted(comp).collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return Get(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return Get(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return Get(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return Get(students, student -> (student.getFirstName() + " " + student.getLastName()));
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream().min(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return Sort(students, Comparator.comparingInt(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return Sort(students, LFID);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return Find(students, student -> Objects.equals(student.getFirstName(), name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return Find(students, student -> Objects.equals(student.getLastName(), name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return Find(students, student -> Objects.equals(student.getGroup(), group));
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return students.stream().filter(student -> Objects.equals(student.getGroup(), group))
                .sorted(LFID).collect(Collectors.toMap(Student::getLastName, Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)));
    }

    /*@Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return students.stream().sorted(LFID).collect(Collectors.groupingBy(Student::getGroup, TreeMap::new,
                Collectors.toList())).entrySet().stream().map(ent-> new Group(ent.getKey(),
                ent.getValue())).sorted(Comparator.comparing(Group::getName)).collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return students.stream().sorted(Comparator.comparingInt(Student::getId)).collect(Collectors
                .groupingBy(Student::getGroup, TreeMap::new, Collectors.toList())).entrySet().stream()
                .map(ent-> new Group(ent.getKey(),ent.getValue())).sorted(Comparator.comparing((Group::getName)))
                .collect(Collectors.toList());
    }

    @Override
    public String getLargestGroup(Collection<Student> students) {
        return students.stream().collect(Collectors.groupingBy(Student::getGroup, Collectors.toList())).entrySet()
                .stream().max()
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return null;
    }*/
}