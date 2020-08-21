package com.example.fibroapp.ui.exercises;

import android.os.Debug;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class Exercise extends ViewModel {
    private String path;
    private String title;

    public Exercise(String title, String path){
        this.path = path;
        this.title = title;
    }

    public String getPath(){
        return path;
    }

    public String getTitle(){
        return title;
    }

    public static ArrayList<Exercise> getExerciseList(ExercisesFragment fragment){
        DatabaseReference databaseExercises = FirebaseDatabase.getInstance().getReference().child("exercises");
        ArrayList<Exercise> exercises = new ArrayList<Exercise>();

        databaseExercises.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    while (it.hasNext()){
                        HashMap<String, String> data = (HashMap<String, String>) it.next().getValue();
                        if (data != null){
                            String title = data.get("title");
                            String path = data.get("path");

                            exercises.add(new Exercise(title, path));
                        }
                    }

                    fragment.setExercises(exercises);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return exercises;
    }
}