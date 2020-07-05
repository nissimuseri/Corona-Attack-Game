package com.example.virusattack;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private TableRow levelBackGround;
    private TextView scoreLabel;
    private TextView TotalScoreLabel;
    private TextView startLabel;
    private TextView countryLabel;
    private TextView levelLabel;
    private ImageView doctorImage;
    private ImageView alcoGelObject;
    private ImageView bonusObject;
    private ImageView red;
    private ImageView green;
    private ImageView blue;
    private ImageView healKit;
    private ImageView[] lifes;
    private ImageView background;
    private ArrayList<Integer> imgArr; // used to load the game 10 points objects
    private AnimationDrawable doctorAnimation;
    private int bgResId;

    // Size
    private int frameHeight;
    private int doctorSize;
    private int screenWidth;
    private int screenHeight;

    // Position
    private int doctorY;
    private int alcoGelObjectX;
    private int alcoGelObjectY;
    private int bonusObjX;
    private int bonusObjY;
    private int coronaRedX;
    private int coronaRedY;
    private int coronaGreenX;
    private int coronaGreenY;
    private int coronaBlueX;
    private int coronaBlueY;
    private int healKitX;
    private int healKitY;

    // Speed
    private int doctorSpeed;
    private int alcoGelObjectSpeed;
    private int bonusObjSpeed;
    private int coronaSpeed;
    private int healKitSpeed;

    // Score
    private int score = 0;
    private int prevScore;
    private int pointTarget;

    //level
    private int level = 1;
    private final int maxLevel = 8;

    //healkit
    private int lifeCount;

    // Initialize Class
    private Handler handler = new Handler();
    private Timer timer = new Timer();
    private SoundPlayer sound;
    Random rand = new Random(); // initialize over here and used in changePos (to avoid recreate every time)

    // Status Check
    private boolean action_flg = false;
    private boolean start_flg = false;

    //settings
    private SharedPreferences spLevels, spSound;
    private boolean mute;
    private MediaPlayer mediaPlayer;
    private Boolean endGameFlag = false;
    private ImageView pause;
    private Boolean gamePaused = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        spLevels = getSharedPreferences("open levels", MODE_PRIVATE);
        sound = new SoundPlayer(this);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        TotalScoreLabel = (TextView) findViewById(R.id.totalScoreLabel);
        startLabel = (TextView) findViewById(R.id.startLabel);
        countryLabel = (TextView) findViewById(R.id.country_label);
        levelLabel = (TextView) findViewById(R.id.levelLabel);
        doctorImage = (ImageView) findViewById(R.id.doctorImage);
        background = (ImageView) findViewById(R.id.countryImage);
        alcoGelObject = (ImageView) findViewById(R.id.alcoGel);
        bonusObject = (ImageView) findViewById(R.id.mask);
        red = (ImageView) findViewById(R.id.red);
        green = (ImageView) findViewById(R.id.green);
        blue = (ImageView) findViewById(R.id.blue);
        healKit = (ImageView) findViewById(R.id.objectHeal);
        levelBackGround = (TableRow) findViewById(R.id.tableBackGround);
        countryLabel.setText(countryLabelString());

        // Volume control and logic
        spSound=getSharedPreferences("sound",MODE_PRIVATE);
        mute=spSound.getBoolean("mute",false);
        final SharedPreferences.Editor soundEditor=spSound.edit();
        final ImageView volumeIv = findViewById(R.id.music_btn);
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
        if (!spSound.getBoolean("mute", false)) {
            mediaPlayer = StartActivity.mediaPlayer;
            mediaPlayer.start();
        }
        else {
            mediaPlayer = MediaPlayer.create(this, R.raw.gamesound);
            StartActivity.mediaPlayer = mediaPlayer;
            volumeIv.setImageResource(R.drawable.musicoff3);
        }

        //load pause game
        pause = (ImageView)findViewById(R.id.pause_btn);
        pause.setVisibility(View.GONE);
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
            }
        });

        //load healkit objects (triple)
        lifes = new ImageView[3];
        lifes[0] = (ImageView) findViewById(R.id.heart);
        lifes[1] = (ImageView) findViewById(R.id.heart2);
        lifes[2] = (ImageView) findViewById(R.id.heart3);
        lifes[0].setTag(findViewById(R.id.heart));
        lifes[1].setTag(findViewById(R.id.heart));
        lifes[2].setTag(findViewById(R.id.heart));

        // load objects to array, initialize imgArr
        getResourcesImages();

        // Get screen size to better match for display game
        WindowManager wm = getWindowManager();
        Display disp = wm.getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        //base speeds
        doctorSpeed = 21;
        alcoGelObjectSpeed = 20;
        bonusObjSpeed = 40;
        coronaSpeed = 23;
        healKitSpeed = 45;

        // Move the objects out of the screen in order to hide them from the user when the game begins
        alcoGelObject.setX(-120);
        alcoGelObject.setY(-120);
        red.setX(-120);
        red.setY(-120);
        green.setX(-120);
        green.setY(-120);
        blue.setX(-120);
        blue.setY(-120);
        healKit.setX(-120);
        healKit.setY(-120);
        bonusObject.setX(-120);
        bonusObject.setY(-120);
        healKit.setVisibility(View.GONE);

        //level label
        levelLabel.setText(String.format("%s1", getString(R.string.level)));

        //healkit count
        lifeCount = 3;

        //doctor animation
        doctorImage.setBackgroundResource(R.drawable.doctor_animation);
        doctorAnimation = (AnimationDrawable) doctorImage.getBackground();
        doctorImage.setVisibility(View.VISIBLE);

        //update the level settings accourding to the extras that come from the previous intent
        bgResId = getIntent().getIntExtra("bgResId", R.drawable.china);
        pointTarget = getIntent().getIntExtra("pointTarget", 200);
        int speed = getIntent().getIntExtra("speed", 1);
        level = getIntent().getIntExtra("level", 1);
        prevScore = getIntent().getIntExtra("SCORE", 0);
        countryLabel.setText(countryLabelString());
        scoreLabel.setText(String.format("%s%d / %d", getString(R.string.game_score), score, pointTarget));
        TotalScoreLabel.setText(String.format("%s %d", getString(R.string.total_game_score), score + prevScore));
        coronaSpeed+=speed;
        alcoGelObjectSpeed +=speed;
        bonusObjSpeed +=speed;
        healKitSpeed += speed;
        String setLevel = getString(R.string.level)+ level;
        levelLabel.setText(setLevel);
        background.setBackgroundResource(bgResId);
    }

    private void getResourcesImages() {
        // in this function we load all the resources images of 10 points objects in game (dynamic loader)
        imgArr = new ArrayList<>();
        Field[] fields = R.drawable.class.getFields();
        for(Field field : fields){
            if (field.getName().startsWith("object_a"))
            {
                imgArr.add(getResources().getIdentifier(field.getName(), "drawable", getPackageName()));
            }
        }
    }

    private String countryLabelString()
    {
        String countryLabel = "";
        switch(bgResId) {
            case R.drawable.israel:
                countryLabel = getResources().getString(R.string.israel_label);
                break;
            case R.drawable.sweden:
                countryLabel = getResources().getString(R.string.sweden_label);
                break;
            case R.drawable.china:
                countryLabel = getResources().getString(R.string.china_label);
                break;
            case R.drawable.germany:
                countryLabel = getResources().getString(R.string.germany_label);
                break;
            case R.drawable.italy:
                countryLabel = getResources().getString(R.string.italy_label);
                break;
            case R.drawable.russia:
                countryLabel = getResources().getString(R.string.russia_label);
                break;
            case R.drawable.brazil:
                countryLabel = getResources().getString(R.string.brazil_label);
                break;
            case R.drawable.usa:
                countryLabel = getResources().getString(R.string.usa_label);
                break;
        }
        return countryLabel;
    }


    public void changePos() {
        /*
            This function control the positions of the objects in the game,
            the start position(before press play) will be out of user view (width screen +x)
            when the game start(After press play) there will be random math function to place the object position
         */
        hitCheck();

        // normal objects
        alcoGelObjectX -= alcoGelObjectSpeed;
        if (alcoGelObjectX < 0) {
            int numberOfAlcoGelObject = imgArr.size();

            alcoGelObject.setImageResource(imgArr.get(rand.nextInt(numberOfAlcoGelObject))); // get random object of 10 points from array(rand not +1 cuz array its 0-4)
            alcoGelObjectX = screenWidth + 20;
            alcoGelObjectY = (int) Math.floor(Math.random() * (frameHeight - alcoGelObject.getHeight()));
        }
        alcoGelObject.setX(alcoGelObjectX);
        alcoGelObject.setY(alcoGelObjectY);


        // Red corona move
        coronaRedX -= coronaSpeed;
        if (coronaRedX < 0) {
            coronaRedX = screenWidth + 10;
            coronaRedY = (int) Math.floor(Math.random() * (frameHeight - red.getHeight()));
        }
        red.setX(coronaRedX);
        red.setY(coronaRedY);

        //Green corona move
        if(level >= 3)
        {
            coronaGreenX -= coronaSpeed;
            if (coronaGreenX < 0) {
                coronaGreenX = screenWidth + 50;
                coronaGreenY = (int) Math.floor(Math.random() * (frameHeight - green.getHeight()));
                while(coronaGreenY < coronaRedY + red.getHeight() && coronaGreenY > coronaRedY - red.getHeight())
                    coronaGreenY = (int) Math.floor(Math.random() * (frameHeight - green.getHeight()));
            }
            green.setX(coronaGreenX);
            green.setY(coronaGreenY);
        }

        //Blue corona move
        if(level >= 6)
        {
            coronaBlueX -= coronaSpeed;
            if (coronaBlueX < 0) {
                coronaBlueX = screenWidth + 100;
                coronaBlueY = (int) Math.floor(Math.random() * (frameHeight - blue.getHeight()));
                while((coronaBlueY < coronaRedY + red.getHeight() && coronaBlueY > coronaRedY - red.getHeight())
                    || (coronaBlueY < coronaGreenY + green.getHeight() && coronaBlueY > coronaGreenY - green.getHeight()))
                    coronaBlueY = (int) Math.floor(Math.random() * (frameHeight - blue.getHeight()));
            }
            blue.setX(coronaBlueX);
            blue.setY(coronaBlueY);
        }

       // bonus recycle object
        bonusObjX -= bonusObjSpeed;
        if (bonusObjX < 0) {
            bonusObjX = screenWidth + 10000; //raise the number make it rare
            bonusObjY = (int) Math.floor(Math.random() * (frameHeight - bonusObject.getHeight()));
        }
        bonusObject.setX(bonusObjX);
        bonusObject.setY(bonusObjY);

        //Heal kit object - if the user has 2 or less lifes the heal kit will appear
        healKitX -= healKitSpeed;
        if(lifeCount < 3 && healKitX<0)
        {
            healKit.setVisibility(View.VISIBLE);
            healKitX = screenWidth + 20000; //raise the number make it rare
            healKitY = (int) Math.floor(Math.random() * (frameHeight - healKit.getHeight()));
        }
        healKit.setX(healKitX);
        healKit.setY(healKitY);

        // Move the doctor
        if (action_flg) {
            // Touching
            doctorY -= doctorSpeed;

        } else {
            // Releasing
            doctorY += doctorSpeed;
        }

        // Check doctor position, and control the doctor position according to screen&frame limit
        if (doctorY < 0) doctorY = 0;
        if (doctorY > frameHeight - doctorSize) doctorY = frameHeight - doctorSize;
        doctorImage.setY(doctorY);

        //update score
        scoreLabel.setText(String.format("%s%d / %d", getString(R.string.game_score), score, pointTarget));
        TotalScoreLabel.setText(String.format("%s %d", getString(R.string.total_game_score), score + prevScore));

        //end the game if you got enough score and update the sharedpref to unlock the next level
        if(score >= pointTarget) {
            score = pointTarget;
            if (level < maxLevel) {
                SharedPreferences.Editor editor = spLevels.edit();
                if (!spLevels.getBoolean("has played already", false))
                    editor.putBoolean("has played already", true);
                editor.putInt("level" + (level + 1), 1);
                editor.commit();
            }
            endGame();
        }
    }

    //call this function after every animation move and check if there's a hit
    public void hitCheck() {

        // If the center of the corona virus hit the doctor, it counts as a hit.

        // objects 10 points
        int alcoGelObjectCenterX = alcoGelObjectX + alcoGelObject.getWidth() / 2;
        int alcoGelObjectCenterY = alcoGelObjectY + alcoGelObject.getHeight() / 2;
        if (0 <= alcoGelObjectCenterX && alcoGelObjectCenterX <= doctorSize &&
                doctorY <= alcoGelObjectCenterY && alcoGelObjectCenterY <= doctorY + doctorSize) {
            score += 10;
            alcoGelObjectX = -10;
            if (!spSound.getBoolean("mute", false))
                sound.playHitSound();
        }

        // bonus mask object
        int bonusObjCenterX = bonusObjX + bonusObject.getWidth() / 2;
        int bonusObjCenterY = bonusObjY + bonusObject.getHeight() / 2;
        if (0 <= bonusObjCenterX && bonusObjCenterX <= doctorSize &&
                doctorY <= bonusObjCenterY && bonusObjCenterY <= doctorY + doctorSize) {
            score += 30;
            bonusObjX = -10;
            if (!spSound.getBoolean("mute", false))
                sound.playHitSound();
        }

        // heal kit object
        int healKitCenterX = healKitX + healKit.getWidth() / 2;
        int healKitCenterY = healKitY + healKit.getHeight() / 2;
        if (0 <= healKitCenterX && healKitCenterX <= doctorSize &&
                doctorY <= healKitCenterY && healKitCenterY <= doctorY + doctorSize) {
            healKitX = -30;
            for(ImageView check:lifes)
            {
                if(check.getTag().equals(R.drawable.heart_g))
                {
                    check.setImageResource(R.drawable.heart);
                    check.setTag(R.drawable.heart);//to avoid get into this condition again
                    lifeCount++;
                    break;
                }
            }
            if (!spSound.getBoolean("mute", false))
                sound.playHitSound();
        }

        // Red
        int redCenterX = coronaRedX + red.getWidth() / 2;
        int redCenterY = coronaRedY + red.getHeight() / 2;
        // green
        int greenCenterX = 0;
        int greenCenterY = 0;
        if(level >= 3) {
            greenCenterX = coronaGreenX + green.getWidth() / 2;
            greenCenterY = coronaGreenY + green.getHeight() / 2;
        }
        // blue
        int blueCenterX = 0;
        int blueCenterY = 0;
        if(level >= 6) {
            blueCenterX = coronaBlueX + blue.getWidth() / 2;
            blueCenterY = coronaBlueY + blue.getHeight() / 2;
        }

        //decrease life count by 1 after a hit and play sound
        if ((0 <= redCenterX && redCenterX <= doctorSize &&
                doctorY <= redCenterY && redCenterY <= doctorY + doctorSize)
            || (level >= 3 && 0 <= greenCenterX && greenCenterX <= doctorSize &&
                doctorY <= greenCenterY && greenCenterY <= doctorY + doctorSize)
            || (level >= 6 && 0 <= blueCenterX && blueCenterX <= doctorSize &&
                doctorY <= blueCenterY && blueCenterY <= doctorY + doctorSize)) {
            lifes[lifeCount -1].setImageResource(R.drawable.heart_g);
            lifes[lifeCount -1].setTag(R.drawable.heart_g);
            lifeCount--;
            if (!spSound.getBoolean("mute", false))
                sound.playOverSound();

            coronaRedX = -10;
            if(level >= 3) {
                coronaGreenX = -10;
            }
            if(level >= 6) {
                coronaBlueX = -10;
            }
            if(lifeCount == 0)
            {
                endGame();
            }
        }
    }

    public void endGame(){
        // Stop Timer
        if(timer != null)
        {
            timer.cancel();
            timer = null;
        }

        endGameFlag = true;

        // Bundle variables
        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        Bundle extras = new Bundle();
        extras.putInt("SCORE", score + prevScore);
        extras.putInt("Level",level);
        extras.putInt("Life",lifeCount);
        intent.putExtras(extras);
        startActivity(intent);
        finish();
    }

    public boolean onTouchEvent(MotionEvent me) {
        if (!start_flg) {
            start_flg = true;
            doctorAnimation.start();
            pause.setVisibility(View.VISIBLE);

            // get frame height and truck height here,
            // Because the UI hasn't been set on the screen in OnCreate()
            FrameLayout frame = (FrameLayout) findViewById(R.id.frame);
            frameHeight = frame.getHeight();
            doctorY = (int) doctorImage.getY();

            // square(height and width are the same.)
            doctorSize = doctorImage.getHeight();

            startLabel.setVisibility(View.GONE);
            countryLabel.setVisibility(View.GONE);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }, 0, 20);  // call changePos every 20 millisec


        } else {
            if (me.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (me.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;
            }
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseGame();
    }

    //pause game function
    private void pauseGame()
    {
        if(mediaPlayer != null && timer != null && gamePaused == false) {
            gamePaused = true;
            mediaPlayer.pause();
            doctorAnimation.stop();
            timer.cancel();
            if(!endGameFlag) {
                SweetAlertDialog pDialog;
                pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(R.string.game_paused)
                        .setContentText(getResources().getString(R.string.game_paused_message))
                        .setConfirmText(getResources().getString(R.string.continue_game))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                resumeGame();
                            }
                        })
                        .setCancelButton(R.string.paused_menu, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setCancelable(false);
                pDialog.show();
            }
        }
    }

    //resume game function
    private void resumeGame()
    {
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        doctorAnimation.start();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        changePos();
                    }
                });
            }
        }, 0, 20);  // call changePos every 20 millisec
        if(!spSound.getBoolean("mute",false))
            mediaPlayer.start();
        gamePaused = false;
    }

    @Override
    public void onBackPressed() {
        pauseGame();
    }
}
