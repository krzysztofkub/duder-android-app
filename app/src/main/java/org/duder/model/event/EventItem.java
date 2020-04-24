package org.duder.model.event;

import android.view.View;

public class EventItem {
    private View EventView;
    private Event event;

    public EventItem(View eventView, Event event) {
        EventView = eventView;
        this.event = event;
    }

    public View getEventView() {
        return EventView;
    }

    public void setEventView(View eventView) {
        EventView = eventView;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}
