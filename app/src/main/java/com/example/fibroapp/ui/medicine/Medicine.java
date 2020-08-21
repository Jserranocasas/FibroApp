package com.example.fibroapp.ui.medicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.fibroapp.ui.alarm.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class Medicine {
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private int code;
    private String name;
    private String startDate;
    private String startTime;
    private String frequency;
    boolean limited;
    private String finishDate;

    public Medicine(int code, String name, String startDate, String startTime, String frequency){
        this.code = code;
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.frequency = frequency;
        this.limited = false;
        this.finishDate = null;
    }

    public Medicine(int code, String name, String startDate, String startTime, String frequency, String finishDate){
        this.code = code;
        this.name = name;
        this.startDate = startDate;
        this.startTime = startTime;
        this.frequency = frequency;
        this.limited = true;
        this.finishDate = finishDate;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getStartDate(){
        return startDate;
    }

    public String getStartTime(){
        return startTime;
    }

    public String getFrecuency(){
        return frequency;
    }

    public String getFinishDate(){
        return finishDate;
    }

    public boolean isLimited(){
        return limited;
    }

    public void doLimited(String finishDate){
        limited = true;
        this.finishDate = finishDate;
    }

    public void doUndefined(){
        limited = false;
        this.finishDate = null;
    }

    public String toString() {
        String s = "name," + name + ",startDate," + startDate + ",startTime," + startTime +
                   ",frequency,"  + frequency +  ",limited," + limited +
                   ",finishDate," + finishDate + ",code," + code;
        return s;
    }

    private long getMillisFromFrequency(){
        String[] split = frequency.split(":");
        int min = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        return min * 60 * 1000;
    }

    public void cancelAlarm(Context context){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent  = new Intent(context, AlarmReceiver.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        manager.cancel(pIntent);
    }

    public void setAlarm(Context context){
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent  = new Intent(context, AlarmReceiver.class);
        intent.putExtra("code", code);
        intent.putExtra("name", name);
        intent.putExtra("limited", limited);
        intent.putExtra("finishDate", finishDate);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_CANCEL_CURRENT );

        Date date = null;
        try {
            date = format.parse(startDate + " " + startTime);
        } catch (ParseException e) {
            return;
        }
        manager.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(), getMillisFromFrequency(), pIntent);
    }
}
