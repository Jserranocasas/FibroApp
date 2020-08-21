package com.example.fibroapp.ui.exercises;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.fibroapp.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    String src;
    VideoView video;
    ProgressBar progressBar;
    boolean playing = false;
    int stopPosition;
    MyAsync myAsync;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_landscape);

        Intent i = getIntent();
        src = i.getStringExtra("src");

        video = findViewById(R.id.videoView);
        video.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        video.setVideoURI(Uri.parse(src));
        video.requestFocus();
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playing = !playing;
                if (playing) {
                    video.seekTo(stopPosition);
                    video.start();
                }
                else {
                    stopPosition = video.getCurrentPosition();
                    video.pause();
                }
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);

        myAsync = new MyAsync();
        myAsync.execute();
    }

    @Override
    public void onBackPressed() {
        myAsync.cancel(true);
        video.stopPlayback();
        super.onBackPressed();
    }

    private class MyAsync extends AsyncTask<Void, Integer, Void>{
        int duration = 0;
        int current = 0;

        @Override
        protected Void doInBackground(Void... voids) {
            video.start();
            playing = true;
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    duration = video.getDuration();
                }
            });

            do {
                current = video.getCurrentPosition();
                try {
                    publishProgress((int) (current * 100.0 / duration));
                    if(progressBar.getProgress() >= 100){
                        break;
                    }
                } catch (Exception e) {
                }
            } while (progressBar.getProgress() <= 100);

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress((int) (values[0]));
        }
    }
}
