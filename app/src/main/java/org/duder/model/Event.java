package org.duder.model;

import java.io.File;

public class Event {
    private String name;
    private String description;
    private String date;
    private String time;
    private Boolean isPrivate;
    private File image;

    public Event(String name, String description, String date, String time, Boolean isPrivate, File image) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.time = time;
        this.isPrivate = isPrivate;
        this.image = image;
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

    public File getImage() {
        return image;
    }
}
