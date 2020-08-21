package com.example.fibroapp.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.fibroapp.MainActivity;
import com.example.fibroapp.R;
import com.example.fibroapp.ui.logout.LogoutFragment;
import com.example.fibroapp.ui.profile.Profile;
import com.example.fibroapp.ui.signup.SignUpActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;
    private EditText emailId, password;
    private TextView tvSignUp;

    private Button btnSignIn;
    private CallbackManager mCallbackManager;
    private TwitterLoginButton mLoginButton;
    private SignInButton signInButton;

    private static final String TAGTW = "TwitterLogin";
    private static final String TAGFB = "FacebookLogin";
    private static final String TAGGO = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private static LoginActivity sSoleInstance;
    private GoogleSignInClient mGoogleSignInClient;

    public static LoginActivity getInstance() {
        return sSoleInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));

        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                .build();

        Twitter.initialize(twitterConfig);

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_login);
        sSoleInstance = this;

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        btnSignIn = findViewById(R.id.buttonLogin);
        tvSignUp = findViewById(R.id.textViewSignUp);
        signInButton = findViewById(R.id.buttonGoogle);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        ImageView imageView = findViewById(R.id.imageView_login);
        Glide.with(this)
                .load(getDrawable(R.mipmap.ic_launcher_fibroapp))
                .apply(RequestOptions.circleCropTransform())
                .into(imageView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.sign_in);
        setSupportActionBar(toolbar);

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if( mFirebaseUser != null ){
                Snackbar.make(findViewById(R.id.login_layout),
                        getResources().getString(R.string.connected), Snackbar.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            }
            else{
                Snackbar.make(findViewById(R.id.login_layout),
                        getResources().getString(R.string.please_login), Snackbar.LENGTH_SHORT).show();
            }
        };

        // [START initialize_normal_login]
        btnSignIn.setOnClickListener((View v) -> {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            String email = emailId.getText().toString();
            String pwd = password.getText().toString();
            if(email.isEmpty()){
                emailId.setError(getResources().getString(R.string.enter_email));
                emailId.requestFocus();
            }
            else  if(pwd.isEmpty()){
                password.setError(getResources().getString(R.string.enter_pwd));
                password.requestFocus();
            }
            else  if(email.isEmpty() && pwd.isEmpty()){
                Snackbar.make(findViewById(R.id.login_layout),
                        getResources().getString(R.string.empty_fields), Snackbar.LENGTH_SHORT).show();
            }
            else  if(!(email.isEmpty() && pwd.isEmpty())){
                mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(
                        LoginActivity.this, task -> {
                    if(!task.isSuccessful()){
                        Snackbar.make(findViewById(R.id.login_layout),
                                getResources().getString(R.string.again_login), Snackbar.LENGTH_SHORT).show();
                    }
                    else{
                        Intent intToHome = new Intent(
                                LoginActivity.this,MainActivity.class);
                                startActivity(intToHome);
                        LogoutFragment.login = LogoutFragment.TypeLogin.LOGINFIREBASE;

                    }
                });
            }
            else{
                Snackbar.make(findViewById(R.id.login_layout),
                        getResources().getString(R.string.exist_error), Snackbar.LENGTH_SHORT).show();
            }
        });
        // [END initialize_normal_login]

        tvSignUp.setOnClickListener((View v) -> {
            Intent intSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intSignUp);
        });

        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAGFB, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAGFB, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAGFB, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]

        // [START initialize_twitter_login]
        mLoginButton = findViewById(R.id.buttonTwitterLogin);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAGTW, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAGTW, "twitterLogin:failure", exception);
                updateUI(null);
            }
        });
        // [END initialize_twitter_login]

        findViewById(R.id.buttonGoogle).setOnClickListener(this);
    }

    // [START on_start_check_user]
    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        updateUI(user);
    }
    // [END on_start_check_user]

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    // [START on_activity_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the Twitter login button.
        mLoginButton.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAGGO, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }
    // [END on_activity_result]

    @Override
    public void onClick(View v) {
        signInGoogle();
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAGFB, "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAGFB, "signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        LogoutFragment.login = LogoutFragment.TypeLogin.LOGINFACEBOOK;
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAGFB, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.login_layout), getResources().getString(R.string.auth_fail),
                                Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog();
                    // [END_EXCLUDE]
                });
    }
    // [END auth_with_facebook]

    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAGTW, "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAGTW, "signInWithCredential:success");
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            LogoutFragment.login = LogoutFragment.TypeLogin.LOGINTWITTER;
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAGTW, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_layout), getResources().getString(R.string.auth_fail),
                                    Snackbar.LENGTH_SHORT).show();

                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_twitter]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAGGO, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAGGO, "signInWithCredential:success");
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        LogoutFragment.login = LogoutFragment.TypeLogin.LOGINGOOGLE;
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAGGO, "signInWithCredential:failure", task.getException());
                        Snackbar.make(findViewById(R.id.login_layout), getResources().getString(R.string.auth_fail),
                                Snackbar.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    // [START_EXCLUDE]
                    hideProgressDialog();
                    // [END_EXCLUDE]
                }
            });
    }
    // [END auth_with_google]

    // [START signin]
    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user == null) {
            Log.d("DEBUG", "Usuario es null");
            findViewById(R.id.buttonGoogle).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonTwitterLogin).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonFacebookLogin).setVisibility(View.VISIBLE);
        }else{
            //Register user in database
            Log.d("DEBUG", "Usuario distinto de null");
            Log.d("DEBUG", "DisplayName: " + user.getDisplayName());
            Log.d("DEBUG", "Email: " + user.getEmail());
            Log.d("DEBUG", "UID: " + user.getUid());
            Profile.getInstance().registerUserInDataBase(user.getEmail(), user.getUid(), user.getDisplayName());
        }
    }

    public void signOutFacebook() {
        mFirebaseAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    public void signOutGoogle() {
        // Firebase sign out
        mFirebaseAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateUI(null);
                }
            });
    }

    public void signOutTwitter() {
        mFirebaseAuth.signOut();
        TwitterCore.getInstance().getSessionManager().clearActiveSession();

        updateUI(null);
    }

}
