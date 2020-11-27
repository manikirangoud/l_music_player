package player.music.activities;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableRow;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import player.music.R;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Tools;

public class SyncerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener, AudioManager.OnAudioFocusChangeListener {


    private static final int PICK_FILE_REQUEST = 1;
    private static final String FOLDER_NAME = "LyricSyncer";

    private Vibrator vibrator;


    private Button buttonSelectFile, buttonSyncLyric, buttonSaveLyrics;
    private Bundle bundle;
    private Button btnAddLyricBefore, btnAddLyricAfter;

    private String songTitle, albumTitle, artistName, songPath, albumArt;

    private UbuntuTextView textViewSongTitle, textViewSongDetails, textViewCurrentTime,
            textViewTotalTime, textViewTopLyric, textViewCenterLyric, textViewBottomLyric,
            textViewProgressTime;
    private ImageView imageViewAlbumArt, imageViewPreviousLyric, imageViewPlayPause,
            imageViewNextLyric, imageViewSubmitLyric;
    private EditText editTextCenterLyric;
    private CoordinatorLayout coordinatorLayout;

    private AppCompatSeekBar seekBar;
    private long songDuration;
    private String[] syncedLyrics;
    private long[] syncedTime;

    private boolean lyricsFound = false;

    private ArrayList<String> lyrics = new ArrayList<>();

    private JSONArray jsonArraySyncedLyrics = new JSONArray();

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private long resumePosition = 0;
    private int lyricPosition = 0;

    private TableRow trAddLine;
    private CoordinatorLayout clAddLine;
    private EditText etAddLine;
    private ImageView ivSubmitLine;
    private int addLyricPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.syncer_layout);


        buttonSelectFile = findViewById(R.id.button_select_file);
        textViewSongTitle = findViewById(R.id.utv_track_title);
        textViewSongDetails = findViewById(R.id.utv_artist_name);
        imageViewAlbumArt = findViewById(R.id.iv_album_art);
        seekBar = findViewById(R.id.seek_bar_syncer);
        textViewCurrentTime = findViewById(R.id.tv_current_time);
        textViewTotalTime = findViewById(R.id.tv_total_time);
        textViewTopLyric = findViewById(R.id.tv_lyric_top);
        textViewCenterLyric = findViewById(R.id.tv_lyric_center);
        textViewBottomLyric = findViewById(R.id.tv_lyric_bottom);
        buttonSyncLyric = findViewById(R.id.button_sync_lyric);
        buttonSaveLyrics = findViewById(R.id.button_save_lyrics);
        imageViewPreviousLyric = findViewById(R.id.iv_lyric_up);
        imageViewPlayPause = findViewById(R.id.iv_play_pause);
        imageViewNextLyric = findViewById(R.id.iv_lyric_down);
        textViewProgressTime = findViewById(R.id.tv_progress_time);
        editTextCenterLyric = findViewById(R.id.et_center_lyric);
        imageViewSubmitLyric = findViewById(R.id.iv_submit_lyric);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        btnAddLyricBefore = findViewById(R.id.btn_add_lyric_before);
        btnAddLyricAfter = findViewById(R.id.btn_add_lyric_after);
        trAddLine = findViewById(R.id.tr_add_line);
        clAddLine = findViewById(R.id.cl_add_line);
        etAddLine = findViewById(R.id.et_add_line);
        ivSubmitLine = findViewById(R.id.iv_submit_line);



        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);



        bundle = getIntent().getExtras();
        albumArt = bundle.getString("albumArt", "");
        songTitle = bundle.getString("songTitle", "");
        songPath = bundle.getString("songPath", "");
        albumTitle = bundle.getString("albumTitle", "");
        artistName = bundle.getString("artistName", "");
        songDuration = bundle.getLong("songDuration", 0);

        setSongDetails();


        fetchLyrics();


        imageViewPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                if (mediaPlayer == null ) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setOnCompletionListener(SyncerActivity.this);
                    mediaPlayer.setOnPreparedListener(SyncerActivity.this);
                    mediaPlayer.setOnErrorListener(SyncerActivity.this);
                    mediaPlayer.setOnSeekCompleteListener(SyncerActivity.this);
                    mediaPlayer.setOnInfoListener(SyncerActivity.this);
                    mediaPlayer.setOnBufferingUpdateListener(SyncerActivity.this);
                    mediaPlayer.reset();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {

                        mediaPlayer.setDataSource(songPath);
                        mediaPlayer.prepareAsync();

                    } catch (NullPointerException ne) {
                        //Log.e("INIT_MEDIA_PLAYER", e.toString());
                    } catch (Exception e) {
                        Log.e("INIT_MEDIA_PLAYER", e.toString());
                    }
                } else {

                    if (mediaPlayer.isPlaying()) {
                        pauseMedia();
                    } else {
                        playMedia();
                    }
                }
            }
        });

        imageViewPreviousLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ( mediaPlayer!= null ) {

                    if ( mediaPlayer.isPlaying() ) {

                        long position = mediaPlayer.getCurrentPosition() - 5000;
                        if ( position > 0 ) {

                            mediaPlayer.seekTo( (int)position );
                        }
                    } else {

                        long position = mediaPlayer.getCurrentPosition() - 5000;
                        if ( position > 0 ) {

                            resumePosition = position;
                            resumeMedia();
                        }
                    }
                }

                vibrator.vibrate(50);
                if ( !lyrics.isEmpty() && (lyricPosition > 0) ) {

                    --lyricPosition;
                    try {
                        if (lyrics.size() != lyricPosition) {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                                jsonArraySyncedLyrics.remove(lyricPosition);
                            }
                        }

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }

                    try {

                        if (lyricPosition == 0) {

                            textViewTopLyric.setText("");
                            textViewCenterLyric.setText(lyrics.get(lyricPosition));
                            textViewBottomLyric.setText(lyrics.get(lyricPosition + 1));
                        } else {
                            if (lyricPosition <= lyrics.size() - 1) {
                                textViewTopLyric.setText(lyrics.get(lyricPosition - 1));
                                textViewCenterLyric.setText(lyrics.get(lyricPosition));
                            }

                            if (lyricPosition <= lyrics.size() - 2) {
                                textViewBottomLyric.setText(lyrics.get(lyricPosition + 1));
                            } else {
                                textViewBottomLyric.setText("");
                            }
                        }
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        btnAddLyricBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trAddLine.setVisibility(View.GONE);
                clAddLine.setVisibility(View.VISIBLE);
                addLyricPosition -= 1;
            }
        });


        btnAddLyricAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trAddLine.setVisibility(View.GONE);
                clAddLine.setVisibility(View.VISIBLE);
                addLyricPosition += 1;
            }
        });


        ivSubmitLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String line = etAddLine.getText().toString();

                if (!line.isEmpty()) {

                    if ( lyricPosition == 0) {

                        addLyricPosition = 0;
                    }
                    lyrics.add((lyricPosition + addLyricPosition), line);
                    clAddLine.setVisibility(View.GONE);
                    trAddLine.setVisibility(View.VISIBLE);


                    if (lyricPosition != 0) {
                        textViewTopLyric.setText(lyrics.get(lyricPosition - 1));
                    }
                    textViewCenterLyric.setText(lyrics.get(lyricPosition));
                    
                    if ( lyricPosition < lyrics.size()-2 ) {
                        textViewBottomLyric.setText(lyrics.get(lyricPosition + 1));
                    }

                    setUnsyncedLyrics();
                }

            }
        });


        buttonSyncLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                syncLyrics();
            }
        });

        imageViewNextLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                syncLyrics();
            }
        });

        buttonSaveLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);

                if (jsonArraySyncedLyrics.length() != 0) {

                    try{

                        File root = Environment.getExternalStorageDirectory();
                        File path = new File(root.getAbsolutePath() + "/" + FOLDER_NAME + "/" +
                                artistName + "/" + albumTitle );
                        path.mkdirs();

                        File file = new File(path, songTitle + ".txt");
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
                        outputStreamWriter.write(jsonArraySyncedLyrics.toString());
                        outputStreamWriter.flush();
                        outputStreamWriter.close();

                        Toast.makeText(getApplicationContext(), "Lyrics saved successfully!", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });


        textViewCenterLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = textViewCenterLyric.getText().toString();
                textViewCenterLyric.setVisibility(View.GONE);

                coordinatorLayout.setVisibility(View.VISIBLE);
                editTextCenterLyric.setText(text);
                editTextCenterLyric.setSelection(text.length());
            }
        });

        imageViewSubmitLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String updatedLyric = editTextCenterLyric.getText().toString();
                if ( !updatedLyric.isEmpty() ) {

                    editTextCenterLyric.clearFocus();
                    coordinatorLayout.setVisibility(View.GONE);

                    textViewCenterLyric.setVisibility(View.VISIBLE);
                    textViewCenterLyric.setText(updatedLyric);


                    if ( lyrics.size() > 0 ) {
                        lyrics.remove(lyricPosition);
                        lyrics.add(lyricPosition, updatedLyric);
                    } else {

                        try {
                            onLyricChanged(textViewCenterLyric.getText().toString());

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });



        buttonSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                //       Intent intentSelectFile = new Intent(getApplicationContext(), FileChooser.class);
                //       intentSelectFile.putExtra(Constants.SELECTION_MODE,
                //               Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                //       startActivityForResult(intentSelectFile, PICK_FILE_REQUEST);

                Intent intentAddLyrics = new Intent(getApplicationContext(), AddLyrics.class);
                intentAddLyrics.putExtra("albumTitle", albumTitle);
                intentAddLyrics.putExtra("songTitle", songTitle);
                startActivityForResult(intentAddLyrics, 1);

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if ( fromUser ) {
                    textViewProgressTime.setText("" + Tools.getSongTime(seekBar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {

                int seekBarPosition = seekBar.getProgress();

                if ( mediaPlayer != null ) {

                    mediaPlayer.seekTo(seekBarPosition);

                    if ( !mediaPlayer.isPlaying() ) {
                        resumePosition = seekBarPosition;
                        resumeMedia();
                    }
                } else {

                    imageViewPlayPause.performClick();
                    pauseMedia();
                    resumePosition = seekBarPosition;
                    resumeMedia();
                }

                updateLyricPosition( seekBarPosition );
                textViewProgressTime.setText("");
            }
        });


    }

    private void setUnsyncedLyrics() {

        File root = Environment.getExternalStorageDirectory();
        File path = new File(root.getAbsolutePath() + "/" + "Data Of Unsynced Lyrics"
                + "/" + albumTitle );

        File file = new File(path, songTitle + ".txt");

        try{

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));

            for (String text : lyrics) {
                outputStreamWriter.write(text + "\n");
            }
            outputStreamWriter.flush();
            outputStreamWriter.close();

            Toast.makeText(this, "Lyrics saved successfully!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            fetchLyrics();
        }
    }



    private void onLyricChanged(String updatedLyric) {

        try{

            JSONArray jsonArray = new JSONArray();

            int length = syncedLyrics.length;
            for ( int i = 0; i < length; i++ ) {

                if ( lyricPosition == i ) {

                    syncedLyrics[i] = updatedLyric;
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lyric", updatedLyric);
                    jsonObject.put("endTime", syncedTime[i]);
                    jsonArray.put(jsonObject);
                } else {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("lyric", syncedLyrics[i]);
                    jsonObject.put("endTime", syncedTime[i]);
                    jsonArray.put(jsonObject);
                }
            }

            try{

                File root = Environment.getExternalStorageDirectory();
                File path = new File(root.getAbsolutePath() + "/" + FOLDER_NAME + "/" +
                        artistName + "/" + albumTitle );
                path.mkdirs();
                File file = new File(path, songTitle + ".txt");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));
                outputStreamWriter.write(jsonArray.toString());
                outputStreamWriter.flush();
                outputStreamWriter.close();

                Toast.makeText(this, "Lyrics saved successfully!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText( this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private void syncLyrics() {

        int syncTime = 0;

        if ( mediaPlayer != null && mediaPlayer.isPlaying() ) {

            syncTime = mediaPlayer.getCurrentPosition();
        }

        vibrator.vibrate(50);
        if (lyrics.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please select lyrics file", Toast.LENGTH_LONG).show();
        } else {
            if ( mediaPlayer == null || !mediaPlayer.isPlaying() ) {
                Toast.makeText(getApplicationContext(), "Please play the song", Toast.LENGTH_LONG).show();
            } else {

                try {
                    if (lyrics.size() != lyricPosition) {

                        JSONObject jsonObject = new JSONObject();
                        // jsonObject.put("lyric", lyrics.get(lyricPosition++));
                        jsonObject.put("lyric", textViewCenterLyric.getText().toString());
                        lyricPosition++;
                        jsonObject.put("endTime", syncTime);
                        jsonArraySyncedLyrics.put(jsonObject);
                    }

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                try {

                    if ( lyricPosition <= lyrics.size()-1 ) {
                        textViewTopLyric.setText(lyrics.get(lyricPosition - 1));
                        textViewCenterLyric.setText(lyrics.get(lyricPosition));
                    }

                    if ( lyricPosition <= lyrics.size()-2 ) {
                        textViewBottomLyric.setText(lyrics.get(lyricPosition + 1));
                    } else {
                        textViewBottomLyric.setText("");
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    private void updateLyricPosition( int seekBarPosition ) {
        if ( lyricsFound ){

            if ( seekBarPosition == 0 ){
                lyricPosition = 0;
                textViewTopLyric.setText("");
                return;
            }

            int i = 0;
            for (long time : syncedTime) {

                if ( time >= seekBarPosition ) {
                    lyricPosition = i;
                    return;
                }
                i++;
            }
        }
    }



    private void setSongDetails() {

        try {

            if (albumArt != null) {

                imageViewAlbumArt.setImageURI(Uri.parse(albumArt));
            }

            textViewSongTitle.setText(songTitle);
            textViewSongDetails.setText(albumTitle + " | " + artistName);

            seekBar.setMax((int) (songDuration));
            textViewCurrentTime.setText("0:00");
            textViewTotalTime.setText(Tools.getSongTime(songDuration));


        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

    }


    private void fetchLyrics() {


        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root + "/" + FOLDER_NAME + "/" + artistName.replace("/", "-") + "/" +
                albumTitle.replace("/", "-") + "/" + songTitle + ".txt");

        if (file.exists()) {

            try {

                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

                String lyrics = bufferedReader.readLine();
                JSONArray jsonArray = new JSONArray(lyrics);
                int length = jsonArray.length();
                syncedLyrics = new String[length];
                syncedTime = new long[length];
                for (int i = 0; i < length; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    syncedLyrics[i] = jsonObject.getString("lyric");
                    syncedTime[i] = jsonObject.getLong("endTime");
                }
                lyricsFound = true;

                buttonSyncLyric.setEnabled(false);
                buttonSaveLyrics.setEnabled(false);
                buttonSelectFile.setEnabled(false);
                imageViewPreviousLyric.setEnabled(false);
                imageViewNextLyric.setEnabled(false);

                imageViewPreviousLyric.setImageResource(R.drawable.ic_action_up_light);
                imageViewNextLyric.setImageResource(R.drawable.ic_action_down_light);

                buttonSyncLyric.setBackgroundResource(R.drawable.button_disabled_background);
                buttonSaveLyrics.setBackgroundResource(R.drawable.button_disabled_background);
                buttonSelectFile.setBackgroundResource(R.drawable.button_disabled_background);

                buttonSyncLyric.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonSaveLyrics.setTextColor(getResources().getColor(R.color.colorPrimary));
                buttonSelectFile.setTextColor(getResources().getColor(R.color.colorPrimary));


            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            if (lyricsFound) {
                if (lyricPosition > 0) {
                    textViewTopLyric.setText(syncedLyrics[lyricPosition - 1]);
                } else {
                    textViewTopLyric.setText("");
                }
                textViewCenterLyric.setText(syncedLyrics[lyricPosition]);
                textViewBottomLyric.setText(syncedLyrics[lyricPosition + 1]);
            }
        } else {

            getUnsyncedLyrics();
        }

    }



    private void getUnsyncedLyrics() {

        File root = Environment.getExternalStorageDirectory();
        File path = new File(root.getAbsolutePath() + "/" + "Data Of Unsynced Lyrics"
                + "/" + albumTitle );

        File file = new File(path, songTitle + ".txt");

        try{
            if (file.exists()) {

                lyrics.clear();

                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line;

                while ((line = bufferedReader.readLine()) != null ) {

                    if ( !line.isEmpty() ) {
                        lyrics.add(line);
                    }
                }
                if (lyrics.size() > 0) {
                    textViewCenterLyric.setText(lyrics.get(0));
                    textViewBottomLyric.setText(lyrics.get(1));
                } else {
                    textViewTopLyric.setText("");
                    textViewCenterLyric.setText("Lyrics are empty, Add lyrics by pressing here!");
                    textViewBottomLyric.setText("");
                }

            } else {

                textViewTopLyric.setText("");
                textViewBottomLyric.setText("");
                path.mkdirs();
                file.createNewFile();
                Toast.makeText(getApplicationContext(), "File successfully created!", Toast.LENGTH_LONG).show();

                textViewCenterLyric.setText("Lyrics are empty, Add lyrics by pressing here!");

            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public void onCompletion(MediaPlayer mp) {

        imageViewPlayPause.setImageResource(R.drawable.icon_play);
    }



    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK :
                Log.e("Media Player Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED :
                Log.e("Media Player Error", "MEDIA ERROR SERVER DIED" + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN :
                Log.e("Media Player Error", "MEDIA ERROR UNKNOWN" + extra);
                break;
        }
        return false;
    }


    //TO PLAY MEDIA IF IT IS NOT PLATING
    private void playMedia() {

        if(!mediaPlayer.isPlaying()) {
            resumeMedia();
        }
    }


    //TO PAUSE MEDIA IF IT IS PLAYING
    private void pauseMedia() {
        try {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

                resumePosition =  mediaPlayer.getCurrentPosition();
                isPlaying = false;
                imageViewPlayPause.setImageResource(R.drawable.icon_play);
            }
        } catch (Exception e) {
            Log.e("Service-pauseMedia()", e.toString());
        }
    }


    //TO RESUME MEDIA IF NOT PLAYING
    private void resumeMedia() {
        if(!mediaPlayer.isPlaying()) {

            setSongProgress(songDuration);
            mediaPlayer.seekTo((int)resumePosition);
            mediaPlayer.start();
            isPlaying = true;
            imageViewPlayPause.setImageResource(R.drawable.ic_action_pause);
        }
    }



    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        try {
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:

                    //RESUME PLAYBACK WEN THE AUDIO FOCUS GAINED
                    if (mediaPlayer != null) {

                        if (!mediaPlayer.isPlaying()) {
                            isPlaying = true;
                            resumeMedia();
                            imageViewPlayPause.setImageResource(R.drawable.icon_play);

                        } else if ( mediaPlayer.isPlaying() ) {
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        }
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:

                    //Lost focus for an unbounded amount of time:
                    // stop playback and release media player
                    if(mediaPlayer != null && mediaPlayer.isPlaying() ) {

                        isPlaying = false;
                        pauseMedia();
                        imageViewPlayPause.setImageResource(R.drawable.ic_action_pause);
                    }
                    // removeAudioFocus();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media player because playback
                    // is likely to resume
                    if ( ( mediaPlayer != null ) && ( mediaPlayer.isPlaying() ) ) {
                        mediaPlayer.pause();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if ( mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.3f, 0.3f);
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("ERROR_OnAudioFocusChang", e.toString());
        }
    }

    private Handler handler = new Handler();


    public void setSongProgress(long initial) {
        seekBar.setMax((int)initial);
        handler.postDelayed(Update, 200);
    }

    private Runnable Update = new Runnable() {

        @Override
        public void run() {
            if(isPlaying && mediaPlayer != null) {
                try {
                    //Getting the mediaplayer lyricPosition
                    long position = mediaPlayer.getCurrentPosition();
                    //Updating the current song time in bottom extra layout
                    textViewCurrentTime.setText(Tools.getSongTime(position));
                    //Updating the current song time for seekbar
                    seekBar.setProgress((int) position);
                    try{
                        //If lyrics are found and lyric lyricPosition should be less than lyrics length
                        //from LyricsFragment

                        if(lyricsFound  && ( lyricPosition <= syncedLyrics.length )) {
                            if(position <= syncedTime[lyricPosition]) {

                                //Checking for top textview to not set text for 1st lyric.
                                if ( lyricPosition > 0) {
                                    textViewTopLyric.setText(syncedLyrics[lyricPosition - 1]);
                                }
                                textViewCenterLyric.setText(syncedLyrics[lyricPosition]);
                                textViewBottomLyric.setText(syncedLyrics[lyricPosition +1]);
                            } else {

                                if ( lyricPosition < syncedLyrics.length-1 ) {
                                    lyricPosition++;
                                    textViewTopLyric.setText(syncedLyrics[lyricPosition -1]);
                                    textViewCenterLyric.setText(syncedLyrics[lyricPosition]);
                                }
                                //Checking for bottom textview to not set text for last lyric.
                                if (lyricPosition < syncedLyrics.length-2 ) {
                                    textViewBottomLyric.setText(syncedLyrics[lyricPosition + 1]);
                                } else {
                                    //If last lyric set bottom text empty.
                                    textViewBottomLyric.setText("");
                                }
                            }
                        }
                        handler.postDelayed(Update, 100);
                    } catch (Exception e) {
                        Log.e("NEW_METHOD_RUNNABLE", e.toString());
                    }
                } catch (Exception e) {
                    Log.e("Exception Handler", e.toString());
                }
            }
        }
    };


    @Override
    public void onBackPressed() {

        if (mediaPlayer != null) {

            if (mediaPlayer.isPlaying()) {
                pauseMedia();
            }
            mediaPlayer.release();
        }
        super.onBackPressed();
    }
}
