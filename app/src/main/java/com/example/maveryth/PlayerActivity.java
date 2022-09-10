package com.example.maveryth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.maveryth.AlbumDetailsAdapter.albumFiles;
import static com.example.maveryth.MainActivity.musicFiles;
import static com.example.maveryth.MainActivity.repeatBoolean;
import static com.example.maveryth.MainActivity.shuffleBoolean;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {


    //049vd7
    TextView song_name, artist_name, duration_played, duration_total ;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    //1.21
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;

    int position = -1;
    //2.37v7
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    //3.15
    static Uri uri;
    static MediaPlayer mediaPlayer;
    //9.04
    private final Handler handler = new Handler();
    private Thread playThread,prevThread,nextThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //1.03
        initViews();
        getIntentMethod();
        //2.29p6v8
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        //5.42
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //5.54
                if(mediaPlayer != null && fromUser)
                {
                    mediaPlayer.seekTo(progress * 1000);

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //6.36v7 -912
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null)
                {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    //704v7
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                //9.14
                handler.postDelayed(this,1000);
            }
        });

        //p8v111
        shuffleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shuffleBoolean)
                {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic__shuffle_off);
                }
                else
                {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(repeatBoolean)
                {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                }
                else
                {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                    //4.15 v10
                }
            }
        });
    }

    //126
    private void initViews()
    {
        song_name = findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar);
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position",-1);
        String sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("albumDetails"))
        {
            listSongs = albumFiles;
        }
        else {
            listSongs = MusicAdapter.mFiles; // check 2.54v7
        }
        if(listSongs != null)
        {
            playPauseBtn.setImageResource(R.drawable.ic_pause);

            uri = Uri.parse(listSongs.get(position).getPath());

        }
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri); //check
            mediaPlayer.start(); //4.31

        }
        else
        {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.28v7
        //1.56p6v8
        metaData(uri);

    }



    //402p6 check


    private void prevThreadBtn() {
        //721
        Thread prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        //5.13
        prevThread.start();
    }

    private void prevBtnClicked() {
        //10.15
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            //9.00v11
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position-1) <0 ? (listSongs.size() - 1 ) : (position-1));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            //8.34
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //907 paste
            seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.51


            //9.13p6v8 paste
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //copyp6
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //copyp6
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();

        }
        else
        {
            //925
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position-1) <0 ? (listSongs.size() - 1 ) : (position-1));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            //8.34
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //907 paste
            seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.51


            //9.13p6v8 paste
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //copyp6
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //copyp6
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private void nextThreadBtn() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        //5.13
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            mediaPlayer.release();

            //5.10v11
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
            else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position+1) % listSongs.size());
            }
            //else 656v11


            uri = Uri.parse(listSongs.get(position).getPath());
            //8.34
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //907 paste
            seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.51


            //9.13p6v8 paste
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //copyp6
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //copyp6
                    handler.postDelayed(this,1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();

        }
        else
        {
            //925
            mediaPlayer.stop();
            mediaPlayer.release();
            //7.39v11
            if(shuffleBoolean && !repeatBoolean)
            {
                position = getRandom(listSongs.size() - 1);
            }
           /* else if(!shuffleBoolean && !repeatBoolean)
            {
                position = ((position+1) % listSongs.size());
            };*/
            uri = Uri.parse(listSongs.get(position).getPath());
            //8.34
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            //907 paste
            seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.51
            mediaPlayer.setOnCompletionListener(this);


            //9.13p6v8 paste
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //copyp6
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //copyp6
                    handler.postDelayed(this,1000);
                }
            });

            playPauseBtn.setBackgroundResource(R.drawable.ic_play);

        }
    }

    private int getRandom(int i) {

        //5.54
        Random random = new Random();

        return random.nextInt(i+1);
    }

    private void playThreadBtn() {

        //4.45
        //3.34p6v8
         playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        //5.13
        playThread.start();

    }

    private void playPauseBtnClicked() {

        if(mediaPlayer.isPlaying())
        {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000); //5.51


            //6.00p6v8 paste
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //copyp6
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //copyp6
                    handler.postDelayed(this,1000);
                }
            });

        }
        else
        {
            //6.08
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            //paste 6.29
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null)
                    {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //704v7
                        seekBar.setProgress(mCurrentPosition);
                        // duration_played.setText(formattedTime(mCurrentPosition));
                    }
                    //9.14
                    handler.postDelayed(this,1000);
                }
            });


        }

    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();

    }
    private String formattedTime(int mCurrentPosition) {

        //8.00
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" +  seconds;
        if(seconds.length() == 1)
        {
            return totalNew;
        }
        else
        {
            //8.54v7
            return totalout;
        }
    }





    //v8
    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        //v73.04
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if( art != null)
        {
            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this,cover_art,bitmap);

        }
        else
        {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.music)
                    .into(cover_art);
        }
    }
    public void ImageAnimation(Context context, ImageView imageView, Bitmap bitmap)
    {
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out) ;
        Animation animIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_out) ;

        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if (mediaPlayer!=null)
        {
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(this);
        }
    }
}
