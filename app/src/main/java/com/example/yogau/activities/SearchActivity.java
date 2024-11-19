package com.example.yogau.activities;


import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogau.R;
import com.example.yogau.database.DatabaseHelper;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearchTeacher, etSearchDate;
    private Button btnSearch;
    private ListView lvSearchResults;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        dbHelper = new DatabaseHelper(this);
        etSearchTeacher = findViewById(R.id.etSearchTeacher);
        etSearchDate = findViewById(R.id.etSearchDate);
        btnSearch = findViewById(R.id.btnSearch);
        lvSearchResults = findViewById(R.id.lvSearchResults);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchClasses();
            }
        });
    }

    private void searchClasses() {
        String teacher = etSearchTeacher.getText().toString() + "%";
        String date = etSearchDate.getText().toString();

        Cursor cursor;
        if (!date.isEmpty()) {
            cursor = dbHelper.getWritableDatabase().rawQuery(
                    "SELECT * FROM class_instances WHERE teacher LIKE ? AND date = ?",
                    new String[]{teacher, date});
        } else {
            cursor = dbHelper.getWritableDatabase().rawQuery(
                    "SELECT * FROM class_instances WHERE teacher LIKE ?",
                    new String[]{teacher});
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"date", "teacher"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0);
        lvSearchResults.setAdapter(adapter);
    }
}
