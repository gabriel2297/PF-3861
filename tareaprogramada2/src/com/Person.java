package com;

public class Person {
    String id, fn, ln, gender, birthDate, phoneNumber, email;
    private String className;

    public Person(String id, String fn, String ln, String gender, String birthDate, String phoneNumber, String email) {
        this.id = id;
        this.fn = fn;
        this.ln = ln;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.className = "Person";
    }

    public void printPersonInformation() {
        System.out.printf("[%s] - ", className);
        System.out.printf("Id: %s, ", getId());
        System.out.printf("Name: %s %s, ", getFn(), getLn());
        System.out.printf("Gender: %s, ", getGender());
        System.out.printf("Birthdate: %s, ", getBirthDate());
        System.out.printf("Phone: %s, ", getPhoneNumber());
        System.out.printf("Email: %s\n", getEmail());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFn() {
        return fn;
    }

    public void setFn(String fn) {
        this.fn = fn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(String ln) {
        this.ln = ln;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
