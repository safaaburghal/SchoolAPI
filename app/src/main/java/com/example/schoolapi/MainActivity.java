package com.example.schoolapi;


import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listViewStudents;
    private ArrayList<String> studentNames;
    private ArrayAdapter<String> adapter;

    private static final String URL_STUDENTS = "http://192.168.1.117/school_api/get_students.php";
  //  private static final String URL_STUDENTS = "http://10.0.2.2/school_api/get_students.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewStudents = findViewById(R.id.listViewStudents);
        studentNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentNames);
        listViewStudents.setAdapter(adapter);

        fetchStudents();
    }

    private void fetchStudents() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_STUDENTS,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        studentNames.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject student = response.getJSONObject(i);
                                String fullName = student.getString("full_name");
                                studentNames.add(fullName);
                            }
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VolleyError", error.toString());
                    }
                }
        );

        queue.add(jsonArrayRequest);
        }
}