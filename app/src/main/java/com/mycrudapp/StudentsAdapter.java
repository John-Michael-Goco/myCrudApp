package com.mycrudapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {

    private List<StudentModel> studentList;
    private OnDeleteClickListener onDeleteClickListener;
    private Context context;

    public StudentsAdapter(List<StudentModel> studentList, Context context) {
        this.studentList = studentList;
        this.context = context;
    }

    public interface OnDeleteClickListener {
        void onDelete(String studentID);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_item, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentModel student = studentList.get(position);
        holder.studentName.setText(student.getStudentName());
        holder.studentNumber.setText(student.getStudentNumber());

        // Pass all student data when editing a student
        holder.editStudent.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditStudent.class);
            intent.putExtra("studentID", student.getStudentNumber());
            intent.putExtra("name", student.getStudentName());
            intent.putExtra("address", student.getAddress());
            intent.putExtra("email", student.getEmail());
            intent.putExtra("phoneNumber", student.getPhoneNumber());
            intent.putExtra("course", student.getCourse());
            context.startActivity(intent);
        });

        // Delete student with confirmation dialog
        holder.deleteStudent.setOnClickListener(v -> {
            if (onDeleteClickListener != null) {
                new AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("Delete Student")
                        .setMessage("Are you sure you want to delete this student?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            onDeleteClickListener.onDelete(student.getStudentNumber());
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView studentName, studentNumber;
        ImageButton editStudent, deleteStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            studentName = itemView.findViewById(R.id.studentName);
            studentNumber = itemView.findViewById(R.id.studentNumber);
            editStudent = itemView.findViewById(R.id.studentEdit);
            deleteStudent = itemView.findViewById(R.id.studentDelete);
        }
    }
}
