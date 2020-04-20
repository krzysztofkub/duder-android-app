package org.duder.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.model.event.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EventPostAdapter extends RecyclerView.Adapter<EventPostAdapter.ViewHolder> {

    private final List<Event> events;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    public EventPostAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_event_post, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(events.get(i));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public List<Event> getEvents() {
        return events;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_view;
        private TextView title_text;
        private TextView participants_text;
        private TextView hobbies_text;
        private TextView timestamp_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.image_view);
            title_text = itemView.findViewById(R.id.title_text);
            participants_text = itemView.findViewById(R.id.participants_text);
            hobbies_text = itemView.findViewById(R.id.hobbies_text);
            timestamp_text = itemView.findViewById(R.id.timestamp_text);
        }

        private void bind(Event event) {
            Picasso
                    .with(itemView.getContext())
                    .cancelRequest(image_view);
            image_view.setImageResource(R.drawable.ic_image_24dp);
            Picasso
                    .with(itemView.getContext())
                    .load("https://miro.medium.com/max/1200/1*mk1-6aYaf_Bes1E3Imhc0A.jpeg")
                    .placeholder(R.drawable.ic_image_24dp)
                    .fit()
                    .centerCrop()
                    .into(image_view);
            title_text.setText(event.getName());
            participants_text.setText(itemView.getContext().getString(R.string.participant, event.getNumberOfParticipants()));
            AtomicReference<String> hobbies = new AtomicReference<>("");
            event.getHobbies().forEach(h -> hobbies.set(h + " " + hobbies));
            hobbies_text.setText(hobbies.get());
            timestamp_text.setText(simpleDateFormat.format(new Date(event.getTimestamp())));
        }
    }
}

