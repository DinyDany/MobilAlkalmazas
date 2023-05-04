package com.example.alkgame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int RC_SING_IN = 69;

    EditText userNameET;
    EditText passwordET;

    private SharedPreferences preferences;
    private GoogleSignInClient mGoogleSignIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userNameET = findViewById(R.id.editTextUserName);
        passwordET = findViewById(R.id.editTextPassword);
        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);

        fAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignIn = GoogleSignIn.getClient(this,gso);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RC_SING_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "Googel felhaszánló "+ account.getId());
                firebaseAuthWithGoogel(account.getIdToken());
            }catch (ApiException e){
                Log.w(LOG_TAG," Googel bejelentkezés hiba"+e );
            }
        }
    }

    private void firebaseAuthWithGoogel(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        fAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Sikeres belépés");
                    startMenu();
                }else {
                    Log.d(LOG_TAG, "Hiba: "+task.getException().getMessage());
                }
            }
        });
    }


    public void login(View view) {
        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();

        try {
            fAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Log.d(LOG_TAG, "Sikeres belépés");
                        startMenu();
                    }else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        Log.d(LOG_TAG, "Hiba: "+task.getException().getMessage());
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    //Most nem használjuk a google accountos beléptetést
    public void loginWithGoogle(View view) {
           Intent singInIntent = mGoogleSignIn.getSignInIntent();
           startActivityForResult(singInIntent,RC_SING_IN);

    }

    private void startMenu(){
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
    }
    public void register(View view) {
        Intent register = new Intent(this,RegisterActivity.class);
        register.putExtra("SECRET_KEY",99);
        startActivity(register);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString("email", userNameET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }



}