package com.example.schoolapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterDashboardActivity extends AppCompatActivity {

    private Button btnAddStudent, btnAddSubject, btnBuildStudentSchedule, btnBuildTeacherSchedule, btnManageUsers;
    private TextView tvTitle, tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_dashborad);

        // ربط الفيوهات مع الكود
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnAddSubject = findViewById(R.id.btnAddSubject);
        btnBuildStudentSchedule = findViewById(R.id.btnBuildStudentSchedule);
        btnBuildTeacherSchedule = findViewById(R.id.btnBuildTeacherSchedule);
        btnManageUsers = findViewById(R.id.btnManageUsers);

        tvTitle = findViewById(R.id.tvTitle); // لازم تعطي الـ TextView ID في XML مثلاً: android:id="@+id/tvTitle"
        tvWelcome = findViewById(R.id.tvWelcome); // نفس الشي

        btnAddStudent.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterDashboardActivity.this, AddStudentActivity.class);
            startActivity(intent);
        });
        btnAddSubject.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterDashboardActivity.this, AddSubjectActivity.class);
            startActivity(intent);
        });



    }
}
