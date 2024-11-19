package com.example.yogau.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogau.R;
import com.example.yogau.models.Course;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseClickListener clickListener;
    private Context context;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
        void onEditClick(Course course);
        void onDeleteClick(Course course);
    }

    public CourseAdapter(Context context, List<Course> courseList, OnCourseClickListener clickListener) {
        this.context = context;
        this.courseList = courseList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course, clickListener);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCourseList(List<Course> newCourses) {
        this.courseList = newCourses;
        notifyDataSetChanged();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView courseName;

        private TextView courseTime;
        private TextView courseCapacity;
        private TextView courseDuration;
        private TextView coursePrice;
        private TextView dayOfWeek;
        private Button btnEdit;
        private Button btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);

            courseTime = itemView.findViewById(R.id.course_time_item);
            courseCapacity = itemView.findViewById(R.id.course_capacity);
            courseDuration = itemView.findViewById(R.id.course_duration);
            coursePrice = itemView.findViewById(R.id.course_price);
            dayOfWeek = itemView.findViewById(R.id.day_of_week);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);

        }

        @SuppressLint("SetTextI18n")
        public void bind(Course course, OnCourseClickListener clickListener) {
            courseName.setText(course.getClassType());
            dayOfWeek.setText(course.getDayOfWeek());
            courseTime.setText("Time: "+course.getTime());
            courseCapacity.setText("Capacity: " + course.getCapacity());
            courseDuration.setText("Duration: " + course.getDuration() + " mins");
            coursePrice.setText("Price: $" + course.getPrice());

            itemView.setOnClickListener(v -> clickListener.onCourseClick(course));
            btnEdit.setOnClickListener(v -> clickListener.onEditClick(course));
            btnDelete.setOnClickListener(v -> clickListener.onDeleteClick(course));
        }
    }
}