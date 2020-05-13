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

import com.squareup.picasso.Picasso;

import org.duder.R;
import org.duder.dto.user.Dude;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DudeListAdapter extends RecyclerView.Adapter<DudeListAdapter.ViewHolder>{
    private final List<Dude> dudes;
    private final Context mContext;

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
        viewHolder.bind(dudes.get(i));
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

        private void bind(Dude dude) {
            Picasso
                    .get()
                    .cancelRequest(view_profile);
            view_profile.setImageResource(R.drawable.profile);
            if (dude.getImageUrl() != null) {
                Picasso
                        .get()
                        .load(dude.getImageUrl())
                        .noFade()
                        .into(view_profile);
            }
            nickname_text.setText(dude.getNickname());

            invite_friend_button.setBackground(mContext.getResources().getDrawable(R.drawable.add_dude));
            invite_friend_button.setVisibility(View.VISIBLE);
            if (dude.getIsFriend()) {
                invite_friend_button.setVisibility(View.GONE);
            } else if (dude.getIsInvitationSent()) {
                invite_friend_button.setBackground(mContext.getResources().getDrawable(R.drawable.add_dude_in_process));
                invite_friend_button.setClickable(false);
            }
        }
    }
}
