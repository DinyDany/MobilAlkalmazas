package com.example.alkgame;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private ImageView dot;
    private TextView playerScoreTV;
    //private TextView playerHpTV;
    private int score;
    private double timeSpeed;
    private CountDownTimer idozito;
    private double liveTime=3000;
    private double spawnTime= 950;
   // private int hp=3;
    MediaPlayer mediaPlayer;
    MediaPlayer mediaPlayerBack;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore playerData = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DocumentReference playerRef = playerData.collection("Players").document(userId);

        progressBar = findViewById(R.id.progressBar);
        //playerHpTV = findViewById(R.id.playerHp);

        dot = findViewById(R.id.dot);

        playerScoreTV = findViewById(R.id.pontokSzama);
        score = 0;


        mediaPlayerBack = MediaPlayer.create(getApplicationContext(), R.raw.drive);
        mediaPlayerBack.setVolume(0.7f, 0.7f);
        mediaPlayerBack.start();

        //Első pötty generálása és megjelenítése
        idozito = new CountDownTimer((long) getLiveTime(), ((long) getSpawnTime())) { // az időzítő liveTime/1000 másodpercig tart, és spawnTim/1000 másodpercenként generál pöttyöt
            public void onTick(long timeLeft) {
                Random r = new Random();
                int spawnWidth = findViewById(R.id.spawn).getWidth();
                int spawnHeight = findViewById(R.id.spawn).getHeight();
                int x = r.nextInt(spawnWidth - dot.getWidth());
                int y = r.nextInt(spawnHeight - dot.getHeight());
                dot.setX(x + findViewById(R.id.spawn).getX());
                dot.setY(y + findViewById(R.id.spawn).getY());
                dot.setVisibility(View.VISIBLE);


                int progress = (int) ((timeLeft / (float) getLiveTime()) * 100);
                progressBar.setProgress(progress);
            }

            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Lejárt az idő! Pontok száma: " + score, Toast.LENGTH_LONG).show();
                finish();
            }
        }.start();



        dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    score++;

                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pop);
                    mediaPlayer.start();

                    timeSpeed = Math.log(score*1.5);
                    setSpawnTime(spawnTime - timeSpeed);
                    setLiveTime(liveTime + (timeSpeed*2));
                    playerScoreTV.setText("Pontok: " + score);
                    dot.setVisibility(View.GONE);

                    if (idozito!=null){
                        idozito.cancel();
                    }


                    idozito = new CountDownTimer((long) getLiveTime(), ((long) getSpawnTime())) {
                        public void onTick(long timeLeft) {
                            Random r = new Random();
                            int spawnWidth = findViewById(R.id.spawn).getWidth();
                            int spawnHeight = findViewById(R.id.spawn).getHeight();
                            int x = r.nextInt(spawnWidth - dot.getWidth());
                            int y = r.nextInt(spawnHeight - dot.getHeight());
                            dot.setX(x + findViewById(R.id.spawn).getX());
                            dot.setY(y + findViewById(R.id.spawn).getY());
                            dot.setVisibility(View.VISIBLE);

                            int progress = (int) ((timeLeft / (float) getLiveTime()) * 100);
                            progressBar.setProgress(progress);
                        }

                        public void onFinish() {
                            // játék vége
                            Toast.makeText(getApplicationContext(), "Lejárt az idő! Pontok száma: " + score, Toast.LENGTH_LONG).show();
                            playerRef.get().addOnSuccessListener(documentSnapshot -> {
                                Player player = documentSnapshot.toObject(Player.class);
                                if (player.getScore() < score) {
                                    player.setScore(score);
                                    playerRef.update("score", score)
                                            .addOnSuccessListener(aVoid -> Log.d(LOG_TAG, "A pontszám sikeresen frissítve."))
                                            .addOnFailureListener(e -> Log.e(LOG_TAG, "Hiba történt a pontszám frissítése közben.", e));

                                }
                            });
                            finish();

                        }
                    }.start();

                    Log.e(LOG_TAG, "Spawin time" + spawnTime);
                    Log.e(LOG_TAG, "Life time" + liveTime);
                }


        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (idozito!=null){
            idozito.cancel();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaPlayerBack != null) {
            mediaPlayerBack.release();
            mediaPlayerBack = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayerBack != null && mediaPlayerBack.isPlaying()) {
            mediaPlayerBack.pause();
        }
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (idozito != null) {
            idozito.cancel();
            idozito = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(mediaPlayerBack != null && !mediaPlayerBack.isPlaying()) {
            mediaPlayerBack.start();
        }
    }

    public void setSpawnTime(double spawnTime) {
        this.spawnTime = spawnTime;
    }


    public void setLiveTime(double liveTime) {
        this.liveTime = liveTime;
    }

    public double getLiveTime() {
        return liveTime;
    }

    public double getSpawnTime() {
        return spawnTime;
    }
}
