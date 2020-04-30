package org.duder.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.dto.event.EventPreview;
import org.duder.model.EventItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final List<EventPreview> events;

    private PublishSubject<EventItem> onClickSubject = PublishSubject.create();
    Observable<EventItem> clickStream = onClickSubject.hide();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
    private final int SHORTEN_DESCRIPTION_LENGTH = 39;

    public EventListAdapter(List<EventPreview> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(events.get(i), (e) -> onClickSubject.onNext(e));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void addEvent(EventPreview event) {
        List<EventPreview> list = new ArrayList<>();
        list.add(event);
        addEvents(list);
    }

    public void addEvents(List<EventPreview> data) {
        data = data != null ? data : new ArrayList<>();
        events.addAll(data);
        Collections.sort(events, Comparator.comparingLong(EventPreview::getTimestamp));
        notifyDataSetChanged();
    }

    public void clearEvents() {
        events.clear();
        notifyDataSetChanged();
    }

    public Observable<EventItem> getClickStream() {
        return clickStream;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_view;
        private TextView title_text;
        private TextView desc_text;
        private TextView participants_text;
        private TextView hobbies_text;
        private TextView timestamp_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_view = itemView.findViewById(R.id.image_view);
            title_text = itemView.findViewById(R.id.title_text);
            desc_text = itemView.findViewById(R.id.desc_text);
            participants_text = itemView.findViewById(R.id.participants_text);
            hobbies_text = itemView.findViewById(R.id.hobbies_text);
            timestamp_text = itemView.findViewById(R.id.timestamp_text);
        }

        private void bind(EventPreview event, Consumer<EventItem> consumer) {
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
            desc_text.setText(getShortenText(event.getDescription()));
            participants_text.setText(itemView.getContext().getString(R.string.participant, event.getNumberOfParticipants()));
            AtomicReference<String> hobbies = new AtomicReference<>("");
            event.getHobbies().forEach(h -> hobbies.set(h + " " + hobbies));
            hobbies_text.setText(hobbies.get());
            timestamp_text.setText(simpleDateFormat.format(new Date(event.getTimestamp())));

            itemView.setOnClickListener((v) -> consumer.accept(new EventItem(image_view, event)));
        }

        private String getShortenText(String description) {
            if (description.length() > SHORTEN_DESCRIPTION_LENGTH) {
                return description.substring(0, SHORTEN_DESCRIPTION_LENGTH) + "...";
            }
            return description;
        }
    }
}

