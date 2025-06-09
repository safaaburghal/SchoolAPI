package com.example.schoolapi;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        LinearLayout container = findViewById(R.id.aboutContainer);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        container.startAnimation(animation);
        Button btnContact = findViewById(R.id.btnContactTeam);

        btnContact.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AboutAppActivity.this);
            builder.setTitle("Contact the Development Team");
            builder.setMessage("📧 Email: support@schoolapp.com\n📞 Phone: +970-599-123456\n\nWe're here to help you anytime.");
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.show();
        });

    }
}
