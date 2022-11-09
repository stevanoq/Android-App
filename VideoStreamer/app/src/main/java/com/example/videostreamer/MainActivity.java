package com.example.videostreamer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog mDialog;
    VideoView videoView;
    ImageButton btn;

    String videoUrl = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoView) findViewById(R.id.VideoView);
        btn = (ImageButton) findViewById(R.id.btn);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        try {
            if (videoView.isPlaying()){
                Uri uri = Uri.parse(videoUrl);
                videoView.setVideoURI(uri);
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        btn.setImageResource(R.drawable.ic_play);
                    }
                });
            }

            else{
                videoView.pause();
                btn.setImageResource(R.drawable.ic_play);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mDialog.dismiss();
                mp.setLooping(true);
                videoView.start();
                btn.setImageResource(R.drawable.ic_pause);
            }
        });
    }
}