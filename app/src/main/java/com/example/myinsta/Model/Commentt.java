package com.example.myinsta.Model;

public class  Commentt {
    private String comment;
    private String publisher;

    public Commentt(String comment, String publisher) {
        this.comment = comment;
        this.publisher = publisher;
    }

    public Commentt() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}

