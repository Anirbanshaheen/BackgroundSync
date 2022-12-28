package com.example.backgroundsync;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.backgroundsync.room.RoomDB;
import com.example.backgroundsync.room.RoomData;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    List<RoomData> dataList;
    private Context context;
    private RoomDB database;
    InterfaceAdapter interfaceAdapter;

    public Adapter(Context context, List<RoomData> dataList, InterfaceAdapter interfaceAdapter) {
        this.context = context;
        this.dataList = dataList;
        this.interfaceAdapter = interfaceAdapter;
        notifyDataSetChanged();
    }

    public Adapter(Context context, List<RoomData> dataList) {
        this.context = context;
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomData data = dataList.get(position);
        database = RoomDB.getInstance(context);

        holder.textView.setText(data.getText());
        holder.btEdit.setOnClickListener(view -> {
            RoomData data1 = dataList.get(holder.getAdapterPosition());
            int sID = data1.getID();
            String sText = data1.getText();
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_update);
            int width = WindowManager.LayoutParams.MATCH_PARENT;
            int height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
            dialog.show();

            EditText editText = dialog.findViewById(R.id.updateET);
            Button btnUpdate = dialog.findViewById(R.id.updateBtn);

            editText.setText(sText);
            btnUpdate.setOnClickListener(view1 -> {
                dialog.dismiss();

                String updateText = editText.getText().toString().trim();
                database.roomDao().update(sID, updateText);

                dataList.clear();
                dataList.addAll(database.roomDao().getAll());
                notifyDataSetChanged();
            });
        });
        holder.btDelete.setOnClickListener(view -> {
            RoomData data1 = dataList.get(holder.getAdapterPosition());
            database.roomDao().delete(data1);
            int position1 = holder.getAdapterPosition();
            dataList.remove(position1);
            notifyItemRemoved(position1);
            notifyItemRangeChanged(position1, dataList.size());
        });
    }

    public void removeSingleItem(RoomData mainData, int position) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.d("data", "RemoveSingleItem Position"+ position+ " Data"+ mainData.getText());
            if (dataList.size() > 0) {
                dataList.remove(position);
                notifyItemRemoved(position);
                interfaceAdapter.observeDataInterface(mainData); // For pass data in interface method
            }
        }, 2000);
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView btEdit, btDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            btEdit = itemView.findViewById(R.id.editIV);
            btDelete = itemView.findViewById(R.id.deleteIV);
        }
    }

    public interface InterfaceAdapter {
        void observeDataInterface(RoomData mainData);
    }
}
