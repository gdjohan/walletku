package com.example.walletku.utility;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletku.R;
import com.example.walletku.database.RecordDAO;
import com.example.walletku.database.SqliteHelper;
import com.example.walletku.model.Record;

import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    Context ctx;
    ArrayList<Record> records;

    public RecordAdapter(){ }
    public RecordAdapter(Context ctx, ArrayList<Record> records) {
        this.ctx = ctx;
        this.records = records;
    }

    @NonNull
    @Override
    public RecordAdapter.RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(ctx).inflate(R.layout.record_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordAdapter.RecordViewHolder holder, int position) {
        Record currRecord = records.get(position);
        if (currRecord == null) {
            Log.v("RecordAdapter", "Record is null");
            return;
        }
        Log.v("RecordAdapter", "Record is not null");
        TextView tvCategory = holder.itemView.findViewById(R.id.tvRecordCategory);
        TextView tvDate = holder.itemView.findViewById(R.id.tvRecordDate);
        TextView tvAmount = holder.itemView.findViewById(R.id.tvRecordAmount);

        holder.itemView.setId(Math.toIntExact(currRecord.getRecordID()));
        tvCategory.setText(currRecord.getRecordCategory());
        tvDate.setText(currRecord.getRecordDateString());
        tvAmount.setText(String.valueOf(currRecord.getRecordAmount()));
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    public void addRecord(Context mCtx, Record record) {
        RecordDAO recordDAO = new RecordDAO(new SqliteHelper(mCtx));
        recordDAO.addRecord(mCtx,record);
        if (records == null) {
            records = new ArrayList<>();
        } else {
            records.clear();
        }

        records.addAll(recordDAO.getRecords());
        notifyItemInserted(records.size() - 1);
    }

    public void deleteRecord(int recordId, int adapterPosition) {
        RecordDAO recordDAO = new RecordDAO(new SqliteHelper(this.ctx));
        recordDAO.deleteRecord(this.ctx, recordId);
        records.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
