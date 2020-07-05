package com.example.virusattack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private TextView scoreLabel;
    private TextView highScoreLabel;
    private TextView endGame;
    private Button share;
    EditText nameET;
    private int level;
    private int score;
    Button nextBtn;
    EditText username;
    private MediaPlayer mediaPlayer;
    private boolean mute;
    SharedPreferences spSound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        db = new DatabaseHelper(this);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        highScoreLabel = (TextView) findViewById(R.id.highScoreLabel);
        endGame = (TextView) findViewById(R.id.endGame);
        share = findViewById(R.id.share_btn);
        username = findViewById(R.id.username);
        username.setVisibility(View.GONE);

        // get variables info from bundle/intent
        score = getIntent().getIntExtra("SCORE", 0);
        level = getIntent().getIntExtra("Level",1);
        int life = getIntent().getIntExtra("Life",3);
        scoreLabel.setText(String.format(Locale.getDefault(),"%d", score));
        checkResults(level, life);

        // update sqlite data with the new result
        nameET = findViewById(R.id.username);
        CheckBox cb = findViewById(R.id.save_score_cb);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox cb = (CheckBox)view;
                if(cb.isChecked())
                {
                    nextBtn.setVisibility(View.GONE);
                    username.setVisibility(View.VISIBLE);
                }
                else
                {
                    nextBtn.setVisibility(View.VISIBLE);
                    username.setVisibility(View.GONE);
                }
            }
        });

        //show current high score
        int highScore;
        if(db.getAllData().size() != 0)
            highScore = ((Player) db.getAllData().get(0)).getScore();
        else
            highScore = 0;
        highScoreLabel.setText(String.format(Locale.getDefault(),"%s%d", getString(R.string.high_score), highScore));

        // Volume control
        spSound = getSharedPreferences("sound", MODE_PRIVATE);
        mute = spSound.getBoolean("mute", false);
        final SharedPreferences.Editor soundEditor = spSound.edit();
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

        //share button
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.titleShare) + " " + scoreLabel.getText().toString());
                startActivity(Intent.createChooser(sharingIntent,getString(R.string.share)));
            }
        });
    }

    //check if the user finished the level successfully or if he finished it with zero lifes
    private void checkResults(int level, int current_life) {
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.my_layout);
        TextView endText = (TextView) findViewById(R.id.endGameText);
        nextBtn = findViewById(R.id.btn_nextLevel);
        if(current_life>0)
        {
            if(level==8)
            {
                endGame.setText(getString(R.string.win_game));
                endText.setText(getString(R.string.text_win));
                Drawable drawable = getResources().getDrawable(R.drawable.winbg);
                drawable.setAlpha(70);
                myLayout.setBackground(drawable);
                nextBtn.setVisibility(View.INVISIBLE);
            }
            else
            {
                endGame.setText(getString(R.string.win_game));
                endText.setText(getString(R.string.text_win));
                Drawable drawable = getResources().getDrawable(R.drawable.level_up);
                drawable.setAlpha(70);
                myLayout.setBackground(drawable);
                nextBtn.setOnClickListener(new continueNextLevel());
                nextBtn.setText(R.string.next_level);
            }
        }
        else
        {
            endGame.setText(getString(R.string.game_over));
            endText.setText(getString(R.string.text_lose));
            Drawable drawable = getResources().getDrawable(R.drawable.losebg);
            drawable.setAlpha(50);
            myLayout.setBackground(drawable);
            nextBtn.setOnClickListener(new retryThisLevel());
            nextBtn.setText(R.string.try_again);
        }
    }

    //save the record and back to main menu
    public void backMainMenu(View view) {
        // back to start activity
        String name = nameET.getText().toString();
        if(name.isEmpty() == false)
            db.insertDataRecords(score,name);
        startActivity(new Intent(getApplicationContext(), StartActivity.class));
        finish();
    }

    //if the user successed last time the button will create the next level
    private class continueNextLevel implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            loadLevel(intent, level+1);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            finish();
        }
    }

    //if the user failed last time the button will recreate the prev level
    private class retryThisLevel implements View.OnClickListener
    {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            loadLevel(intent, level);
            startActivity(intent);
            finish();
        }
    }

    // Disable Return Button
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if(event.getKeyCode()== KeyEvent.KEYCODE_BACK)
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //reload prev level / load next level
    private void loadLevel(Intent intent, int level)
    {
        switch (level) {
            case 1: // Level 1
                intent.putExtra("bgResId", R.drawable.israel);
                intent.putExtra("pointTarget", 200);
                intent.putExtra("speed", 0);
                intent.putExtra("level", 1);
                break;

            case 2: // Level 2
                intent.putExtra("bgResId", R.drawable.sweden);
                intent.putExtra("pointTarget", 200);
                intent.putExtra("speed", 1);
                intent.putExtra("level", 2);
                break;

            case 3: // Level 3
                intent.putExtra("bgResId", R.drawable.china);
                intent.putExtra("pointTarget", 300);
                intent.putExtra("speed", 2);
                intent.putExtra("level", 3);
                break;

            case 4: // Level 4
                intent.putExtra("bgResId", R.drawable.germany);
                intent.putExtra("pointTarget", 300);
                intent.putExtra("speed", 3);
                intent.putExtra("level", 4);
                break;

            case 5: // Level 5
                intent.putExtra("bgResId", R.drawable.italy);
                intent.putExtra("pointTarget", 400);
                intent.putExtra("speed", 4);
                intent.putExtra("level", 5);
                break;

            case 6: // Level 6
                intent.putExtra("bgResId", R.drawable.russia);
                intent.putExtra("pointTarget", 400);
                intent.putExtra("speed", 5);
                intent.putExtra("level", 6);
                break;

            case 7: // Level 7
                intent.putExtra("bgResId", R.drawable.brazil);
                intent.putExtra("pointTarget", 500);
                intent.putExtra("speed", 6);
                intent.putExtra("level", 7);
                break;

            case 8: // Level 8
                intent.putExtra("bgResId", R.drawable.usa);
                intent.putExtra("pointTarget", 500);
                intent.putExtra("speed", 7);
                intent.putExtra("level", 8);
                break;
        }
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
}
