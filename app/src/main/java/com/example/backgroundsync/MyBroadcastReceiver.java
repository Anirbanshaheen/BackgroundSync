package com.example.backgroundsync;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/*public class MyBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mp;
    Account account;
    public static final String AUTHORITY = "com.example.backgroundsync.provider";
    public static final String ACCOUNT_TYPE = "com.example.backgroundsync";
    public static final String ACCOUNT = "placeholderaccount";
    public static final String TAG = "logggggg";
    @Override
    public void onReceive(Context context, Intent intent) {
        mp=MediaPlayer.create(context, R.raw.alarm);
        mp.start();
        account = createSyncAccount(context);
        Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
    }

    private Account createSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        account = new Account(ACCOUNT, ACCOUNT_TYPE);

        try {
            if (accountManager.addAccountExplicitly(account, null, null)) {
                Log.d(TAG, "createSyncAccount: ");
                return null;
            } else {
                syncImmediately(account);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }

    private void syncImmediately(Account account) {
        Log.d(TAG, "syncImmediately: ");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(account, AUTHORITY, bundle);
    }
}*/
