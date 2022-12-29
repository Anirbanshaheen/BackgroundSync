package com.example.backgroundsync;

import static android.content.Context.ACCOUNT_SERVICE;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/*class SyncAdapterManager {

    private static final String TAG = SyncAdapterManager.class.getSimpleName();
    public static final String AUTHORITY = "com.example.backgroundsync.provider";
    public static final String ACCOUNT_TYPE = "com.example.backgroundsync";
    public static final String ACCOUNT = "placeholderaccount";

    private Account account;
    private Context context;

    SyncAdapterManager(final Context context) {
        this.context = context;
        account = new Account(ACCOUNT, ACCOUNT_TYPE);
    }

    @SuppressWarnings ("MissingPermission")
    void beginPeriodicSync() {
        final AccountManager accountManager = (AccountManager) context
                .getSystemService(ACCOUNT_SERVICE);

        if (!accountManager.addAccountExplicitly(account, null, null)) {
            account = accountManager.getAccountsByType(ACCOUNT_TYPE)[0];
        }

        setAccountSyncable();

        ContentResolver.addPeriodicSync(account, AUTHORITY,
                Bundle.EMPTY, 1000);


        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
    }

    void syncImmediately() {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(account, AUTHORITY, settingsBundle);

    }

    private void setAccountSyncable() {
        if (ContentResolver.getIsSyncable(account, AUTHORITY) == 0) {
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        }
    }

}*/
