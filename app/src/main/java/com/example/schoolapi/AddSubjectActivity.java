package com.example.schoolapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddSubjectActivity extends AppCompatActivity {
    EditText etSubjectName, etDescription;
    Button btnSubmit;
    Spinner spinnerTeachers;
    ArrayList<String> teacherList;
    ArrayList<Integer> teacherIds;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject);
        etSubjectName = findViewById(R.id.etSubjectName);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmitSubject);

        btnSubmit.setOnClickListener(v -> submitSubject());

        spinnerTeachers = findViewById(R.id.spinnerTeachers);
        teacherList = new ArrayList<>();
        teacherIds = new ArrayList<>();

        loadTeachers();
    }
    private void submitSubject() {
        String name = etSubjectName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        // التحقق من الحقول الفارغة
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "⚠️ Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // التحقق من أن المدرسين تم تحميلهم
        if (teacherIds.isEmpty()) {
            Toast.makeText(this, "⚠️ No teachers available. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        int teacherId = teacherIds.get(spinnerTeachers.getSelectedItemPosition());

        String url = "http://192.168.1.117/school_api/add_subject.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(this, "✅ Subject added!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, RegisterDashboardActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "❌ " + json.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                      //  Toast.makeText(this, "⚠️ JSON parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "⚠️ Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("description", description);
                params.put("teacher_id", String.valueOf(teacherId));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    private void loadTeachers() {
        String url = "http://192.168.1.117/school_api/get_teachers.php"; // غيّر حسب شبكتك

        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray teachersArray = jsonObject.getJSONArray("teachers");

                            for (int i = 0; i < teachersArray.length(); i++) {
                                JSONObject teacher = teachersArray.getJSONObject(i);
                                teacherList.add(teacher.getString("full_name"));
                                teacherIds.add(teacher.getInt("id"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, teacherList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerTeachers.setAdapter(adapter);

                        } else {
                            Toast.makeText(this, "Failed to load teachers", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void onBackPressed() {
        // يرجع للواجهة الرئيسية (Dashboard)
        Intent intent = new Intent(AddSubjectActivity.this, RegisterDashboardActivity.class);
        startActivity(intent);
        finish(); // يمنع الرجوع للواجهة الحالية مرة أخرى
    }
}
