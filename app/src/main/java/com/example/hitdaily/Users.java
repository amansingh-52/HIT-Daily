package com.example.hitdaily;

public class Users {
   public String email;
   public String password;
   public String name;
   public String year;
   public String dept;
   public String section;
   public String group;

   public Users(){}

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getDept() {
        return dept;
    }

    public String getSection() {
        return section;
    }

    public String getGroup() {
        return group;
    }
}
