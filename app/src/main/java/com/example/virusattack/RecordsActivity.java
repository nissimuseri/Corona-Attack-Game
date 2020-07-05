package com.example.virusattack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private ArrayList<Player> arrayList;
    private MyAdapter myAdapter;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;
    private MediaPlayer mediaPlayer;
    private boolean mute;
    SharedPreferences spSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        relativeLayout = findViewById(R.id.recordsActivityLayout);
        Drawable drawable = getResources().getDrawable(R.drawable.startbgalpha);
        drawable.setAlpha(50);
        relativeLayout.setBackground(drawable);
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        listView = (ListView)(findViewById(R.id.listView_records));
        progressBar = (ProgressBar) (findViewById(R.id.progressBar_result));
        new LoaderAsyncTask(this).execute(10);
        databaseHelper = new DatabaseHelper(this);
        arrayList= new ArrayList<>();

        // Back Button
        TextView backBtn = findViewById(R.id.backBtn_tv);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecordsActivity.this, StartActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
    }

    // load data from SQLITE and give the listview the adapter in order to show that on the screen
    private void loadDataInListView() {
        arrayList = databaseHelper.getAllData();
        myAdapter = new MyAdapter(this,arrayList);
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }

    private static class LoaderAsyncTask extends AsyncTask<Integer, Integer, String> {
        private WeakReference<RecordsActivity> activityWeakReference;

        LoaderAsyncTask(RecordsActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RecordsActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            activity.progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Integer... integers) {
            // loading bar - do "heavy" job
            for (int i = 0; i <= integers[0]; i++) {
                publishProgress(i * 100 / integers[0]);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            RecordsActivity activity = activityWeakReference.get();
            return activity.getString(R.string.pb_Status);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            RecordsActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String statusMsg) {
            super.onPostExecute(statusMsg);
            RecordsActivity activity = activityWeakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            Toast.makeText(activity, statusMsg, Toast.LENGTH_SHORT).show();
            activity.progressBar.setProgress(0);
            activity.progressBar.setVisibility(View.INVISIBLE);
            activity.loadDataInListView();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RecordsActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }
}