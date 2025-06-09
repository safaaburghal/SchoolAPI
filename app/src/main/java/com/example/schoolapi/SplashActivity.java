package com.example.schoolapi;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 ثوانٍ
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // تأكد أن XML اسمه activity_splash.xml

        new Handler().postDelayed(() -> {
            // بعد انتهاء الوقت، الانتقال إلى الشاشة التالية (مثلاً LoginActivity)
           // Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
          //  startActivity(intent);
            finish();
        }, SPLASH_DURATION);
        ImageView logoImage = findViewById(R.id.logoImage);
        TextView appName = findViewById(R.id.appName);

        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Animation fadeInMove = AnimationUtils.loadAnimation(this, R.anim.fade_in_translate);
        appName.startAnimation(fadeInMove);

        logoImage.startAnimation(scaleUp);
        mediaPlayer = MediaPlayer.create(this, R.raw.nokia_style_tone);
        mediaPlayer.start();



    }
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

}
