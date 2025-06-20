package com.example.walletku.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.walletku.R;
import com.example.walletku.database.RecordDAO;
import com.example.walletku.database.SqliteHelper;
import com.example.walletku.model.Record;
import com.example.walletku.utility.RecordAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddRecord extends AppCompatActivity {

    Spinner spinnerType, spinnerCategory;
    EditText etCategory, etAmount, etDate, etNote;
    Button btnDone;
    RecordAdapter adapter;
    String USER_ACCOUNT;
    RecordDAO recordDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        USER_ACCOUNT = getIntent().getStringExtra("USER_ACCOUNT");

        //create new record
        Record record = new Record();
        recordDAO = new RecordDAO(new SqliteHelper(this));
        adapter = new RecordAdapter(this, recordDAO.getRecords());

        //find spinner view
        spinnerType = findViewById(R.id.spinnerType);
        //create arrayAdapter for spinner
        ArrayAdapter<CharSequence> typeArrayAdapter = ArrayAdapter.createFromResource(this, R.array.type_dropdown, android.R.layout.simple_spinner_item);
        typeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set spinner adapter to arrayAdapter
        spinnerType.setAdapter(typeArrayAdapter);
        //set record type based on selected item
        spinnerType.setOnItemSelectedListener(spinnerItemSelected(record));

        //find record category
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> catArrayAdapter = ArrayAdapter.createFromResource(this, R.array.category_dropdown, android.R.layout.simple_spinner_item);
        catArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set spinner adapter to arrayAdapter
        spinnerCategory.setAdapter(catArrayAdapter);
        //set record category based on selected item
        spinnerCategory.setOnItemSelectedListener(getSelectedCategory(record));

        //set record amount
        etAmount = findViewById(R.id.etAmount);

        //set record date
        etDate = findViewById(R.id.etDate);
        Calendar calendar = Calendar.getInstance();
        etDate.setOnClickListener(getDate(calendar, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())));

        etNote = findViewById(R.id.etNote);

        btnDone = findViewById(R.id.btnDone);
        adapter = new RecordAdapter();
        btnDone.setOnClickListener(addRecord(record));

    }

    @NonNull
    private static AdapterView.OnItemSelectedListener getSelectedCategory(Record record) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                record.setRecordCategory(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @NonNull
    private View.OnClickListener addRecord(Record record) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateField()){
                    record.setRecordAmount(Double.parseDouble(etAmount.getText().toString()));
                    record.setRecordDate(etDate.getText().toString());
                    record.setRecordAccount(USER_ACCOUNT);
                    if (!etNote.getText().toString().isEmpty())
                        record.setRecordNote(etNote.getText().toString());
                    else record.setRecordNote(null);
                    adapter.addRecord(v.getContext(), record);
                    Intent homeIntent = new Intent(v.getContext(), MainActivity.class);
                    startActivity(homeIntent);
                };
            }
        };
    }

    private boolean validateField() {
        if(etAmount.getText().toString().isEmpty()) {
            etAmount.setError("Enter amount");
            return false;
        }
        if(etDate.getText().toString().isEmpty()) {
            etDate.setError("Enter date");
            return false;
        }
        return true;
    }

    @NonNull
    private static AdapterView.OnItemSelectedListener spinnerItemSelected(Record record) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                record.setRecordType(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }

    @NonNull
    private View.OnClickListener getDate(Calendar calendar, SimpleDateFormat dateFormat) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AddRecord.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    String formattedDate = dateFormat.format(calendar.getTime());

                    etDate.setText(formattedDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        };
    }
}