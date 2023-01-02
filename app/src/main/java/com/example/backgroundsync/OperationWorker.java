package com.example.backgroundsync;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.backgroundsync.model.Post;
import com.example.backgroundsync.network.RetrofitClient;
import com.example.backgroundsync.network.ServiceInterface;
import com.example.backgroundsync.room.RoomDB;
import com.example.backgroundsync.room.RoomData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class OperationWorker extends Worker {

    ServiceInterface serviceInterface;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    List<RoomData> dataList = new ArrayList();
    RoomDB database;
    Context context;
    public static final String TAG = "OperationWorker";

    public OperationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        database = RoomDB.getInstance(getApplicationContext());
        dataList = database.roomDao().getAll();
        serviceInterface = RetrofitClient.getClient(context).create(ServiceInterface.class);
        //compositeDisposable = new CompositeDisposable();
        //adapter = new Adapter(context, dataList, this);

        if (dataList.size() > 0) {
            uploadData(dataList);
            Log.d(TAG, "delete data");
        }
        Log.d(TAG, "do work");

        return Result.success();
    }

    private void uploadData(List<RoomData> roomData) {

        for (int i = 0; i < roomData.size(); i++) {
            RoomData data = roomData.get(i);
            Post post = new Post(data.getID(), data.getText());

            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/

            int finalI = i;

            compositeDisposable.add(serviceInterface.postData(post)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(postResponse -> {
                database.roomDao().delete(roomData.get(finalI));
            }, throwable -> {
                throwable.getMessage();
            }));
        }
    }
}
