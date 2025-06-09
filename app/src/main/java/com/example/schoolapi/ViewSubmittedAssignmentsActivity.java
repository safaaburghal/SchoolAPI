package com.example.schoolapi;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.*;
import java.util.*;

public class ViewSubmittedAssignmentsActivity extends AppCompatActivity {

    Spinner spinnerAssignment;
    RecyclerView recyclerView;
    ArrayList<String> assignmentTitles = new ArrayList<>();
    ArrayList<Integer> assignmentIds = new ArrayList<>();
    SubmittedAssignmentsAdapter adapter;
    List<SubmittedAssignment> submittedList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_submitted_assignments);

        spinnerAssignment = findViewById(R.id.spinnerAssignment);
        recyclerView = findViewById(R.id.recyclerSubmittedAssignments);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubmittedAssignmentsAdapter(submittedList);
        recyclerView.setAdapter(adapter);

        loadAssignments();

        spinnerAssignment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedId = assignmentIds.get(position);
                loadSubmissions(selectedId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadAssignments() {
        String url = "http://192.168.1.117/school_api/get_assignments.php";

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray array = json.getJSONArray("assignments");

                assignmentTitles.clear();
                assignmentIds.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    assignmentTitles.add(obj.getString("title"));
                    assignmentIds.add(obj.getInt("assignment_id"));
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, assignmentTitles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAssignment.setAdapter(adapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to load assignments", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }

    private void loadSubmissions(int assignmentId) {
        String url = "http://192.168.1.117/school_api/get_submissions.php?assignment_id=" + assignmentId;

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                JSONArray array = json.getJSONArray("submissions");

                submittedList.clear();

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    submittedList.add(new SubmittedAssignment(
                            obj.getString("student_name"),
                            obj.getString("file_url"),
                            obj.getString("submitted_at")
                    ));
                }

                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this, "Failed to load submissions", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}
