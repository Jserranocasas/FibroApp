package com.example.fibroapp.ui.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fibroapp.MainActivity;
import com.example.fibroapp.R;
import com.example.fibroapp.ui.login.LoginActivity;
import com.example.fibroapp.ui.logout.LogoutFragment;
import com.example.fibroapp.ui.profile.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth mFirebaseAuth;
    EditText nameId, emailId, password, confirmPassword;
    TextView tvSignIn;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();

        nameId = findViewById(R.id.nameEditText);
        emailId = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        confirmPassword = findViewById(R.id.confirmPasswordEditText);
        tvSignIn = findViewById(R.id.text_signup);
        btnSignUp = findViewById(R.id.button);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = findViewById(R.id.imageView_signup);
        Glide.with(this)
                .load(getDrawable(R.mipmap.ic_launcher_fibroapp))
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);

        btnSignUp.setOnClickListener(view -> {
            String name = nameId.getText().toString();
            String email = emailId.getText().toString();
            String pwd = password.getText().toString();
            String confpwd = confirmPassword.getText().toString();

            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            if (name.isEmpty()){
                nameId.setError(getResources().getString(R.string.enter_name));
                nameId.requestFocus();
            }
            else if(email.isEmpty()){
                emailId.setError(getResources().getString(R.string.enter_email));
                emailId.requestFocus();
            }
            else if (!email.contains("@")){
                emailId.setError(getResources().getString(R.string.enter_valid_email));
                emailId.requestFocus();
            }
            else if(pwd.isEmpty()){
                password.setError(getResources().getString(R.string.enter_pwd));
                password.requestFocus();
            }
            else if(pwd.length() < 8){
                password.setError(getResources().getString(R.string.pwd_length_error));
                password.requestFocus();
            }
            else if(confpwd.isEmpty()){
                password.setError(getResources().getString(R.string.confirm_pwd));
                password.requestFocus();
            }
            else if(!email.isEmpty() && !pwd.isEmpty() && !confpwd.isEmpty()){
                if(pwd.equals(confpwd)) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(
                        SignUpActivity.this, task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, getResources().getString(
                                        R.string.sign_up_failed), Toast.LENGTH_LONG).show();
                            } else {
                                LogoutFragment.login = LogoutFragment.TypeLogin.LOGINFIREBASE;
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();

                                Profile.getInstance().registerUserInDataBase(user.getEmail(), user.getUid(), name);
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            }
                        });
                } else {
                    confirmPassword.setError(getResources().getString(R.string.error_confirm_email));
                }
            }
            else{
                Toast.makeText(SignUpActivity.this,  getResources().getString(
                        R.string.exist_error), Toast.LENGTH_LONG).show();
            }
        });

        tvSignIn.setOnClickListener(view -> {
            mFirebaseAuth.signOut();
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        });
    }
}
