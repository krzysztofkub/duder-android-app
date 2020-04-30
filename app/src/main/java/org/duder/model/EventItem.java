package org.duder.model;

import android.view.View;

import org.duder.dto.event.EventPreview;

public class EventItem {
    private View imageView;
    private EventPreview eventPreview;

    public EventItem(View imageView, EventPreview eventPreview) {
        this.imageView = imageView;
        this.eventPreview = eventPreview;
    }

    public View getImageView() {
        return imageView;
    }

    public EventPreview getEventPreview() {
        return eventPreview;
    }
}
