package com.example.walletku.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletku.R;
import com.example.walletku.database.RecordDAO;
import com.example.walletku.database.SqliteHelper;
import com.example.walletku.utility.Authentication;
import com.example.walletku.utility.RecordAdapter;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Double[] amounts;
    RecyclerView rvRecords;
    TextView tvIncome, tvExpense, tvTotalAmount;
    String USER_ACCOUNT;
    FirebaseUser USER;
    Toolbar toolbarHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        USER_ACCOUNT = getIntent().getStringExtra("USER_ACCOUNT");
        USER = getIntent().getParcelableExtra("USER");

        toolbarHome = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbarHome);

        rvRecords = findViewById(R.id.rvRecord);
        rvRecords.setLayoutManager(new LinearLayoutManager(this));
        RecordDAO recordDAO = new RecordDAO(new SqliteHelper(this));
        RecordAdapter adapter = new RecordAdapter(this, recordDAO.getRecords());
        rvRecords.setAdapter(adapter);

        amounts = new Double[3];
        amounts = recordDAO.getAmounts(this);
        tvIncome = findViewById(R.id.tvIncome);
        tvExpense = findViewById(R.id.tvExpense);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvIncome.setText(String.format("%s", amounts[0]));
        tvExpense.setText(String.format("%s", amounts[1]));
        tvTotalAmount.setText(String.format("%s", amounts[2]));

        ItemTouchHelper.SimpleCallback ithc = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int id = viewHolder.itemView.getId();
                adapter.deleteRecord(id, position);
                amounts = recordDAO.getAmounts(MainActivity.this);
                tvIncome.setText(String.format("%s", amounts[0]));
                tvExpense.setText(String.format("%s", amounts[1]));
                tvTotalAmount.setText(String.format("%s", amounts[2]));
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(ithc);
        itemTouchHelper.attachToRecyclerView(rvRecords);

        Button addRecordBtn = findViewById(R.id.btnAddRecord);
        Intent addRecordIntent = new Intent(this, AddRecord.class);
        addRecordIntent.putExtra("USER_ACCOUNT", USER_ACCOUNT);
        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(addRecordIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_signout) {
            Authentication auth = new Authentication();
                auth.signout();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}