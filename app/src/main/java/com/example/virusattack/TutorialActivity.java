package com.example.virusattack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.FocusShape;
import me.toptas.fancyshowcase.listener.OnCompleteListener;

public class TutorialActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    SharedPreferences spSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        spSound = getSharedPreferences("sound", MODE_PRIVATE);
        if (!spSound.getBoolean("mute", false)) {
            mediaPlayer = StartActivity.mediaPlayer;
            mediaPlayer.start();
        }
        FancyShowCaseView fancyShowCaseViewStart1 = new FancyShowCaseView.Builder(this)
                .title(getResources().getString(R.string.game_tut) + "\n\n" + getResources().getString(R.string.game_tut2))
                .titleSize(36, TypedValue.COMPLEX_UNIT_SP)
                .titleGravity(Gravity.CENTER)
                .build();
        FancyShowCaseView fancyShowCaseViewDoctor1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.doctor_tut_tv))
                .title(getResources().getString(R.string.doctor_tut))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewDoctor2 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.doctor_tut_tv))
                .title(getResources().getString(R.string.doctor_tut2_1) + "\n" + getResources().getString(R.string.doctor_tut2_2))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewDoctor3 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.doctor_tut_tv))
                .title(getResources().getString(R.string.doctor_tut3_1) + "\n" + getResources().getString(R.string.doctor_tut3_2))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewCorona1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.corona_tut_tv))
                .title(getResources().getString(R.string.corona_tut))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewCorona2 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.corona_tut_tv))
                .title(getResources().getString(R.string.corona_tut1))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewHeal1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.heal_tut_tv))
                .title(getResources().getString(R.string.heal_tut1))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewHeal2 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.heal_tut_tv))
                .title(getResources().getString(R.string.heal_tut2))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewItems1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.items_tut_tv))
                .title(getResources().getString(R.string.items_tut))
                .titleSize(30, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();
        FancyShowCaseView fancyShowCaseViewBonus1 = new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(R.id.bonus_tut_tv))
                .title(getResources().getString(R.string.bonus_tut_1) + "\n" + getResources().getString(R.string.bonus_tut_2))
                .titleSize(36, TypedValue.COMPLEX_UNIT_SP)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .roundRectRadius(90)
                .build();


        FancyShowCaseQueue queue = new FancyShowCaseQueue()
                .add(fancyShowCaseViewStart1)
                .add(fancyShowCaseViewDoctor1)
                .add(fancyShowCaseViewDoctor2)
                .add(fancyShowCaseViewDoctor3)
                .add(fancyShowCaseViewCorona1)
                .add(fancyShowCaseViewCorona2)
                .add(fancyShowCaseViewHeal1)
                .add(fancyShowCaseViewHeal2)
                .add(fancyShowCaseViewItems1)
                .add(fancyShowCaseViewBonus1);

        queue.show();
        queue.setCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                ImageView bgIv = findViewById(R.id.layout_bg_iv);
                bgIv.setBackgroundResource(R.drawable.china);

                FancyShowCaseView fancyShowCaseView7 = new FancyShowCaseView.Builder(TutorialActivity.this)
                        .title(getResources().getString(R.string.goodluck_tut))
                        .titleSize(46, TypedValue.COMPLEX_UNIT_SP)
                        .titleGravity(Gravity.CENTER)
                        .build();
                FancyShowCaseQueue queue2 = new FancyShowCaseQueue().add(fancyShowCaseView7);
                queue2.show();
                queue2.setCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete() {
                        Intent intent = new Intent(TutorialActivity.this, StartActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mediaPlayer != null && !spSound.getBoolean("mute",false))
            mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this , StartActivity.class);
        startActivity(intent);
        finish();
    }
}
