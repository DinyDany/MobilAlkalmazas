package com.example.alkgame;

import android.app.AlarmManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {
    private static final String LOG_TAG = MenuActivity.class.getName();
    private FirebaseUser user;

    private FirebaseFirestore firestore;
    private CollectionReference players;
    private  int limit= 10;
    private PlayerAdapter mAdapter;

    List<Player> playersData = new ArrayList<>();

    private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        user = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();
        players = firestore.collection("Players");

        mAdapter = new PlayerAdapter(playersData,this);

        RecyclerView recyclerView= findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);

        if (user!=null){
            Log.d(LOG_TAG,"Belépett felhasználó");
        } else {
            Log.d(LOG_TAG,"Nem belépett felhasználó");
            finish();
        }

        queryData().addOnCompleteListener(this::updatePlayersData);


        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReciver,filter);

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

    }

    //Ha a telefont feltesszük töltőre akkor több játékos pontszámát listázza ki
    BroadcastReceiver powerReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action==null){
                return;
            }

            switch (action){
                case Intent.ACTION_POWER_CONNECTED:
                    limit = 20;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    limit = 10;
                    break;
            }
            queryData().addOnCompleteListener(MenuActivity.this::updatePlayersData);
        }
    };

    public void updatePlayersData(Task<QuerySnapshot> task) {
        if (task.isSuccessful()) {
            playersData.clear();
            for (QueryDocumentSnapshot doc : task.getResult()) {
                Player player = doc.toObject(Player.class);
                playersData.add(player);
            }
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d(LOG_TAG, "Hiba: ", task.getException());
        }
    }

    public Task<QuerySnapshot> queryData() {
        return players.orderBy("score", Query.Direction.DESCENDING).limit(limit).get();
    }



    public void gameStart(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        setAlarmManager();
        startActivity(intent);
    }

    private void setAlarmManager(){
        long repeat = AlarmManager.INTERVAL_HALF_DAY;
        long tTime = SystemClock.elapsedRealtime()+repeat;

        Intent intent = new Intent(this,AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,tTime,repeat,pendingIntent);

    }

    public void reload(View view) {
        queryData().addOnCompleteListener(this::updatePlayersData);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReciver);
    }
}