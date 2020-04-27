package com.github.maxiaoda;

public class Message {
    private Integer id;
    private String massage;

    public Message() {
    }

    public Message(Integer id, String massage) {
        this.id = id;
        this.massage = massage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }
}
