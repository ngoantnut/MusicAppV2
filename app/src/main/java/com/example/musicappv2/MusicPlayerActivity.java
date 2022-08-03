package com.example.musicappv2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView pausePlay, nextBtn, preBtn, musicIcon;

    ArrayList<AudioModel> songsList;
    AudioModel currentSong;
    int x = 0;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.txtSongStart);
        totalTimeTv = findViewById(R.id.txtSongEnd);

        seekBar = findViewById(R.id.seekBar);

        pausePlay = findViewById(R.id.btnPlay);
        nextBtn = findViewById(R.id.btnNext);
        preBtn = findViewById(R.id.btnback);

        musicIcon = findViewById(R.id.music_icon_big);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourceWithMusic();



        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));
                    if(mediaPlayer.isPlaying()){
                       pausePlay.setImageResource(R.drawable.ic_baseline_pause_24);
                        musicIcon.setRotation(x++);
                    }
                    else {
                       pausePlay.setImageResource(R.drawable.ic_baseline_play);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


     void setResourceWithMusic() {
        currentSong = songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        preBtn.setOnClickListener(v -> playPreSong());

        playMusic();


    }
    private void playMusic(){
        mediaPlayer.reset();
            try{
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
            }
            catch (IOException e){
                e.printStackTrace();
            }
    }

    private void playNextSong(){
            if(MyMediaPlayer.currentIndex == songsList.size()-1)
                return;
            MyMediaPlayer.currentIndex +=1;
            mediaPlayer.reset();
            setResourceWithMusic();
    }

    private void playPreSong(){
        if(MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -=1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying())

            mediaPlayer.pause();
        else  {

            mediaPlayer.start();
    }}

    @SuppressLint("DefaultLocale")
    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
    }
