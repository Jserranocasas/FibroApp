package com.example.fibroapp.ui.exercises;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibroapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExercisesFragment extends Fragment {

    ArrayList<Exercise> exercises;
    ExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recycler, container, false);

        //Get te RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);

        //Initialize exercises
        exercises = Exercise.getExerciseList(this);

        //Create adapter
        adapter = new ExerciseAdapter(exercises, getContext());

        //Attach the adapter to the recycler view
        recyclerView.setAdapter(adapter);

        //Set GridLayout to position the item
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(), 2));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton actionButton = view.findViewById(R.id.floating_button);
        actionButton.hide();
    }

    public void setExercises(ArrayList<Exercise> exercises){
        this.exercises = exercises;
        adapter.notifyDataSetChanged();
    }
}