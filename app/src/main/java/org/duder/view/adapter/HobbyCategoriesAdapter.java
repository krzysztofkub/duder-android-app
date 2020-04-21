package org.duder.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.duder.R;

import java.util.List;

import static org.duder.viewModel.CreateEventViewModel.*;

public class HobbyCategoriesAdapter extends RecyclerView.Adapter<HobbyCategoriesAdapter.ViewHolder> {
    private List<String> hobbies;

    public HobbyCategoriesAdapter(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    @NonNull
    @Override
    public HobbyCategoriesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hobby_checkbox, viewGroup, false);
        return new HobbyCategoriesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(hobbies.get(i));
        viewHolder.checkBox.setOnClickListener(v -> {
            final boolean isChecked = viewHolder.checkBox.isChecked();
            String text = viewHolder.checkBox.getText().toString();
            if (isChecked && !hobbiesSelected.contains(text)) {
                hobbiesSelected.add(text);
            } else if (!isChecked){
                hobbiesSelected.remove(text);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hobbies.size();
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        private void bind(String hobby) {
            checkBox.setText(hobby);
        }
    }
}
