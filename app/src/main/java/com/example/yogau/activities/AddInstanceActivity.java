package com.example.yogau.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogau.R;
import com.example.yogau.database.DatabaseHelper;
import com.example.yogau.firebase.FirebaseManager;
import com.example.yogau.models.Course;
import com.example.yogau.models.CourseInstance;
import com.example.yogau.utils.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddInstanceActivity extends AppCompatActivity {
    private EditText dateInstancep;
    private EditText teacherIstance;
    private EditText commentInstance;
    private Button saveInstanceButton;

    private DatabaseHelper databaseHelper;

    private int courseId;
    private int instanceId = -1;

    private TextView addEditIsantceT;
    private Button btnCancel;

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instance);

        databaseHelper = new DatabaseHelper(this);
        firebaseManager = new FirebaseManager();

        dateInstancep = findViewById(R.id.dateInstance);
        teacherIstance = findViewById(R.id.teacherInstance);
        commentInstance = findViewById(R.id.commentInstance);
        saveInstanceButton = findViewById(R.id.saveInstanceButton);

        addEditIsantceT = findViewById(R.id.addEditIsantce);
        btnCancel = findViewById(R.id.btn_cancel);


        courseId = getIntent().getIntExtra("COURSE_ID", -1);

        // Check if we are in Edit Mode by looking for INSTANCE_ID in the intent
        if (getIntent().hasExtra("INSTANCE_ID")) {
            instanceId = getIntent().getIntExtra("INSTANCE_ID", -1);
            loadInstanceData(instanceId);  // Load instance data for editing
        }

        if (instanceId == -1) {
            addEditIsantceT.setText("Add Instance");
        }else {
            addEditIsantceT.setText("Edit Instance");
        }
        btnCancel.setOnClickListener(v -> finish());



        saveInstanceButton.setOnClickListener(v -> saveInstance());
        // Set up the DatePickerDialog
        dateInstancep.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        String courseDayOfWeek = databaseHelper.getCourseById(courseId).getDayOfWeek();
        int targetDayOfWeek = getDayOfWeek(courseDayOfWeek);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,

                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    int selectedDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                    if (selectedDayOfWeek != targetDayOfWeek) {
                        Toast.makeText(this, "Please select a " + courseDayOfWeek, Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                        dateInstancep.setText(sdf.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void loadInstanceData(int instanceId) {
        // Retrieve the instance data from the database and populate fields
        CourseInstance instance = databaseHelper.getCourseInstanceById(instanceId);

        if (instance != null) {
            dateInstancep.setText(instance.getDate());
            teacherIstance.setText(instance.getTeacher());
            commentInstance.setText(instance.getComments());
        }
    }

    private void saveInstance() {
        String date = dateInstancep.getText().toString();
        String teacher = teacherIstance.getText().toString();
        String comments = commentInstance.getText().toString();

        if (date.isEmpty() || teacher.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        CourseInstance Addinstance ;
        long result = -1;
        if(instanceId == -1){

            if (NetworkUtils.isNetworkAvailable(AddInstanceActivity.this)) {
                Addinstance = new CourseInstance(courseId, date, teacher, comments);
                result = databaseHelper.insertCourseInstance(Addinstance);
                Addinstance.setId((int) result);
                firebaseManager.addCourseInstance(Addinstance);
                Log.d("AddEditCourseActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(AddInstanceActivity.this));
            }else{
                Toast.makeText(AddInstanceActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();

            }

            if (result == -1) {
                Toast.makeText(this, "Failed to add instance", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "Instance added successfully", Toast.LENGTH_SHORT).show();
                finish();

            }

        }else {

            if (NetworkUtils.isNetworkAvailable(AddInstanceActivity.this)) {
                CourseInstance instance = new CourseInstance(instanceId, courseId, date, teacher, comments);
                result = databaseHelper.updateCourseInstance(instance)? instanceId : -1;
                firebaseManager.updateCourseInstance(instance);
                Log.d("AddEditCourseActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(AddInstanceActivity.this));
            }else{
                Toast.makeText(AddInstanceActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();

            }

            if (result == -1){
                Toast.makeText(this, "Failed to update instance", Toast.LENGTH_SHORT).show();
            }else {

                Toast.makeText(this, "Instance updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }


    }

    private int getDayOfWeek(String dayOfWeek) {
        switch (dayOfWeek.toLowerCase(Locale.ROOT)) {
            case "sunday":
                return Calendar.SUNDAY;
            case "monday":
                return Calendar.MONDAY;
            case "tuesday":
                return Calendar.TUESDAY;
            case "wednesday":
                return Calendar.WEDNESDAY;
            case "thursday":
                return Calendar.THURSDAY;
            case "friday":
                return Calendar.FRIDAY;
            case "saturday":
                return Calendar.SATURDAY;
            default:
                return -1;
        }
    }
}