package org.duder.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.dto.user.Dude;
import org.duder.model.DudeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static org.duder.util.InviteButtonUtil.setInviteButtonProperties;

public class DudeListAdapter extends RecyclerView.Adapter<DudeListAdapter.ViewHolder> {
    private final List<Dude> dudes;
    private final Context mContext;

    private PublishSubject<DudeItem> onClickSubject = PublishSubject.create();
    private Observable<DudeItem> clickStream = onClickSubject.hide();

    public DudeListAdapter(Context mContext, List<Dude> dudes) {
        this.mContext = mContext;
        this.dudes = dudes;
    }

    @NonNull
    @Override
    public DudeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dude_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DudeListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(dudes.get(i), d -> onClickSubject.onNext(d));
    }

    @Override
    public int getItemCount() {
        return dudes.size();
    }

    public void addDudes(List<Dude> data) {
        data = data != null ? data : new ArrayList<>();
        dudes.addAll(data);
        Collections.sort(dudes, Comparator.comparing(Dude::getNickname));
        notifyDataSetChanged();
    }

    public void clearItems() {
        dudes.clear();
        notifyDataSetChanged();
    }

    public Observable<DudeItem> getClickStream() {
        return clickStream;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView view_profile;
        private TextView nickname_text;
        private Button invite_friend_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_profile = itemView.findViewById(R.id.view_profile);
            nickname_text = itemView.findViewById(R.id.nickname_text);
            invite_friend_button = itemView.findViewById(R.id.invite_friend_btn);
        }

        private void bind(Dude dude, Consumer<DudeItem> consumer) {
            Picasso.get()
                    .cancelRequest(view_profile);

            view_profile.setImageResource(R.drawable.profile);

            if (dude.getImageUrl() != null) {
                Picasso.get()
                        .load(dude.getImageUrl())
                        .noFade()
                        .into(view_profile);
            }

            nickname_text.setText(dude.getNickname());
            setInviteButtonProperties(mContext, dude.getFriendshipStatus(), (MaterialButton) invite_friend_button);
            invite_friend_button.setOnClickListener(v -> consumer.accept(new DudeItem(dude, invite_friend_button)));
        }
    }
}
