package com.example.yogau.firebase;

import android.util.Log;

import com.example.yogau.models.Course;
import com.example.yogau.models.CourseInstance;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private DatabaseReference databaseReference;

    public FirebaseManager() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://yogau-dd011-default-rtdb.asia-southeast1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference();
    }


    public void addCourse(Course course) {
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("courses/" + "course"+course.getId(), course);
        databaseReference.updateChildren(courseMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course added successfully");
            } else {
                Log.e("FirebaseManager", "Course addition failed", task.getException());
            }
        });
    }

    public void updateCourse(Course course) {
        String courseId = String.valueOf(course.getId());
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("courses/" + "course"+courseId, course);
        databaseReference.updateChildren(courseMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course updated successfully");
            } else {
                Log.e("FirebaseManager", "Course update failed", task.getException());
            }
        });
    }

    public void deleteCourse(int courseId) {
        Log.d("FirebaseManager", "Deleting course with ID: " + courseId);
        databaseReference.child("courses").child("course"+String.valueOf(courseId)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course deleted successfully");
            } else {
                Log.e("FirebaseManager", "Course deletion failed", task.getException());
            }
        });
    }


    public void addCourseInstance(CourseInstance courseInstance) {
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("courses/"+"course"+courseInstance.getCourseId()+"/"+"courses_Instance/" + "courseInstance"+courseInstance.getId(), courseInstance);
        databaseReference.updateChildren(courseMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course Instance added successfully");
            } else {
                Log.e("FirebaseManager", "Course Instance addition failed", task.getException());
            }
        });

    }

    public void updateCourseInstance(CourseInstance courseInstance) {
        String courseId = String.valueOf(courseInstance.getId());
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("courses/"+"course"+courseInstance.getCourseId()+"/"+"courses_Instance/" + "courseInstance"+courseId, courseInstance);
        databaseReference.updateChildren(courseMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course updated successfully");
            } else {
                Log.e("FirebaseManager", "Course update failed", task.getException());
            }
        });
    }

    public void deleteCourseInstance(int instanceId,int courseId) {
        Log.d("FirebaseManager", "Deleting course with ID: " + courseId);
        databaseReference.child("courses").child("course"+String.valueOf(courseId)).child("courses_Instance").child("courseInstance"+String.valueOf(instanceId)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("FirebaseManager", "Course deleted successfully");
            } else {
                Log.e("FirebaseManager", "Course deletion failed", task.getException());
            }
        });
    }

}

