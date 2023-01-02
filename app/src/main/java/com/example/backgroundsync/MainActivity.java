package com.example.backgroundsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.backgroundsync.databinding.ActivityMainBinding;
import com.example.backgroundsync.network.RetrofitClient;
import com.example.backgroundsync.network.ServiceInterface;
import com.example.backgroundsync.room.RoomDB;
import com.example.backgroundsync.room.RoomData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = "com.example.backgroundsync.provider";
    public static final String ACCOUNT_TYPE = "com.example.backgroundsync";
    public static final String ACCOUNT = "placeholderaccount";
    public static final String TAG = "MainActivity";

    Account mAccount;
    ContentResolver mResolver;
    ServiceInterface serviceInterface;
    //SyncAdapter syncAdapter;

    List<RoomData> dataList = new ArrayList();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;
    Adapter adapter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //mAccount = CreateSyncAccount(this);
        //mResolver = getContentResolver();

        database = RoomDB.getInstance(this);
        dataList = database.roomDao().getAll();

        linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Adapter(MainActivity.this, dataList);
        binding.recyclerView.setAdapter(adapter);

        clickEvent();
        //alarmManager();
        workManger();

    }

    private void workManger() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(OperationWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);
        Log.d(TAG, "main activity");
    }

    @SuppressLint("ShortAlarm")
    /*private void alarmManager() {
        Intent intent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 30000L, pendingIntent);
        //alarmManager.set(Math.toIntExact(AlarmManager.INTERVAL_HALF_HOUR), System.currentTimeMillis(), pendingIntent);
        Toast.makeText(this, "Alarm set",Toast.LENGTH_LONG).show();
    }*/

    private void clickEvent() {
        binding.addBtn.setOnClickListener(view -> {
            String sText = binding.editText.getText().toString().trim();
            if (!sText.equals("")) {
                RoomData data = new RoomData();
                data.setText(sText);
                database.roomDao().insert(data);
                binding.editText.setText("");

                dataList.clear();
                dataList.addAll(database.roomDao().getAll());
                adapter.notifyDataSetChanged();
            }
        });

        binding.resetBtn.setOnClickListener(view -> {
            database.roomDao().reset(dataList);

            dataList.clear();
            dataList.addAll(database.roomDao().getAll());
            adapter.notifyDataSetChanged();
        });
    }

    /*public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        *//*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccountExplicitly(newAccount);
        }*//*
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

            //ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(newAccount, AUTHORITY, Bundle.EMPTY,60L); // seconds
            Log.d("logggggg", "*******CreateSyncAccount: successful ");
        } else {
            Log.d("logggggg", "*******CreateSyncAccount: error occurred ");
        }
        return newAccount;
    }

    public void onRefreshButtonClick(View v) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    public static void onSync(Account newAccount) {
        Log.d("logggggg", "call");
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(newAccount, AUTHORITY, settingsBundle);
    }*/
}