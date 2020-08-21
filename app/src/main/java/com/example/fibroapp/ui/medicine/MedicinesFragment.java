package com.example.fibroapp.ui.medicine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fibroapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MedicinesFragment extends Fragment {

    ArrayList<Medicine> medicines;
    MedicineAdapter adapter;
    MedicinesFragment fragment = this;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recycler, container, false);

        //Get te RecyclerView
        recyclerView = root.findViewById(R.id.recyclerView);

        //Initialize exercises
        medicines = new ArrayList<>();

        //Create adapter
        adapter = new MedicineAdapter(this, medicines);

        //Attach the adapter to the recycler view
        recyclerView.setAdapter(adapter);

        //Set GridLayout to position the item
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton actionButton = view.findViewById(R.id.floating_button);
        actionButton.show();
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MedicinesEditFragment.medicineIndex = -1;
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.nav_medicine_edit);
            }
        });

        DatabaseReference medicinesRef = FirebaseDatabase.getInstance().getReference().child("medicines").child(FirebaseAuth.getInstance().getUid());

        medicinesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    HashMap<String, String> data = (HashMap<String, String>) dataSnapshot.getValue();

                    if (data != null){
                        medicines = loadMedicines(data.get("medicines"));
                    }

                    adapter = new MedicineAdapter(fragment, medicines);

                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<Medicine> loadMedicines(String medicinesString){
        ArrayList<Medicine> medicines = new ArrayList<>();

        String[] firstSplit = medicinesString.split("#");
        if (firstSplit[0].isEmpty())
            return medicines;

        for (int i = 0; i<firstSplit.length; i++){
            String[] secondSplit = firstSplit[i].split(",");
            boolean fail = false;

            //Name
            String name = "";
            if (secondSplit[0].equals("name")){
                name = secondSplit[1];
            }else{
                fail = true;
            }

            //StartDate
            String startDate = "";
            if (secondSplit[2].equals("startDate")){
                startDate = secondSplit[3];
            }else{
                fail = true;
            }

            //StartTime
            String startTime = "";
            if (secondSplit[4].equals("startTime")){
                startTime = secondSplit[5];
            }else{
                fail = true;
            }

            //Frequency
            String frequency = "";
            if (secondSplit[6].equals("frequency")){
                frequency = secondSplit[7];
            }else{
                fail = true;
            }

            //Limited
            boolean limited = false;
            if (secondSplit[8].equals("limited")){
                limited = Boolean.parseBoolean(secondSplit[9]);
            }else{
                fail = true;
            }

            //FinishDate
            String finishDate = "";
            if (limited){
                if (secondSplit[10].equals("finishDate")){
                    finishDate = secondSplit[11];
                }else{
                    fail = true;
                }
            }

            //Code
            int code = 0;
            if (secondSplit[12].equals("code")){
                code = Integer.parseInt(secondSplit[13]);
            }else{
                fail = true;
            }

            if (!fail){
                Medicine m = null;
                if (limited){
                    m = new Medicine(code, name, startDate, startTime, frequency, finishDate);
                }else{
                    m = new Medicine(code, name, startDate, startTime, frequency);
                }

                medicines.add(m);
            }
        }

        return medicines;
    }

    public void saveMedicines(ArrayList<Medicine> medicines){
        DatabaseReference medicinesRef = FirebaseDatabase.getInstance().getReference().child("medicines");

        String s = "";
        if (medicines.size() != 0){
            for (int i = 0; i<medicines.size()-1; i++){
                s += medicines.get(i).toString() + "#";
            }
            s += medicines.get(medicines.size()-1).toString();
        }

        HashMap<String, String> data = new HashMap<>();
        data.put("medicines", s);
        medicinesRef.child(FirebaseAuth.getInstance().getUid()).setValue(data);

        adapter.notifyDataSetChanged();
    }
}