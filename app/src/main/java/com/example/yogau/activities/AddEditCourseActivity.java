package com.example.yogau.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogau.R;
import com.example.yogau.database.DatabaseHelper;
import com.example.yogau.firebase.FirebaseManager;
import com.example.yogau.models.Course;
import com.example.yogau.utils.NetworkUtils;

import java.util.Calendar;

public class AddEditCourseActivity extends AppCompatActivity {

    private EditText  courseTime, courseCapacity, courseDuration, coursePrice, courseClassType, courseDescription;
    private TextView titleAddEdit;
    private Spinner courseDay;
    private DatabaseHelper databaseHelper;
    private FirebaseManager firebaseManager;
    private int courseId = -1; // Default -1 indicates "Add Mode"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_course);

        // Initialize views and database helper
        databaseHelper = new DatabaseHelper(this);
        firebaseManager = new FirebaseManager();

        courseDay = findViewById(R.id.course_day);
        courseTime = findViewById(R.id.course_time);
        courseCapacity = findViewById(R.id.course_capacity);
        courseDuration = findViewById(R.id.course_duration);
        coursePrice = findViewById(R.id.course_price);
        courseClassType = findViewById(R.id.course_class_type);
        courseDescription = findViewById(R.id.course_description);
        titleAddEdit = findViewById(R.id.addEditText);
        Button saveCourseButton = findViewById(R.id.save_course_button);
        Button btnCancel = findViewById(R.id.btnCancel);



        // Check if we are in Edit Mode by looking for COURSE_ID in the intent
        if (getIntent().hasExtra("COURSE_ID")) {
            courseId = getIntent().getIntExtra("COURSE_ID", -1);
            loadCourseData(courseId);  // Load course data for editing
        }

        if (courseId == -1) {
            titleAddEdit.setText("Add Course");
        } else {
            titleAddEdit.setText("Edit Course");
        }

        saveCourseButton.setOnClickListener(v -> saveCourse());
        btnCancel.setOnClickListener(v -> finish());

        // Set up the TimePickerDialog
        courseTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void loadCourseData(int courseId) {
        // Retrieve the course data from the database and populate fields
        Course course = databaseHelper.getCourseById(courseId);
        if (course != null) {
            int spinnerPosition = ((ArrayAdapter) courseDay.getAdapter()).getPosition(course.getDayOfWeek());
            courseDay.setSelection(spinnerPosition);
            courseTime.setText(course.getTime());
            courseCapacity.setText(String.valueOf(course.getCapacity()));
            courseDuration.setText(String.valueOf(course.getDuration()));
            coursePrice.setText(String.valueOf(course.getPrice()));
            courseClassType.setText(course.getClassType());
            courseDescription.setText(course.getDescription());

        }
    }

    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minuteOfHour);
                    courseTime.setText(time);
                },
                hour,
                minute,
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    private void saveCourse() {
        // Retrieve entered data
        // Retrieve entered data
        String dayOfWeek = courseDay.getSelectedItem().toString().trim();
        String time = courseTime.getText().toString().trim();
        String capacityStr = courseCapacity.getText().toString().trim();
        String durationStr = courseDuration.getText().toString().trim();
        String priceStr = coursePrice.getText().toString().trim();
        String classType = courseClassType.getText().toString().trim();
        String description = courseDescription.getText().toString().trim();

        // Validate inputs
        if (dayOfWeek.isEmpty() || time.isEmpty() || capacityStr.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty() || classType.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity;
        int duration;
        double price;
        try {
            capacity = Integer.parseInt(capacityStr);
            duration = Integer.parseInt(durationStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for capacity, duration, and price", Toast.LENGTH_SHORT).show();
            return;
        }
        // Create or update the course based on the mode
        Course course = null ;
        if (courseId == -1) {
            long result = -1;
            if (NetworkUtils.isNetworkAvailable(AddEditCourseActivity.this)) {
                course = new Course(dayOfWeek, time ,capacity, duration, price, classType, description);
                 result = databaseHelper.insertCourse(course);
                Log.d("AddEditCourseActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(AddEditCourseActivity.this));
            }else{

                Toast.makeText(AddEditCourseActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();

            }

            if (result == -1) {
                Toast.makeText(this, "Failed to add course", Toast.LENGTH_SHORT).show();
            } else {
                course.setId((int) result);

                firebaseManager.addCourse(course);
                Toast.makeText(this, "Course added successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        } else {

            boolean result = false;
            if (NetworkUtils.isNetworkAvailable(AddEditCourseActivity.this)) {
                course = new Course(courseId, dayOfWeek, time, capacity, duration, price, classType, description);
                 result = databaseHelper.updateCourse(course);
                firebaseManager.updateCourse(course);// Update in database
                Log.d("AddEditCourseActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(AddEditCourseActivity.this));
            }else{
                Toast.makeText(AddEditCourseActivity.this, "No Internet Available", Toast.LENGTH_SHORT).show();

            }

            if (result) {

                Toast.makeText(this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update course", Toast.LENGTH_SHORT).show();

            }
        }

    }
}
