package com.example.fibroapp.ui.profile;

import android.util.Log;

import com.example.fibroapp.ui.logout.LogoutFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

public class Profile {
    public static Profile profile;

    //Private Singleton
    public static Profile getInstance(){
        if (profile == null){
            profile = new Profile();
        }

        return profile;
    }

    private String _name;
    private String _email;
    private String _imagePath;
    private String _typeLogin;

    public Profile(String name, String email, String imagePath, String typeLogin){
        this._name = name;
        this._email = email;
        this._imagePath = imagePath;
        this._typeLogin = typeLogin;

        LogoutFragment.login = LogoutFragment.TypeLogin.valueOf(typeLogin);
    }

    public Profile(){}

    public String getName(){
        return Profile.profile._name;
    }

    public void setName(String name){
        Profile.profile._name = name;
    }

    public String getEmail(){
        return Profile.profile._email;
    }

    public String getImagePath(){
        return Profile.profile._imagePath;
    }

    public String getTypeLogin(){
        return _typeLogin;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: " + _name + ". Email: " + _email + ". ImagePath: " + _imagePath + ". Typelogin: " + _typeLogin;
    }

    private String getNameFromEmail(String email){
        int pos = email.indexOf("@");
        return email.substring(0, pos);
    }

    public void registerUserInDataBase(String email, String uid, String name){
        String imagePath = "https://firebasestorage.googleapis.com/v0/b/fibroapp-db0ff.appspot.com/o/images%2Fdefault.png?alt=media&token=6dd6482b-d3e7-41a7-97a0-9efe32255e04";
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        //Check if already exist
        mDatabase.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    return;
                }

                //Create the profile
                DatabaseReference usersRef = mDatabase.child("users");
                Map<String, String> dataUser = new HashMap<>();
                dataUser.put("email", email);
                dataUser.put("name", name);
                dataUser.put("image", imagePath);
                dataUser.put("typelogin", LogoutFragment.login.name());
                usersRef.child(uid).setValue(dataUser);

                //Create the medicines
                DatabaseReference medicinesRef = mDatabase.child("medicines");
                Map<String, String> dataMedicine = new HashMap<>();
                dataMedicine.put("medicines", "");
                medicinesRef.child(uid).setValue(dataMedicine);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
