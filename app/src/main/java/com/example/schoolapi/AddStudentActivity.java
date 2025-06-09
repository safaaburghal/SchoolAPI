package com.example.schoolapi;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddStudentActivity extends AppCompatActivity {

    EditText etUsername, etPassword, etFullName, etEmail;
    DatePicker datePicker;
    Button btnSubmit;
    ImageView ivTogglePassword;
    boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_student);

        // ربط الفيوز
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        datePicker = findViewById(R.id.datePicker);
        btnSubmit = findViewById(R.id.btnSubmitStudent);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        // عند الضغط على زر الإرسال
        btnSubmit.setOnClickListener(v -> submitStudent());

        // إظهار/إخفاء كلمة المرور
        ivTogglePassword.setOnClickListener(v -> {
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.visibility); // أيقونة عين مغلقة
                isPasswordVisible = false;
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.visibility_off); // أيقونة عين مفتوحة
                isPasswordVisible = true;
            }
            etPassword.setSelection(etPassword.length()); // للمحافظة على مكان المؤشر
        });
    }

    private void submitStudent() {
        String url = "http://192.168.1.117/school_api/add_student.php"; // عدل الرابط حسب حالتك

        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // ✅ التحقق من الحقول الفارغة
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "⚠️ الرجاء تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ التحقق من أن التاريخ ليس في المستقبل
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        Calendar today = Calendar.getInstance();
        if (selectedDate.after(today)) {
            Toast.makeText(this, "⚠️ لا يمكن اختيار تاريخ في المستقبل", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ تكوين تاريخ الميلاد بصيغة YYYY-MM-DD
        String dob = year + "-" + (month + 1) + "-" + day;

        // ✅ إرسال البيانات
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Toast.makeText(AddStudentActivity.this, "✅ تم إضافة الطالب بنجاح!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AddStudentActivity.this, RegisterDashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getApplicationContext(), "Volley error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("password", password);
                params.put("full_name", fullName);
                params.put("email", email);
                params.put("dob", dob);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddStudentActivity.this, RegisterDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
