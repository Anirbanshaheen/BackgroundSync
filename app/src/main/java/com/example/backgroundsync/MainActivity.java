package com.example.backgroundsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.backgroundsync.databinding.ActivityMainBinding;
import com.example.backgroundsync.room.RoomDB;
import com.example.backgroundsync.room.RoomData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String AUTHORITY = "com.example.backgroundsync.provider";
    public static final String ACCOUNT_TYPE = "com.example.backgroundsync";
    public static final String ACCOUNT = "placeholderaccount";

    Account mAccount;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    ContentResolver mResolver;
    SyncAdapter syncAdapter;

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

        mAccount = CreateSyncAccount(this);
        mResolver = getContentResolver();

        database = RoomDB.getInstance(this);
        dataList = database.roomDao().getAll();

        linearLayoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new Adapter(MainActivity.this, dataList);
        binding.recyclerView.setAdapter(adapter);

        clickEvent();

        /*SyncAdapterManager syncAdapterManager = new SyncAdapterManager(this);
        syncAdapterManager.beginPeriodicSync();*/

    }

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

    public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            accountManager.removeAccountExplicitly(newAccount);
        }*/
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {

            //ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(newAccount, AUTHORITY, Bundle.EMPTY,10L); // seconds
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
    }
}