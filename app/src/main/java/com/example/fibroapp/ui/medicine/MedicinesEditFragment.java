package com.example.fibroapp.ui.medicine;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.fibroapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

public class MedicinesEditFragment extends Fragment {

    private static final String TIME24_PATTERN= "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    public static MedicinesEditFragment instance;
    private final SimpleDateFormat formatterDay = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
    public static MedicineAdapter adapter;
    public static int medicineIndex;

    EditText editName, editFrequency, editDate, editTime, editDateFinish;
    RadioGroup radioGroup;
    TextView labelDateFinsih;
    Button btnSave;

    Medicine medicine;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_medicine, container, false);

        medicine = adapter.getMedicine(medicineIndex);

        editName = root.findViewById(R.id.edit_name_medicine);
        editFrequency = root.findViewById(R.id.edit_frecquency_medicine);
        editDate = root.findViewById(R.id.edit_date_medicine);
        editTime = root.findViewById(R.id.edit_time_medicine);
        labelDateFinsih = root.findViewById(R.id.label_date_finish);
        editDateFinish = root.findViewById(R.id.edit_date_finish_medicine);
        radioGroup = root.findViewById(R.id.radioGroup);
        btnSave = root.findViewById(R.id.btn_medicine);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radiobutton_undefined){
                labelDateFinsih.setVisibility(View.GONE);
                editDateFinish.setVisibility(View.GONE);
            }else{
                labelDateFinsih.setVisibility(View.VISIBLE);
                editDateFinish.setVisibility(View.VISIBLE);
            }
        });

        editName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                boolean valid = isNameValid(editName.getText().toString());
                if (!valid){
                    editName.setError(getResources().getString(R.string.name_error));
                }
            }
        });

        editDate.setOnClickListener((v) -> {
            if (medicine == null){
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        (datePicker, year, month, dayOfMonth) -> {
                            try {
                                editDate.setText(
                                        formatterDay.format(formatterDay.parse(dayOfMonth + "/" + (month+1) + "/" + year)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

                datePickerDialog.show();
            }
        });

        editDateFinish.setOnClickListener((v) -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                (datePicker, year, month, dayOfMonth) -> {
                    try {
                        editDateFinish.setText(
                                formatterDay.format(formatterDay.parse(dayOfMonth + "/" + (month+1) + "/" + year)));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        editTime.setOnClickListener((v) -> {
            if (medicine == null){
                Calendar calendar = Calendar.getInstance();

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        (timePicker, hourOfDay, minutes) -> {
                            try {
                                editTime.setText(
                                        formatterTime.format(formatterTime.parse(hourOfDay + ":" + minutes)));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE), true);

                timePickerDialog.show();
            }
        });

        editFrequency.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                boolean valid = isFrequencyValid(editFrequency.getText().toString());
                if (!valid){
                    editFrequency.setError(getResources().getString(R.string.frequency_error));
                }
            }
        });

        btnSave.setOnClickListener(v -> saveMedicine());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (medicine != null){
            editName.setText(medicine.getName());

            editDate.setText(medicine.getStartDate());
            editDate.setFocusable(false);
            editDate.setFocusableInTouchMode(false);
            editDate.setClickable(false);

            editTime.setText(medicine.getStartTime());
            editTime.setFocusable(false);
            editTime.setFocusableInTouchMode(false);
            editTime.setClickable(false);

            editFrequency.setText(medicine.getFrecuency());
            editFrequency.setFocusable(false);
            editFrequency.setFocusableInTouchMode(false);
            editFrequency.setClickable(false);

            if (medicine.isLimited()){
                editDateFinish.setText(medicine.getFinishDate());
                radioGroup.check(R.id.radiobutton_limited);
            }else{
                radioGroup.check(R.id.radiobutton_undefined);
            }

            btnSave.setText(getResources().getText(R.string.btn_save_medicine));
        }else{
            Calendar calendar = Calendar.getInstance();

            try{
                editDate.setText(
                        formatterDay.format(formatterDay.parse(calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR))));

                editTime.setText(
                        formatterTime.format(formatterTime.parse(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE))));
            }catch(ParseException e){
                e.printStackTrace();
            }

            radioGroup.check(R.id.radiobutton_undefined);
        }
    }

    private boolean isNameValid(String nameString){
        return !nameString.trim().isEmpty();
    }

    private boolean isDateValid(String dateString){
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);

        try{
            Date date = format.parse(dateString);

            if (!format.format(date).equals(dateString)){
                return false;
            }
        }catch(ParseException e){
            return false;
        }

        return true;
    }

    private boolean isTimeValid(String timeString){
        return Pattern.compile(TIME24_PATTERN).matcher(timeString).matches();
    }

    private boolean isInFuture(String dateString, String timeString){
        if (medicine == null){
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = null;
            try {
                date = format.parse(dateString + " " + timeString);
            } catch (ParseException e) {
                return false;
            }

            if (System.currentTimeMillis() > date.getTime()){
                return false;
            }

            return true;
        }else{
            return true;
        }
    }

    private boolean isFrequencyValid(String frequencyString){
        if (frequencyString.isEmpty()){
            return false;
        }

        return true;
    }

    public boolean isFinishDateValid(String startDateString, String finishDateString){
        if (radioGroup.getCheckedRadioButtonId() == R.id.radiobutton_undefined)
            return true;

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        if (finishDateString.isEmpty() || startDateString.isEmpty()){
            return false;
        }

        Date startDate;
        Date finishDate;

        try{
            startDate = format.parse(startDateString);
            finishDate = format.parse(finishDateString);
        }catch (ParseException e){
            return false;
        }

        if (startDate.getTime() > finishDate.getTime()){
            return false;
        }

        return true;
    }

    private void saveMedicine(){
        //Check value
        if (!isNameValid(editName.getText().toString())){
            editName.setError(getResources().getString(R.string.name_error));
            editName.requestFocus();
            return;
        }

        if (!isDateValid(editDate.getText().toString())){
            editDate.setError(getResources().getString(R.string.date_error));
            editDate.requestFocus();
            return;
        }

        if (!isTimeValid(editTime.getText().toString())){
            editTime.setError(getResources().getString(R.string.time_error));
            editTime.requestFocus();
            return;
        }

        if (!isInFuture(editDate.getText().toString(), editTime.getText().toString())){
            editDate.setError(getResources().getString(R.string.future_error));
            editTime.setError(getResources().getString(R.string.future_error));
            editTime.requestFocus();
            return;
        }

        if (!isFrequencyValid(editFrequency.getText().toString())){
            editFrequency.setError(getResources().getString(R.string.frequency_error));
            editFrequency.requestFocus();
            return;
        }

        if (!isFinishDateValid(editDate.getText().toString(), editDateFinish.getText().toString())){
            editDateFinish.setError(getResources().getString(R.string.finish_date_error));
            editDateFinish.requestFocus();
            return;
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.radiobutton_limited){
            if (!isDateValid(editDateFinish.getText().toString())){
                editDateFinish.setError(getResources().getString(R.string.date_error));
                editDateFinish.requestFocus();
                return;
            }
        }

        //Save value
        if (medicine == null){
            //New medicine
            String name = editName.getText().toString();
            String startDate = editDate.getText().toString();
            String startTime = editTime.getText().toString();
            String frequency = editFrequency.getText().toString() + ":00";

            if (radioGroup.getCheckedRadioButtonId() == R.id.radiobutton_limited){
                String finishDate = editDateFinish.getText().toString();
                medicine = new Medicine((int)System.currentTimeMillis(), name, startDate, startTime, frequency, finishDate);
            }else{
                medicine = new Medicine((int) System.currentTimeMillis(), name, startDate, startTime, frequency);
            }

            medicine.setAlarm(getContext());
        }else{
            //Set new value
            medicine.setName(editName.getText().toString());

            if (radioGroup.getCheckedRadioButtonId() == R.id.radiobutton_limited){
                medicine.doLimited(editDateFinish.getText().toString());
            }else{
                medicine.doUndefined();
            }
        }

        //Add or save in the adapter
        if (medicineIndex == -1){
            adapter.addMedicine(medicine);
        }else{
            adapter.replaceMedicine(medicine, medicineIndex);
        }

        //Go back to the adapter view
        getActivity().getOnBackPressedDispatcher().onBackPressed();
    }
}
