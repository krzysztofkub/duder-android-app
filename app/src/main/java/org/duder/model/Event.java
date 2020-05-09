package org.duder.model;

public class Event {
    private String name;
    private String description;
    private String date;
    private String time;
    private Boolean isPrivate;

    public Event(String name, String description, String date, String time, Boolean isPrivate) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isPrivate = isPrivate;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }
}
