package com.github.devholic.somareport.data.view;

/**
 * Created by devholic on 15. 7. 23..
 */
public class User {

    private String name;
    private String email;
    private String role;
    private int[] year;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int[] getYear() {
        return year;
    }

    public void setYear(int[] year) {
        this.year = year;
    }

}
