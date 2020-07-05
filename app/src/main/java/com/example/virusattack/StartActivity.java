package com.example.virusattack;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.virusattack.R;


public class StartActivity extends AppCompatActivity {

    private Button startButton;
    //private EditText nameEditText;
    private Button recordsButton;
    private TextView closeInstruction;
    private Dialog instructionsDialog;
    SharedPreferences spLevels, spSound;
    private boolean mute;
    public static MediaPlayer mediaPlayer;
    private static Boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        startButton = (Button) (findViewById(R.id.btn_start));
        recordsButton = (Button)(findViewById(R.id.btn_records));
        instructionsDialog = new Dialog(this);
        spSound = getSharedPreferences("sound", MODE_PRIVATE);
        mute=spSound.getBoolean("mute",false);
        final SharedPreferences.Editor soundEditor = spSound.edit();
        blink();
        // Volume control
        if(firstTime == true) {
            mediaPlayer = MediaPlayer.create(this, R.raw.gamesound);
            mediaPlayer.setLooping(true);
            if(!spSound.getBoolean("mute",false))
                mediaPlayer.start();
            firstTime = false;
        }
        final ImageView volumeIv = findViewById(R.id.volume_iv_levels);
        volumeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mute) {
                    mute = true;
                    mediaPlayer.pause();
                    soundEditor.putBoolean("mute",true);
                    volumeIv.setImageResource(R.drawable.musicoff3);
                } else { // when muted
                    mute = false;
                    mediaPlayer.start();
                    soundEditor.putBoolean("mute",false);
                    volumeIv.setImageResource(R.drawable.music3);
                }
                soundEditor.commit();
            }
        });
        if(spSound.getBoolean("mute",false))
            volumeIv.setImageResource(R.drawable.musicoff3);
    }

    private void blink(){
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeToBlink = 1500;    //in milissegunds
                try{Thread.sleep(timeToBlink);}catch (Exception e) {}
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        TextView txt = (TextView) findViewById(R.id.start_details);
                        if(txt.getVisibility() == View.VISIBLE){
                            txt.setVisibility(View.INVISIBLE);
                        }else{
                            txt.setVisibility(View.VISIBLE);
                        }
                        blink();
                    }
                });
            }
        }).start();
    }

    public void startGame(View view) {
        Intent intent = new Intent(StartActivity.this, LevelActivity.class);
        spLevels = getSharedPreferences("open levels", MODE_PRIVATE);
        Bundle extras = new Bundle();
        //extras.putString("PlayerName", nameEditText.getText().toString());
        extras.putString("PlayerName", "rotem");
        intent.putExtras(extras);
        if (!spLevels.getBoolean("has played already", false)) {
            SharedPreferences.Editor editorlevels = spLevels.edit();
            editorlevels.putBoolean("first time playing", true);
            editorlevels.commit();
        }
        startActivity(intent);
        finish();
    }

    public void showRecords(View view){
        Intent intent = new Intent(StartActivity.this, RecordsActivity.class);
        startActivity(intent);
        finish();
    }

    public void showInstructions(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
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
}
