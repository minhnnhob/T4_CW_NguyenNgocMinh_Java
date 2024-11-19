package com.example.yogau.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogau.R;
import com.example.yogau.models.CourseInstance;

import java.util.List;

public class CourseInstanceAdapter extends RecyclerView.Adapter<CourseInstanceAdapter.CourseInstanceViewHolder> {

    private List<CourseInstance> courseInstances;
    private OnInstanceClickListener clickListener;

    public interface OnInstanceClickListener {
        void onInstanceClick(CourseInstance instance);
        void onEditClick(CourseInstance instance);
        void onDeleteClick(CourseInstance instance);
    }

    public CourseInstanceAdapter(List<CourseInstance> courseInstances, OnInstanceClickListener clickListener) {
        this.courseInstances = courseInstances;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CourseInstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_instance, parent, false);
        return new CourseInstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseInstanceViewHolder holder, int position) {
        CourseInstance instance = courseInstances.get(position);
        holder.bind(instance, clickListener);
    }

    @Override
    public int getItemCount() {
        return courseInstances.size();
    }

    public class CourseInstanceViewHolder extends RecyclerView.ViewHolder {
        private TextView instanceDate;
        private TextView instanceTeacher;
        private TextView instanceComments;
        private Button btnEditInstance;
        private Button btnDeleteInstance;



        public CourseInstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            instanceDate = itemView.findViewById(R.id.instanceDate);
            instanceTeacher = itemView.findViewById(R.id.instanceTeacher);
            instanceComments = itemView.findViewById(R.id.instanceComments);
            btnEditInstance = itemView.findViewById(R.id.btn_edit_instance);
            btnDeleteInstance = itemView.findViewById(R.id.btn_delete_instance);
        }

        public void bind(CourseInstance instance, OnInstanceClickListener clickListener) {

            instanceDate.setText("Date: "+ instance.getDate());
            instanceTeacher.setText("Teacher: "+instance.getTeacher());
            instanceComments.setText("Comment: "+ instance.getComments());

            itemView.setOnClickListener(v -> clickListener.onInstanceClick(instance));
            btnEditInstance.setOnClickListener(v -> clickListener.onEditClick(instance));
            btnDeleteInstance.setOnClickListener(v -> clickListener.onDeleteClick(instance));
        }
    }
}