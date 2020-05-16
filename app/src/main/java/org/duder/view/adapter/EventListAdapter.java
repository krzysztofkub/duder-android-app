package org.duder.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.dto.event.EventPreview;
import org.duder.dto.event.HobbyName;
import org.duder.model.EventItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {

    private final List<EventPreview> events;
    private final int SHORTEN_DESCRIPTION_LENGTH = 39;
    private PublishSubject<EventItem> onClickSubject = PublishSubject.create();
    private Observable<EventItem> clickStream = onClickSubject.hide();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

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
        private ImageView profile_image;
        private ImageView event_image;
        private TextView nickname_text;
        private TextView created_text;
        private TextView title_text;
        private TextView desc_text;
        private TextView participants_num_text;
        private TextView observers_num_text;
        private TextView hobbies_text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.view_profile);
            event_image = itemView.findViewById(R.id.event_img);
            nickname_text = itemView.findViewById(R.id.nickname_text);
            created_text = itemView.findViewById(R.id.created_text);
            title_text = itemView.findViewById(R.id.title_text);
            desc_text = itemView.findViewById(R.id.description_text);
            participants_num_text = itemView.findViewById(R.id.participants_number_text);
            observers_num_text = itemView.findViewById(R.id.observers_number_text);
            hobbies_text = itemView.findViewById(R.id.hobbies_text);
        }

        private void bind(EventPreview event, Consumer<EventItem> consumer) {
            event_image.setVisibility(View.GONE);
            if (event.getImageUrl() != null) {
                event_image.setVisibility(View.VISIBLE);
                setupImage(event.getImageUrl(), event_image, R.drawable.ic_image_24dp);
            }
            setupImage(event.getHost().getImageUrl(), profile_image, R.drawable.profile);
            nickname_text.setText(event.getHost().getNickname());
            created_text.setText(setEventCreatedText(event.getCreated()));
            title_text.setText(event.getName());
            desc_text.setText(getShortenText(event.getDescription()));
            participants_num_text.setText(String.valueOf(event.getNumberOfParticipants()));
            observers_num_text.setText("0");
            hobbies_text.setText(setHobbies(event.getHobbies()));

            itemView.setOnClickListener((v) -> consumer.accept(new EventItem(profile_image, event)));
        }

        private String setHobbies(Set<HobbyName> hobbies) {
            return hobbies.stream().map(HobbyName::name).collect(Collectors.joining(" "));
        }

        private String setEventCreatedText(Long created) {
            return "1d";
        }

        private void setupImage(String imageUrl, ImageView imageView, @DrawableRes int drawable) {
            Picasso
                    .get()
                    .cancelRequest(imageView);
            imageView.setImageResource(drawable);

            Picasso
                    .get()
                    .load(imageUrl)
                    .placeholder(drawable)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }

        private String getShortenText(String description) {
            if (description.length() > SHORTEN_DESCRIPTION_LENGTH) {
                return description.substring(0, SHORTEN_DESCRIPTION_LENGTH) + "... Read more";
            }
            return description;
        }
    }
}

