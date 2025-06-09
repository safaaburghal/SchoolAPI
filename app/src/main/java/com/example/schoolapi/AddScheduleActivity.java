package com.example.schoolapi;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddScheduleActivity extends AppCompatActivity {

    Spinner daySpinner, subjectSpinner, classSpinner;
    EditText startTimeEditText, endTimeEditText;
    Button saveButton, showScheduleButton;
    ArrayList<String> subjectList = new ArrayList<>();
    ArrayList<Integer> subjectIds = new ArrayList<>();
    ArrayList<String> classList = new ArrayList<>();
    ArrayList<Integer> classIds = new ArrayList<>();
    int selectedSubjectId = -1;
    int selectedClassId = -1;

    TableLayout tableSchedule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        daySpinner = findViewById(R.id.spinnerDay);
        subjectSpinner = findViewById(R.id.spinnerSubject);
        classSpinner = findViewById(R.id.spinnerClass);
        startTimeEditText = findViewById(R.id.editStartTime);
        endTimeEditText = findViewById(R.id.editEndTime);
        saveButton = findViewById(R.id.btnSubmitSchedule);
        showScheduleButton = findViewById(R.id.btnShowClassSchedule);
        tableSchedule = findViewById(R.id.tableSchedule);

        setupDaySpinner();
        loadSubjectsFromServer();
        loadClassesFromServer();

        startTimeEditText.setOnClickListener(v -> showTimePicker(startTimeEditText));
        endTimeEditText.setOnClickListener(v -> showTimePicker(endTimeEditText));

        saveButton.setOnClickListener(v -> saveSchedule());
        showScheduleButton.setOnClickListener(v -> showClassSchedule());
    }

    private void setupDaySpinner() {
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
    }

    private void loadSubjectsFromServer() {
        String url = "http://10.0.2.2/school_api/get_subject.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray subjects = new JSONArray(response);
                subjectList.clear();
                subjectIds.clear();
                for (int i = 0; i < subjects.length(); i++) {
                    JSONObject subject = subjects.getJSONObject(i);
                    subjectList.add(subject.getString("subject_name"));
                    subjectIds.add(subject.getInt("subject_id"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjectList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjectSpinner.setAdapter(adapter);
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddScheduleActivity.this, RegisterDashboardActivity.class);
        startActivity(intent);
        finish();
    }
    private void loadClassesFromServer() {
        String url = "http://10.0.2.2/school_api/load_classes.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray classes = new JSONArray(response);
                classList.clear();
                classIds.clear();
                for (int i = 0; i < classes.length(); i++) {
                    JSONObject cls = classes.getJSONObject(i);
                    String className = cls.getString("class_name");
                    int classId = cls.getInt("class_id");
                    classList.add(className);
                    classIds.add(classId);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(adapter);
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to load classes", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(request);
    }

    private void showTimePicker(EditText editText) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            editText.setText(time);
        }, hour, minute, true);
        dialog.show();
    }

    private void saveSchedule() {
        int subjectPosition = subjectSpinner.getSelectedItemPosition();
        selectedSubjectId = (subjectPosition >= 0) ? subjectIds.get(subjectPosition) : -1;

        int classPosition = classSpinner.getSelectedItemPosition();
        if(classPosition < 0) {
            Toast.makeText(this, "Please select a class", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedClassId = classIds.get(classPosition);

        String day = daySpinner.getSelectedItem().toString();
        String startTime = startTimeEditText.getText().toString();
        String endTime = endTimeEditText.getText().toString();

        if (startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please select start and end times", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2/school_api/add_schedule.php";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(this, "Schedule Saved", Toast.LENGTH_SHORT).show();
        }, error -> {
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (selectedSubjectId != -1) {
                    params.put("subject_id", String.valueOf(selectedSubjectId));
                }
                params.put("day_of_week", day);
                params.put("start_time", startTime);
                params.put("end_time", endTime);
                params.put("class_id", String.valueOf(selectedClassId));
                return params;
            }
        };

        queue.add(request);
    }

    private void showClassSchedule() {
        int classPosition = classSpinner.getSelectedItemPosition();
        if (classPosition < 0) {
            Toast.makeText(this, "Please select a class first", Toast.LENGTH_SHORT).show();
            return;
        }

        int classId = classIds.get(classPosition);
        String url = "http://10.0.2.2/school_api/get_schedule_by_class.php?class_id=" + classId;

        tableSchedule.removeAllViews();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray scheduleArray = new JSONArray(response);

                TableRow headerRow = new TableRow(this);
                String[] headers = {"Day", "Start", "End", "Subject"};
                for (String h : headers) {
                    TextView tv = new TextView(this);
                    tv.setText(h);
                    tv.setPadding(12, 8, 12, 8);
                    tv.setTypeface(null, Typeface.BOLD);
                    headerRow.addView(tv);
                }
                tableSchedule.addView(headerRow);

                for (int i = 0; i < scheduleArray.length(); i++) {
                    JSONObject obj = scheduleArray.getJSONObject(i);
                    TableRow row = new TableRow(this);

                    String[] values = {
                            obj.getString("day_of_week"),
                            obj.getString("start_time"),
                            obj.getString("end_time"),
                            obj.optString("subject_name", "-"),
//                            obj.optString("teacher_name", "-")
                    };

                    for (String v : values) {
                        TextView tv = new TextView(this);
                        tv.setText(v);
                        tv.setPadding(12, 8, 12, 8);
                        row.addView(tv);
                    }
                    tableSchedule.addView(row);
                }

            } catch (JSONException e) {
                Toast.makeText(this, "Failed to parse schedule", Toast.LENGTH_SHORT).show();
            }
        }, error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show());

        queue.add(request);
        Log.d("DEBUG", "classIds: " + classIds.toString());
        Log.d("DEBUG", "Selected Position: " + classSpinner.getSelectedItemPosition());
}
}