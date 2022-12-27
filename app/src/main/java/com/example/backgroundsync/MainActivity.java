package com.example.backgroundsync;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.backgroundsync.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Constants
    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "com.example.backgroundsync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.example.backgroundsync";
    // The account name
    public static final String ACCOUNT = "placeholderaccount";
    // Instance fields
    Account mAccount;

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;
    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    SyncAdapter syncAdapter;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAccount = CreateSyncAccount(this);
        //mResolver = getContentResolver();

        SyncAdapterManager syncAdapterManager = new SyncAdapterManager(this);
        syncAdapterManager.beginPeriodicSync();

    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */


        /*if (accountManager.addAccountExplicitly(newAccount, null, null)) {

        } else {

        }
        return newAccount;*/
        return newAccount;
    }

    /**
     * Respond to a button click by calling requestSync(). This is an
     * asynchronous operation.
     * <p>
     * This method is attached to the refresh button in the layout
     * XML file
     *
     * @param v The View associated with the method call,
     *          in this case a Button
     */
    public void onRefreshButtonClick(View v) {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
    }

    public static void onSync(Account newAccount) {
        Log.d("logggggg", "call");
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(newAccount, AUTHORITY, settingsBundle);
    }
}