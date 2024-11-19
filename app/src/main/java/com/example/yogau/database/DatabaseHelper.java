package com.example.yogau.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.yogau.models.Course;
import com.example.yogau.models.CourseInstance;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "YogaApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_COURSE = "Course";
    public static final String TABLE_COURSE_INSTANCE = "CourseInstance";

    // Course table columns
    public static final String COURSE_ID = "id";
    public static final String COURSE_DAY_OF_WEEK = "dayOfWeek";
    public static final String COURSE_TIME = "time";
    public static final String COURSE_CAPACITY = "capacity";
    public static final String COURSE_DURATION = "duration";
    public static final String COURSE_PRICE = "price";
    public static final String COURSE_TYPE = "classType";
    public static final String COURSE_DESCRIPTION = "description";

    // CourseInstance table columns
    public static final String INSTANCE_ID = "id";
    public static final String INSTANCE_COURSE_ID = "courseId";
    public static final String INSTANCE_DATE = "date";
    public static final String INSTANCE_TEACHER = "teacher";
    public static final String INSTANCE_COMMENTS = "comments";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Course table
        String createCourseTable = "CREATE TABLE " + TABLE_COURSE + " (" +
                COURSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COURSE_DAY_OF_WEEK + " TEXT NOT NULL, " +
                COURSE_TIME + " TEXT NOT NULL, " +
                COURSE_CAPACITY + " INTEGER NOT NULL, " +
                COURSE_DURATION + " INTEGER NOT NULL, " +
                COURSE_PRICE + " REAL NOT NULL, " +
                COURSE_TYPE + " TEXT NOT NULL, " +
                COURSE_DESCRIPTION + " TEXT);";

        // Create CourseInstance table
        String createCourseInstanceTable = "CREATE TABLE " + TABLE_COURSE_INSTANCE + " (" +
                INSTANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                INSTANCE_COURSE_ID + " INTEGER NOT NULL, " +
                INSTANCE_DATE + " TEXT NOT NULL, " +
                INSTANCE_TEACHER + " TEXT NOT NULL, " +
                INSTANCE_COMMENTS + " TEXT, " +
                "FOREIGN KEY(" + INSTANCE_COURSE_ID + ") REFERENCES " + TABLE_COURSE + "(" + COURSE_ID + "));";

        db.execSQL(createCourseTable);
        db.execSQL(createCourseInstanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSE_INSTANCE);
        onCreate(db);
    }

    // Search courses by multiple fields
    public List<Course> searchCourses(String query) {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE + " WHERE " +
                COURSE_TYPE + " LIKE ? OR " +
                COURSE_DAY_OF_WEEK + " LIKE ? OR " +
                COURSE_CAPACITY + " LIKE ? OR " +
                COURSE_DURATION + " LIKE ? OR " +
                COURSE_PRICE + " LIKE ? ";

        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%", "%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                Course course = new Course(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY_OF_WEEK)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_CAPACITY)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_DURATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION))
                );
                courses.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courses;
    }

    // Search course instances by multiple fields
    public List<CourseInstance> searchCourseInstances(int courseId, String query) {
        List<CourseInstance> courseInstances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE_INSTANCE + " WHERE " +
                INSTANCE_COURSE_ID + " = ? AND (" +
                INSTANCE_DATE + " LIKE ? OR " +
                INSTANCE_TEACHER + " LIKE ? OR " +
                INSTANCE_COMMENTS + " LIKE ?)";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(courseId), "%" + query + "%", "%" + query + "%", "%" + query + "%"});
        if (cursor.moveToFirst()) {
            do {
                CourseInstance courseInstance = new CourseInstance(
                        cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_COURSE_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_TEACHER)),
                        cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_COMMENTS))
                );
                courseInstances.add(courseInstance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return courseInstances;
    }

    // Get a course by ID
    public Course getCourseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSE + " WHERE " + COURSE_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Course course = null;
        if (cursor.moveToFirst()) {
            course = new Course(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY_OF_WEEK)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_CAPACITY)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_DURATION)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION))
            );
        }
        cursor.close();
        db.close();
        Log.d(
                "DatabaseHelper",
                "Query: " + query + ", Parameters: " + course.getClassType()
        );
        return course;
    }

    public CourseInstance getCourseInstanceById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_COURSE_INSTANCE + " WHERE " + INSTANCE_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        CourseInstance courseInstance = null;

        if (cursor.moveToFirst()) {
            courseInstance = new CourseInstance(
                    cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_COURSE_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_TEACHER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_COMMENTS))


            );
        }
        cursor.close();
        db.close();
        return courseInstance;
    }


    public long insertCourse(Course  course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(COURSE_TIME, course.getTime());
        values.put(COURSE_CAPACITY, course.getCapacity());
        values.put(COURSE_DURATION, course.getDuration());
        values.put(COURSE_PRICE,  course.getPrice());
        values.put(COURSE_TYPE, course.getClassType());
        values.put(COURSE_DESCRIPTION, course.getDescription());

        long result = -1;
        try {
            result = db.insert(TABLE_COURSE, null, values);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting course: " + e.getMessage());
        } finally {
            db.close();  // Close the database after insertion is done
        }
        Log.d("DatabaseHelper", "Inserted course with ID: " + result);
        return result;
    }

    public long insertCourseInstance(CourseInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INSTANCE_COURSE_ID, instance.getCourseId());
        values.put(INSTANCE_DATE, instance.getDate());
        values.put(INSTANCE_TEACHER, instance.getTeacher());
        values.put(INSTANCE_COMMENTS, instance.getComments());

        return db.insert(TABLE_COURSE_INSTANCE, null, values);
    }

    public boolean updateCourse(Course course) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COURSE_DAY_OF_WEEK, course.getDayOfWeek());
        values.put(COURSE_TIME, course.getTime());
        values.put(COURSE_CAPACITY, course.getCapacity());
        values.put(COURSE_DURATION, course.getDuration());
        values.put(COURSE_PRICE,  course.getPrice());
        values.put(COURSE_TYPE, course.getClassType());
        values.put(COURSE_DESCRIPTION, course.getDescription());

        int rowsAffected = db.update(TABLE_COURSE, values, COURSE_ID+" = ?", new String[]{String.valueOf(course.getId())});
        return  rowsAffected > 0;
    }

    public int deleteCourse(int courseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all instances related to the course
        db.delete(TABLE_COURSE_INSTANCE, INSTANCE_COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
        // Delete the course
        return db.delete(TABLE_COURSE, COURSE_ID + "=?", new String[]{String.valueOf(courseId)});
    }

    public boolean updateCourseInstance(CourseInstance instance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(INSTANCE_COURSE_ID, instance.getCourseId());
        values.put(INSTANCE_DATE, instance.getDate());
        values.put(INSTANCE_TEACHER, instance.getTeacher());
        values.put(INSTANCE_COMMENTS, instance.getComments());

        int rowsAffected = db.update(TABLE_COURSE_INSTANCE, values, INSTANCE_ID + " = ?", new String[]{String.valueOf(instance.getId())});
        db.close();
        return rowsAffected > 0;
    }

    public int deleteCourseInstance(int instanceId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_COURSE_INSTANCE, INSTANCE_ID + " = ?", new String[]{String.valueOf(instanceId)});
        db.close();
        return rowsAffected;
    }

    public List<Course> getAllCourses(String query) {
        List<Course> courses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE + " WHERE " +
                COURSE_DAY_OF_WEEK + " LIKE ? OR " + COURSE_TIME + " LIKE ? OR " + COURSE_TYPE + " LIKE ?"+" ORDER BY "+ COURSE_ID+" DESC ";

        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"})) {
            if (cursor.moveToFirst()) {
                do {
                    Course course = new Course(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DAY_OF_WEEK)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TIME)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_CAPACITY)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(COURSE_DURATION)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(COURSE_PRICE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COURSE_TYPE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COURSE_DESCRIPTION))
                    );
                    courses.add(course);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while fetching courses: " + e.getMessage());
        }

        if (!courses.isEmpty()) {
            Log.d("DatabaseHelper", "First course day of week: " + courses.get(0).getDayOfWeek());
        } else {
            Log.d("DatabaseHelper", "No courses found matching the query.");
        }

        return courses;
    }

    public List<CourseInstance> getAllCourseInstances(int courseId) {
        List<CourseInstance> courseInstances = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_COURSE_INSTANCE + " WHERE " + INSTANCE_COURSE_ID + " = ?";

        try (Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(courseId)})) {
            if (cursor.moveToFirst()) {
                do {
                    CourseInstance courseInstance = new CourseInstance(
                            cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(INSTANCE_COURSE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_TEACHER)),
                            cursor.getString(cursor.getColumnIndexOrThrow(INSTANCE_COMMENTS))
                    );
                    courseInstances.add(courseInstance);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error while fetching course instances: " + e.getMessage());
        }

        if (!courseInstances.isEmpty()) {
            Log.d("DatabaseHelper", "First instance date: " + courseInstances.get(0).getDate());
        } else {
            Log.d("DatabaseHelper", "No course instances found for courseId: " + courseId);
        }

        return courseInstances;
    }


}
