package com.example.yogau.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogau.R;
import com.example.yogau.adapters.CourseInstanceAdapter;
import com.example.yogau.database.DatabaseHelper;
import com.example.yogau.firebase.FirebaseManager;
import com.example.yogau.models.Course;
import com.example.yogau.models.CourseInstance;
import com.example.yogau.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class ManageInstancesActivity extends AppCompatActivity {
    private int courseId;
    private DatabaseHelper db;
    private CourseInstanceAdapter adapter;
    private List<CourseInstance> courseInstances;
    private Button btnAddInstance;
    private RecyclerView courseInstancesView;
    private TextView courseTypeInstance;
    private EditText searchInstances;

    private FirebaseManager firebaseManager;

    private Button btnBack;

    private TextView dayOfWeekCInfor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_instance);

        // Get course ID passed from MainActivity
        courseId = getIntent().getIntExtra("courseId", -1);
        Log.d("ManageInstancesActivity", "Received course ID: " + courseId);

        db = new DatabaseHelper(this);
        firebaseManager= new FirebaseManager();

        courseInstancesView = findViewById(R.id.courseInstanceList);
        btnAddInstance = findViewById(R.id.addCourseInstance);
        courseTypeInstance = findViewById(R.id.courseTypeInstance);
        searchInstances = findViewById(R.id.searchInstance);

        btnBack = findViewById(R.id.btnBackToMain);

        dayOfWeekCInfor = findViewById(R.id.dayOfWeekInfor);
        TextView courseTimeInfor = findViewById(R.id.courseTimeInfor);
        TextView courseCapacityInfor = findViewById(R.id.courseCapacityInfor);
        TextView courseDurationInfor = findViewById(R.id.courseDurationInfor);
        TextView coursePriceInfor = findViewById(R.id.coursePriceInfor);
        TextView courseDescriptionInfor = findViewById(R.id.courseDescriptionInfor);

        courseDescriptionInfor.setMovementMethod(new ScrollingMovementMethod());

        dayOfWeekCInfor.setText("Day of week: "+db.getCourseById(courseId).getDayOfWeek());
        courseTimeInfor.setText("Time : "+db.getCourseById(courseId).getTime());
        courseCapacityInfor.setText("Capacity : " + db.getCourseById(courseId).getCapacity());
        courseDurationInfor.setText("Duration : " + db.getCourseById(courseId).getDuration() + " mins");
        coursePriceInfor.setText("Price: $" + db.getCourseById(courseId).getPrice());
        courseDescriptionInfor.setText("Description: "+ db.getCourseById(courseId).getDescription());

        courseTypeInstance.setText(db.getCourseById(courseId).getClassType());

        courseInstances = new ArrayList<>();
        loadInstances();

        adapter = new CourseInstanceAdapter(courseInstances, new CourseInstanceAdapter.OnInstanceClickListener() {
            @Override
            public void onInstanceClick(CourseInstance instance) {

            }

            @Override
            public void onEditClick(CourseInstance instance) {
                Intent intent = new Intent(ManageInstancesActivity.this, AddInstanceActivity.class);
                intent.putExtra("INSTANCE_ID", instance.getId());
                intent.putExtra("COURSE_ID", courseId);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(CourseInstance instance) {
                showDeleteConfirmationDialog(instance);
            }
        });

        searchInstances.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchInstances(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnAddInstance.setOnClickListener(v -> {
            Intent intent = new Intent(ManageInstancesActivity.this, AddInstanceActivity.class);
            intent.putExtra("COURSE_ID", courseId);
            startActivity(intent);
        });

        courseInstancesView.setLayoutManager(new LinearLayoutManager(this));
        courseInstancesView.setAdapter(adapter);

        btnAddInstance.setOnClickListener(v -> {
            Intent intent = new Intent(ManageInstancesActivity.this, AddInstanceActivity.class);
            intent.putExtra("COURSE_ID", courseId);
            startActivity(intent);
        });

        btnBack.setOnClickListener(
           v->finish()
        );

    }

    private void loadInstances() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CourseInstance> loadedInstances = db.getAllCourseInstances(courseId);
            runOnUiThread(() -> {
                courseInstances.clear();
                courseInstances.addAll(loadedInstances);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void searchInstances(String query) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<CourseInstance> searchedInstances = db.searchCourseInstances(courseId, query);
            runOnUiThread(() -> {
                courseInstances.clear();
                courseInstances.addAll(searchedInstances);
                adapter.notifyDataSetChanged();
            });
        });
    }

    private void showDeleteConfirmationDialog(CourseInstance instance) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Instance")
                .setMessage("Are you sure you want to delete this instance?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (NetworkUtils.isNetworkAvailable(ManageInstancesActivity.this)) {
                        db.deleteCourseInstance(instance.getId());
                        firebaseManager.deleteCourseInstance(instance.getId(), instance.getCourseId());
                        loadInstances();
                        Toast.makeText(ManageInstancesActivity.this, "Instance deleted", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(ManageInstancesActivity.this));
                    }else{
                        Toast.makeText(ManageInstancesActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ManageInstancesActivity.this, "Faild to delete", Toast.LENGTH_SHORT).show();

                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

       @Override
    protected void onResume() {
        super.onResume();
        loadInstances();
    }
}