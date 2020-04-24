package com.example.hitdaily;
public class Classes{
    String category;
    String room_no;
    String subject;
    String teacher;

    public Classes(){

    }

    public Classes(String category, String room_no, String subject, String teacher) {
        this.category = category;
        this.room_no = room_no;
        this.subject = subject;
        this.teacher = teacher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRoom_no() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no = room_no;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
}