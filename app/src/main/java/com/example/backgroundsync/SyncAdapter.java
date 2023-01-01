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
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.backgroundsync.model.Post;
import com.example.backgroundsync.network.RetrofitClient;
import com.example.backgroundsync.network.ServiceInterface;
import com.example.backgroundsync.room.RoomDB;
import com.example.backgroundsync.room.RoomData;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    ContentResolver contentResolver;
    ServiceInterface serviceInterface;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<RoomData> dataList = new ArrayList();
    RoomDB database;
    public static final String TAG = "SyncAdapter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        serviceInterface = RetrofitClient.getClient(context).create(ServiceInterface.class);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync()");

        dataList.clear();
        try {
            database = RoomDB.getInstance(getContext());
            dataList.addAll(database.roomDao().getAll());
            if (dataList.size() > 0) {
                Log.d(TAG, "getLocalData List size: " + dataList.size());
                uploadData(dataList);
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void uploadData(List<RoomData> roomData) {
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_notifications)
                .setContentTitle("Upload..")
                .setAllowSystemGeneratedContextualActions(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        for (int i = 0; i < roomData.size(); i++) {
            RoomData data = roomData.get(i);
            Post post = new Post(data.getID(), data.getText());
            builder.setProgress(roomData.size(), i, false);
            notificationManager.notify(2, builder.build());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int finalI = i;

            compositeDisposable.add(serviceInterface.postData(post)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(postResponse -> {
                        database.roomDao().delete(roomData.get(finalI));
                    }, throwable -> {
                        throwable.getMessage();
                    }));

            /*RetrofitClient.getApiServices()
                    .postData(post)
                    .enqueue(new Callback<PostResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<PostResponse> call,
                                               @NonNull Response<PostResponse> response) {
                            if (response.isSuccessful()) {
                                Log.d(logDebug, "call -: RetrofitClient onResponse ...");
                                studentDB.studentDao().deleteStudent(students.get(finalI));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                            Log.d(logDebug, "call -: RetrofitClient onFailure ...");
                        }
                    });*/
        }

        builder.setProgress(0, 0, false);
        builder.clearActions();
        builder.setAutoCancel(false);
        builder.setContentIntent(null);
        notificationManager.notify(2, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notification Channel";
            String description = "this is for Test Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
}

