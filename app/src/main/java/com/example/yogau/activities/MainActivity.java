package com.example.yogau.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogau.R;
import com.example.yogau.adapters.CourseAdapter;
import com.example.yogau.database.DatabaseHelper;
import com.example.yogau.firebase.FirebaseManager;
import com.example.yogau.models.Course;

import com.example.yogau.utils.NetworkUtils;
import com.google.firebase.FirebaseApp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

  private EditText searchBar;
  private RecyclerView coursesView;
  private CourseAdapter courseAdapter;
  private DatabaseHelper db;
  private List<Course> courses;

  private FirebaseManager firebaseManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    db = new DatabaseHelper(this);
    FirebaseApp.initializeApp(this);

    searchBar = findViewById(R.id.searchBar);
    coursesView = findViewById(R.id.coursesView);
    Button btnAddClass = findViewById(R.id.addCourse);


    firebaseManager = new FirebaseManager();

    courses = new ArrayList<>();
    loadCourse("");

    courseAdapter = new CourseAdapter(this, courses, new CourseAdapter.OnCourseClickListener() {
      @Override
      public void onCourseClick(Course course) {
        Intent intent = new Intent(MainActivity.this, ManageInstancesActivity.class);
        intent.putExtra("courseId", course.getId());
        intent.putExtra("classType", course.getClassType());
        startActivity(intent);
        Toast.makeText(MainActivity.this, "Clicked on " + course.getClassType(), Toast.LENGTH_SHORT).show();
      }

      @Override
      public void onEditClick(Course course) {
        Intent intent = new Intent(MainActivity.this, AddEditCourseActivity.class);
        intent.putExtra("COURSE_ID", course.getId());
        startActivity(intent);
      }

      @Override
      public void onDeleteClick(Course course) {
        showDeleteConfirmationDialog(course);
      }
    });

    coursesView.setLayoutManager(new LinearLayoutManager(this));
    coursesView.setAdapter(courseAdapter);

    searchBar.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        searchCourses(s.toString());
      }

      @Override
      public void afterTextChanged(Editable s) {}
    });

    btnAddClass.setOnClickListener(v -> {
      Intent intent = new Intent(MainActivity.this, AddEditCourseActivity.class);
      startActivity(intent);
    });


  }

  private void loadCourse(String query) {
    Executors.newSingleThreadExecutor().execute(() -> {
      List<Course> loadedCourses = db.getAllCourses(query);
      runOnUiThread(() -> {
        courses.clear();
        courses.addAll(loadedCourses);
        courseAdapter.notifyDataSetChanged();
      });
    });
  }

  private void searchCourses(String query) {
    Executors.newSingleThreadExecutor().execute(() -> {
      List<Course> searchedCourses = db.searchCourses(query);
      runOnUiThread(() -> {
        courses.clear();
        courses.addAll(searchedCourses);
        courseAdapter.notifyDataSetChanged();
      });
    });
  }

  private void showDeleteConfirmationDialog(Course course) {
    new AlertDialog.Builder(this)
            .setTitle("Delete Course")
            .setMessage("Are you sure you want to delete this course?")
            .setPositiveButton("Yes", (dialog, which) -> {

              if (NetworkUtils.isNetworkAvailable(MainActivity.this)) {
                db.deleteCourse(course.getId());
                firebaseManager.deleteCourse(course.getId());
                loadCourse("");
                Toast.makeText(MainActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
                Log.d("MainActivity,", "Internet available:" + NetworkUtils.isNetworkAvailable(MainActivity.this));
              }else{
                Toast.makeText(MainActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
                Toast.makeText(MainActivity.this, "Faild to delete", Toast.LENGTH_SHORT).show();

              }


            })
            .setNegativeButton("No", null)
            .show();
  }


  @Override
  protected void onResume() {
    super.onResume();
    loadCourse("");
  }
}