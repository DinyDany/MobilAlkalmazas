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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final int SECRERT_KEY = 99;
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final String LOG_TAG = RegisterActivity.class.getName();
    EditText userNameET;
    EditText userEmailET;
    EditText userPasswordET;
    EditText userPasswordConfET;
    private SharedPreferences preferences;
    private FirebaseAuth fAuth;

    private FirebaseFirestore firestore;
    private CollectionReference players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


       int secret_key = getIntent().getIntExtra("SECRET_KEY",0);
       if(secret_key!=99){
           finish();
       }
        userNameET = findViewById(R.id.editTextUserName);
        userEmailET = findViewById(R.id.userEmail);
        userPasswordET = findViewById(R.id.editTextPassword);
        userPasswordConfET = findViewById(R.id.passwordSec);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String email =  preferences.getString("email","");
        String password = preferences.getString("password","");

        userEmailET.setText(email);
        userPasswordET.setText(password);
        userPasswordConfET.setText(password);
        fAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();
        players = firestore.collection("Players");
    }



    public void register(View view) {
        String userName = userNameET.getText().toString();
        String email = userEmailET.getText().toString();
        String password = userPasswordET.getText().toString();
        String passwordConf = userPasswordConfET.getText().toString();

        Pattern pattern = Pattern.compile("[a-zA-Z0-9áéíóöőúüűÁÉÍÓÖŐÚÜŰ\\s]+");
        Matcher matcher = pattern.matcher(userName);

        if (userName.equals("")){
            Toast.makeText(getApplicationContext(), "Nem adtál meg nevet!", Toast.LENGTH_LONG).show();
            return;
        }
        if (email.equals("")){
            Toast.makeText(getApplicationContext(), "Nem adtál meg e-mail címet!", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.equals("")){
            Toast.makeText(getApplicationContext(), "Nem adtál meg jelszót!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(passwordConf)){
            Log.e(LOG_TAG,"Nem megegyező jelszavak!");
            Toast.makeText(getApplicationContext(), "Nem megegyező jelszavak!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!matcher.matches()){
            Toast.makeText(getApplicationContext(), "Nem megfelelő formátum!", Toast.LENGTH_LONG).show();
            return;
        }



        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = fAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();
                    players.document(userId).set(new Player(userName, email, 0));
                    startMenu();
                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void cancel(View view) {
        finish();
    }

    private void startMenu(){
        Intent intent = new Intent(this,MenuActivity.class);
        startActivity(intent);
    }

    public RegisterActivity() {
        super();
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