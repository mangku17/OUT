package com.homeout;

public class Memo {

    int number;
    String context;
    String date;
    String state;

    public Memo() {    }

    public Memo(String context, String date, String state){
        this.context = context;
        this.date = date;
        this.state =state;
    }

    public Memo(int number, String context, String date, String state) {
        this.number = number;
        this.context = context;
        this.date = date;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
