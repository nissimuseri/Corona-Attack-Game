package com.example.virusattack;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class LevelActivity extends AppCompatActivity {

    private final int[] levels = new int[8];
    private final TextView[] tvLevelsArr = new TextView[8];
    SharedPreferences preferences,spSound;
    private MediaPlayer mediaPlayer;
    private boolean mute;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        preferences = getSharedPreferences("open levels", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        spSound = getSharedPreferences("sound", MODE_PRIVATE);
        mute = spSound.getBoolean("mute", false);
        final SharedPreferences.Editor soundEditor = spSound.edit();

        for (int i = 0; i < 8; i++) {
            /*Unlocks the first level and locks the rest for the first time played*/
            if (!preferences.getBoolean("has played already", false)) {
                editor.putInt("level" + (i + 1), (i == 0) ? 1 : 0);
                editor.commit();
            }
            levels[i] = preferences.getInt("level" + (i + 1), 0);
        }

        // Back Button
        TextView backBtn = findViewById(R.id.backBtn_tv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LevelActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tvLevelsArr[0] = findViewById(R.id.ivLevel1);
        tvLevelsArr[1] = findViewById(R.id.ivLevel2);
        tvLevelsArr[2] = findViewById(R.id.ivLevel3);
        tvLevelsArr[3] = findViewById(R.id.ivLevel4);
        tvLevelsArr[4] = findViewById(R.id.ivLevel5);
        tvLevelsArr[5] = findViewById(R.id.ivLevel6);
        tvLevelsArr[6] = findViewById(R.id.ivLevel7);
        tvLevelsArr[7] = findViewById(R.id.ivLevel8);


        /*Unlocks the levels that the player passed already*/
        for (int i = 0; i < levels.length; i++) {
            if (levels[i] != 0) {
                tvLevelsArr[i].setText(Integer.toString(i + 1));
                tvLevelsArr[i].setClickable(true);

            } else {
                tvLevelsArr[i].setBackgroundResource(R.drawable.lock);
                tvLevelsArr[i].setText("");
                tvLevelsArr[i].setClickable(false);
            }
        }


        // Volume control
        final ImageView volumeIv = findViewById(R.id.volume_iv_levels);
        volumeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mute) {
                    mute = true;
                    mediaPlayer.pause();
                    soundEditor.putBoolean("mute", true);
                    volumeIv.setImageResource(R.drawable.musicoff3);
                } else { // when muted
                    mute = false;
                    mediaPlayer.start();
                    soundEditor.putBoolean("mute", false);
                    volumeIv.setImageResource(R.drawable.music3);
                }
                soundEditor.commit();
            }
        });
        if (!spSound.getBoolean("mute", false)) {
            mediaPlayer = StartActivity.mediaPlayer;
            mediaPlayer.start();
        }
        else {
            mediaPlayer = MediaPlayer.create(this, R.raw.gamesound);
            StartActivity.mediaPlayer = mediaPlayer;
            volumeIv.setImageResource(R.drawable.musicoff3);
        }
    }



    public void onClick(final View view) {
        final Intent intent = new Intent(LevelActivity.this, MainActivity.class);
        switch (view.getId()) {
            case R.id.ivLevel1: // Level 1
                intent.putExtra("bgResId", R.drawable.israel);
                intent.putExtra("pointTarget", 200);
                intent.putExtra("speed", 0);
                intent.putExtra("level", 1);
                break;

            case R.id.ivLevel2: // Level 2
                intent.putExtra("bgResId", R.drawable.sweden);
                intent.putExtra("pointTarget", 200);
                intent.putExtra("speed", 1);
                intent.putExtra("level", 2);
                break;

            case R.id.ivLevel3: // Level 3
                intent.putExtra("bgResId", R.drawable.china);
                intent.putExtra("pointTarget", 300);
                intent.putExtra("speed", 2);
                intent.putExtra("level", 3);
                break;

            case R.id.ivLevel4: // Level 4
                intent.putExtra("bgResId", R.drawable.germany);
                intent.putExtra("pointTarget", 300);
                intent.putExtra("speed", 3);
                intent.putExtra("level", 4);
                break;

            case R.id.ivLevel5: // Level 5
                intent.putExtra("bgResId", R.drawable.italy);
                intent.putExtra("pointTarget", 400);
                intent.putExtra("speed", 4);
                intent.putExtra("level", 5);
                break;

            case R.id.ivLevel6: // Level 6
                intent.putExtra("bgResId", R.drawable.russia);
                intent.putExtra("pointTarget", 400);
                intent.putExtra("speed", 5);
                intent.putExtra("level", 6);
                break;

            case R.id.ivLevel7: // Level 7
                intent.putExtra("bgResId", R.drawable.brazil);
                intent.putExtra("pointTarget", 500);
                intent.putExtra("speed", 6);
                intent.putExtra("level", 7);
                break;

            case R.id.ivLevel8: // Level 8
                intent.putExtra("bgResId", R.drawable.usa);
                intent.putExtra("pointTarget", 500);
                intent.putExtra("speed", 7);
                intent.putExtra("level", 8);
                break;
        }

        // Object animation
        animateLevel(view);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                mediaPlayer.pause();
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    private void animateLevel(View view) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationY", 0.0f, 360f);
        animation.setDuration(1000);
        animation.setRepeatCount(0);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if(!spSound.getBoolean("mute",false))
            mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LevelActivity.this , StartActivity.class);
        startActivity(intent);
        finish();
    }

}

