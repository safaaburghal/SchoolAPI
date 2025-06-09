package com.example.schoolapi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubmittedAssignmentsAdapter extends RecyclerView.Adapter<SubmittedAssignmentsAdapter.ViewHolder> {

    List<SubmittedAssignment> list;

    public SubmittedAssignmentsAdapter(List<SubmittedAssignment> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtStudent, txtDate, txtLink;

        public ViewHolder(View view) {
            super(view);
            txtStudent = view.findViewById(R.id.txtStudentName);
            txtDate = view.findViewById(R.id.txtSubmissionDate);
            txtLink = view.findViewById(R.id.txtFileLink);
        }
    }

    @Override
    public SubmittedAssignmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_submission, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubmittedAssignmentsAdapter.ViewHolder holder, int position) {
        SubmittedAssignment submission = list.get(position);
        holder.txtStudent.setText("Student: " + submission.studentName);
        holder.txtDate.setText("Date: " + submission.submittedAt);
        holder.txtLink.setText("File: " + submission.fileUrl);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}