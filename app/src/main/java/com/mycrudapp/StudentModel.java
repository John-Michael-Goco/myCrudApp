package com.mycrudapp;

public class StudentModel {
    private String studentName;
    private String studentNumber;
    private String address;
    private String email;
    private String phoneNumber;
    private String course;

    public StudentModel(String studentName, String studentNumber, String address, String email, String phoneNumber, String course) {
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCourse() {
        return course;
    }

}
