package org.duder.model.event;

import java.util.List;

public class EventBuilder {
    private String name;
    private String description;
    private List<String> hobbies;
    private int numberOfParticipants;
    private Long timestamp;

    public EventBuilder name(String name) {
        this.name = name;
        return this;
    }

    public EventBuilder hobbies(List<String> hobbies) {
        this.hobbies = hobbies;
        return this;
    }

    public EventBuilder numberOfParticipants(int numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
        return this;
    }

    public EventBuilder timestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public EventBuilder description(String description) {
        this.description = description;
        return this;
    }

    public Event createEvent() {
        return new Event(name, description, hobbies, numberOfParticipants, timestamp);
    }
}