package com.example.backgroundsync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.backgroundsync.network.RetrofitClient;
import com.example.backgroundsync.network.ServiceInterface;

import java.io.IOException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver contentResolver;
    ServiceInterface serviceInterface;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
        serviceInterface = RetrofitClient.getClient(context).create(ServiceInterface.class);
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        contentResolver = context.getContentResolver();
    }

    /*
     * Specify the code you want to run in the sync adapter. The entire
     * sync adapter runs in a background thread, so you don't have to set
     * up your own background processing.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        /*
         * Put the data transfer code here.
         */
        Log.d("logggggg", "onPerformSync()");
        fetchData();

        //showNotification(getContext(), "The query", "let'see");
    }

    private void fetchData() {
        compositeDisposable.add(serviceInterface.getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(postModels -> {
                    postModels.get(0); // todo modify
                }, throwable -> {
                    throwable.getMessage();
                }));
    }



    /*private void showNotification(Context context, String title, String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "RandomName";
            String description = "RandomDescription";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ID1", name, importance);
            channel.setDescription(description);
            // Add the channel
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "ID1")
                //used notification icon for testing purpose
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title).setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setPriority(NotificationCompat.PRIORITY_DEFAULT).setVibrate(new long[0]);


        // Show the notification
        NotificationManagerCompat.from(context).notify(1, builder.build());
        Log.e("WorkManager", "Notification shown");
    }*/
}

