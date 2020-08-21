package com.example.fibroapp.ui.assistant;

import android.util.Log;

import com.example.fibroapp.ui.exercises.Exercise;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;

public class Assistant {
    private String description;
    private String pathImage;

    public Assistant(String pathImage, String description){
        this.description = description;
        this.pathImage = pathImage;
    }

    public String getPathImage(){
        return pathImage;
    }

    public String getDescription(){
        return description;
    }

    public static ArrayList<Assistant> getAssistantsList(AssistantFragment fragment){
        DatabaseReference databaseAssistant = FirebaseDatabase.getInstance().getReference().child("assistants");
        ArrayList<Assistant> assistants = new ArrayList<Assistant>();

        databaseAssistant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    while (it.hasNext()){
                        HashMap<String, String> data = (HashMap<String, String>) it.next().getValue();
                        if (data != null){
                            String description = data.get("description");
                            String path = data.get("path");

                            assistants.add(new Assistant(path, description));
                        }
                    }

                    fragment.setAssistants(assistants);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return assistants;
    }
}
