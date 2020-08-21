package com.example.fibroapp.ui.assistant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fibroapp.R;
import com.example.fibroapp.ui.exercises.ExerciseAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AssistantFragment extends Fragment {

    AssistantAdapter adapter;
    ArrayList<Assistant> assistants;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recycler, container, false);

        //Get te RecyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);

        //Initialize exercises
        assistants = Assistant.getAssistantsList(this);

        //Create adapter
        adapter = new AssistantAdapter(assistants, getContext());

        //Attach the adapter to the recycler view
        recyclerView.setAdapter(adapter);

        //Set GridLayout to position the item
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(horizontalLayoutManager);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton actionButton = view.findViewById(R.id.floating_button);
        actionButton.hide();
    }

    @Override
    public void onDestroy() {
        adapter.destroyTTS();
        super.onDestroy();
    }

    public void setAssistants(ArrayList<Assistant> assistants){
        this.assistants = assistants;
        adapter.notifyDataSetChanged();
    }
}