package com.example.tidu.reply;

public class FbGrpMsg {
    String date,message,name,time,id;

    public FbGrpMsg(String date, String message, String name, String time, String id) {
        this.date = date;
        this.message = message;
        this.name = name;
        this.time = time;
        this.id = id;
    }

    public FbGrpMsg()
    {

    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
