package com.example.schoolapi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class SendAssignmentActivity extends AppCompatActivity {

    Spinner spinnerClasses, spinnerSubjects;
    EditText etTitle, etDescription;
    Button btnSubmitAssignment;
    TextView tvDueDate;

    ArrayList<String> classList = new ArrayList<>();
    ArrayList<Integer> classIds = new ArrayList<>();

    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> subjectIds = new ArrayList<>();

    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_assignment);

        spinnerClasses = findViewById(R.id.spinnerClass);
        spinnerSubjects = findViewById(R.id.spinnerSubject);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);
        tvDueDate = findViewById(R.id.tvDueDate);

        loadClasses();
        loadSubjects();

        tvDueDate.setOnClickListener(v -> showDatePicker());

        btnSubmitAssignment.setOnClickListener(v -> submitAssignment());
    }

    private void loadClasses() {
        String url = "http://192.168.1.117/school_api/get_classes.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray array = json.getJSONArray("classes");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            classList.add(obj.getString("class_name"));
                            classIds.add(obj.getInt("class_id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerClasses.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading classes", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void loadSubjects() {
        String url = "http://192.168.1.117/school_api/get_subjects.php";

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray array = json.getJSONArray("subjects");

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            subjectList.add(obj.getString("subject_name"));
                            subjectIds.add(obj.getInt("subject_id"));
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerSubjects.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading subjects", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDate = year + "-" + (month + 1) + "-" + day;
            tvDueDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitAssignment() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(spinnerClasses.getSelectedItemPosition());
        int subjectId = subjectIds.get(spinnerSubjects.getSelectedItemPosition());
        int teacherId = 3; // لاحقًا يُؤخذ من الجلسة أو الـ login

        String url = "http://192.168.1.117/school_api/add_assignment.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "✅ Assignment sent!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this, json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "JSON error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("description", description);
                params.put("due_date", selectedDate);
                params.put("class_id", String.valueOf(classId));
                params.put("subject_id", String.valueOf(subjectId));
                params.put("uploaded_by", String.valueOf(teacherId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}
