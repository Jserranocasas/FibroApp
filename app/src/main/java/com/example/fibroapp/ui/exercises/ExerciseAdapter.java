package com.example.fibroapp.ui.exercises;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fibroapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView title;

        public ViewHolder(View itemView){
            super(itemView);
            image = itemView.findViewById(R.id.imageExercise);
            title = itemView.findViewById(R.id.titleExercise);

            //Set the onClickListener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), VideoActivity.class);
                    i.putExtra("src", exercises.get(getAdapterPosition()).getPath());
                    v.getContext().startActivity(i);
                }
            });
        }


    }

    private List<Exercise> exercises;
    private Context context;

    public ExerciseAdapter(List<Exercise> exercises, Context context){
        this.exercises = exercises;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View exerciseView = inflater.inflate(R.layout.item_exercise, parent, false);

        // Create a new holder instance
        final ViewHolder viewHolder = new ViewHolder(exerciseView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise e = exercises.get(position);

        //ImageView
        Glide.with(context)
                .load(Uri.parse(e.getPath()))
                .into(holder.image);

        //Title
        TextView title = holder.title;
        title.setText(e.getTitle());
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }
}
