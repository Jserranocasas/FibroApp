package com.example.fibroapp.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fibroapp.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LogoutFragment extends Fragment {

    public enum TypeLogin{
        LOGINFIREBASE, LOGINGOOGLE, LOGINTWITTER, LOGINFACEBOOK, LOGINDEFAULT
    }

    public  static  TypeLogin login = TypeLogin.LOGINDEFAULT;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (login) {
            case LOGINFIREBASE:
                FirebaseAuth.getInstance().signOut();
                break;
            case LOGINGOOGLE:
                LoginActivity.getInstance().signOutGoogle();
                break;
            case LOGINTWITTER:
                LoginActivity.getInstance().signOutTwitter();
                break;
            case LOGINFACEBOOK:
                LoginActivity.getInstance().signOutFacebook();
                break;
            default:
                FirebaseAuth.getInstance().signOut();
                break;
        }

        Intent i = new Intent(getContext(), LoginActivity.class);
        startActivity(i);
    }

}