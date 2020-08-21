package com.example.fibroapp.ui.medicine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fibroapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder>{
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView frequency;
        public TextView undefined;

        public ViewHolder(final View itemView){
            super(itemView);

            name = itemView.findViewById(R.id.name_medicine);
            frequency = itemView.findViewById(R.id.frequency_medicine);
            undefined = itemView.findViewById(R.id.undefined_medicine);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MedicinesEditFragment.medicineIndex = getAdapterPosition();
                    NavController navController = Navigation.findNavController(fragment.getActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.nav_medicine_edit);
                }
            });
        }
    }

    private static ArrayList<Medicine> medicines;
    private MedicinesFragment fragment;

    public MedicineAdapter(MedicinesFragment fragment, ArrayList<Medicine> medicines){
        this.medicines = medicines;
        this.fragment = fragment;

        MedicinesEditFragment.adapter = this;

        RemoveExpiredMedicine();
    }

    public Context getContext(){
        return fragment.getContext();
    }

    public RecyclerView getRecyclerView(){
        return fragment.getView().findViewById(R.id.recyclerView);
    }
    public void addMedicine(Medicine medicine){
        medicines.add(medicine);
        fragment.saveMedicines(medicines);
    }

    public void replaceMedicine(Medicine medicine, int pos){
        medicine.cancelAlarm(getContext());
        medicines.remove(pos);

        medicine.setAlarm(getContext());
        medicines.add(pos, medicine);
        
        fragment.saveMedicines(medicines);
    }

    public void removeMedicine(int pos){
        if (pos >= 0 && pos < medicines.size()){
            removeMedicine(medicines.get(pos));
        }
    }

    public void removeMedicine(Medicine medicine){
        medicine.cancelAlarm(getContext());
        medicines.remove(medicine);
        fragment.saveMedicines(medicines);
    }

    public Medicine getMedicine(int pos){
        if (pos < 0 || pos > medicines.size()){
            return null;
        }

        return medicines.get(pos);
    }

    private void RemoveExpiredMedicine(){
        for (int i = 0; i<medicines.size(); i++){
            Medicine m = medicines.get(i);
            if (m.isLimited()){
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = null;
                try {
                    date = format.parse(m.getFinishDate() + " 23:59");
                } catch (ParseException e) {
                    return;
                }

                if (System.currentTimeMillis() > date.getTime()){
                    removeMedicine(m);
                }
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_medicine, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine m = medicines.get(position);

        //Name
        TextView name = holder.name;
        name.setText(m.getName().toUpperCase());

        //Frequency
        TextView frequency = holder.frequency;
        frequency.setText("Cada " + m.getFrecuency() + " horas");

        //Undefined
        TextView undefined = holder.undefined;
        if (m.isLimited()){
            undefined.setText("Hasta " + m.getFinishDate());
        }else{
            undefined.setText("Indefinidamente");
        }
    }

    @Override
    public int getItemCount() {
        if (medicines == null)
            return 0;

        return medicines.size();
    }
}
