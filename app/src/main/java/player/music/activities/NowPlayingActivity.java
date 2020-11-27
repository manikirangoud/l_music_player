package player.music.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import player.music.Constants;
import player.music.MainActivity;
import player.music.R;
import player.music.adapters.NowPlayingAdapter;
import player.music.adapters.ViewPagerAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Preferences;
import player.music.extras.Tools;
import player.music.fragments.LyricsFragment;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.managers.DatabaseManager;
import player.music.objects.Song;
import player.music.services.PlaybackService;

public class NowPlayingActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {


    private RecyclerView recyclerView;
    private NowPlayingAdapter nowPlayingAdapter;

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;

    private SharedPreferences sharedPreferences;
    private AppCompatSeekBar seekBar;
    private List<Song> songs;

    //private MyTextView mtvCenterLyricsNp;

    private UbuntuTextView mtvNumberOfTracks, mtvSongTitle, mtvArtistName,
                        mtvSongDuration;
    private TickerView mtvRunTime, mtvTotalTime;
    private ImageView ivPlayPause, ivPreviousTrack, ivNextTrack, ivShuffle;
    private RelativeLayout trShuffle;
    private boolean isPlaying;
    private int currentPosition;
    private long currentDuration;
    private Handler handler = new Handler();

    private FetchPlaylist fetchPlaylist;
    private FloatingActionButton fabTraverseUp;

    private List<Song> shuffledSongs;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.now_playing_layout);


        recyclerView = findViewById(R.id.recycler_view_playlist_activity);
        //mtvCenterLyricsNp = findViewById(R.id.tv_lyrics_center);
        seekBar = findViewById(R.id.sbNowPlaying);
        ivPlayPause = findViewById(R.id.iv_play_pause_now_playing);
        ivPreviousTrack = findViewById(R.id.iv_previous_now_playing);
        ivNextTrack = findViewById(R.id.iv_next_now_playing);
        mtvRunTime = findViewById(R.id.tv_run_time_now_playing);
        mtvTotalTime = findViewById(R.id.tv_total_time_now_playing);
        trShuffle = findViewById(R.id.tr_shuffle);
        ivShuffle = findViewById(R.id.iv_shuffle);
        mtvNumberOfTracks = findViewById(R.id.utv_track_count);
        mtvSongTitle = findViewById(R.id.utv_track_title);
        mtvArtistName = findViewById(R.id.utv_artist_name);
        mtvSongDuration = findViewById(R.id.utv_track_duration);
        fabTraverseUp = findViewById(R.id.fab_traverse_up);
        viewPager = findViewById(R.id.vp_now_playing);


        sharedPreferences = getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);



        register_UpdateControls();

        mtvRunTime.setCharacterLists(TickerUtils.provideNumberList());
        mtvTotalTime.setCharacterLists(TickerUtils.provideNumberList());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int seekBarPosition = seekBar.getProgress();

                if ( PlaybackService.mediaPlayer != null ) {

                    PlaybackService.mediaPlayer.seekTo(seekBarPosition);

                    if ( !PlaybackService.mediaPlayer.isPlaying() ) {
                        Preferences.setCurrentSongPosition(sharedPreferences, seekBarPosition);
                        sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS));

                    }
                } else {
                    Preferences.setCurrentSongPosition(sharedPreferences, seekBarPosition);
                    sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS));
                }
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                updateLyricPosition( seekBarPosition );

            }
        });

        fetchPlaylist = new FetchPlaylist();
        fetchPlaylist.execute();



        ivPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ON BOTTOM ACTIONS [ PLAY / PAUSE ] CLICKED
                checkService();
                Intent intent = new Intent();
                intent.setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS);
                sendBroadcast(intent);

                if (isPlaying) {
                    ivPlayPause.setImageResource(R.drawable.icon_play);
                    isPlaying = false;
                } else {
                    ivPlayPause.setImageResource(R.drawable.ic_action_pause);
                    isPlaying = true;
                }
                Preferences.setIsPlaying(sharedPreferences, isPlaying);

                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));

            }
        });


        //PLAYING THE PREVIOUS TRACKS IN THE CURRENT PLAYLIST DATABASE
        ivPreviousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(NowPlayingActivity.this, MainActivity.lyricPosition);

                DatabaseManager.playTrackAtPosition(NowPlayingActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist - 1));
                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));


                recyclerView.scrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(NowPlayingActivity.this) - 1);

            }
        });


        //PLAYING THE NEXT TRACKS IN THE CURRENT PLAYLIST DATABASE
        ivNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(NowPlayingActivity.this, MainActivity.lyricPosition);


                DatabaseManager.playTrackAtPosition(NowPlayingActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist + 1));
                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));


                recyclerView.scrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(NowPlayingActivity.this) - 1);            }
        });



        trShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final int size = songs.size();
                int random;

                if ( size > 0 ) {

                    random = new Random().nextInt(size);
                    Song song = songs.get(random);

                    Preferences.setSongPath(sharedPreferences, song.getPath());
                    Preferences.setArtistName(sharedPreferences, song.getArtistName());
                    Preferences.setCurrentAlbumArt(sharedPreferences, song.getAlbumArt());
                    Preferences.setCurrentSongTitle(sharedPreferences, song.getTitle());
                    Preferences.setCurrentSongDuration(sharedPreferences, song.getDuration());
                    Preferences.setIsPlaying(sharedPreferences, true);
                    Preferences.setIsSongCompleted(sharedPreferences, false);
                    Preferences.setCurrentPlaylistSize(sharedPreferences, size);
                    Preferences.setCurrentSongId(sharedPreferences, song.getId());
                    Preferences.setCurrentAlbumTitle(sharedPreferences, song.getAlbumTitle());
                    Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, 1);
                    Preferences.setLyricPosition(NowPlayingActivity.this, 0);
                    Preferences.setShuffle(sharedPreferences, true);


                    //SENDING RECEIVER TO PLAY A SPECIFIC SONG
                    Intent intentSetMedia = new Intent();
                    intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
                    sendBroadcast(intentSetMedia);


                    //Sending receiver to set now playing details
                    sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));


                    //Sending receiver to set bottom details
                    Intent intentSetBottomDetails = new Intent();
                    intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
                    sendBroadcast(intentSetBottomDetails);

                    if (shuffledSongs != null && !shuffledSongs.isEmpty()) {

                        shuffledSongs.clear();
                    } else {

                        shuffledSongs = new ArrayList<>();
                    }
                    shuffledSongs.add(song);


                    //For adding all shuffled songs.
                    do {

                        boolean found = false;
                        random = new Random().nextInt(size);
                        Song song1 = songs.get(random);

                        for (Song check : shuffledSongs) {
                            if (check.getId() == song1.getId()) {
                                found = true;
                            }
                        }

                        if (!found) {
                            shuffledSongs.add(song1);
                        }

                    } while (shuffledSongs.size() < size);

                    updateRecyclerView();


                    //Updating the playlist data to the database
                    Thread threadUpdateDatabase = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            DatabaseManager.updateCurrentPlaylistTable();
                            DatabaseManager.insertCurrentPlaylist(shuffledSongs);
                        }
                    });
                    threadUpdateDatabase.start();

                }

            }
        });


        ivShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                trShuffle.performClick();
            }
        });


        sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                if ( dy < 0 ){
                    fabTraverseUp.setVisibility(View.VISIBLE);
                } else {

                    fabTraverseUp.setVisibility(View.GONE);
                }
            }
        });

        fabTraverseUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recyclerView.scrollToPosition(0);
            }
        });



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                onViewPagerSwipe(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });


    }


    private void updateRecyclerView() {

        if (nowPlayingAdapter != null) {

            NowPlayingAdapter.songs = shuffledSongs;
            nowPlayingAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(0);
        }
    }



    private void register_UpdateControls() {

        //Registering the UpdateControls Receiver
        registerReceiver(UpdateControls, new IntentFilter(
                Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
    }



    private BroadcastReceiver UpdateControls = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            if ( intent.getAction().equals(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS) ) {

                updateUserInterface();
            }
        }
    };


    private void updateUserInterface() {

        isPlaying = Preferences.getIsPlaying(sharedPreferences);
        currentPosition = Preferences.getCurrentSongPosition(sharedPreferences);
        currentDuration = Preferences.getCurrentSongDuration(sharedPreferences);
        int songPosition = Preferences.getCurrentSongPositionFromPlaylist(this);


        seekBar.setMax( (int) currentDuration );
        mtvRunTime.setText(Tools.getSongTime(currentPosition) + "");
        mtvTotalTime.setText(Tools.getSongTime(currentDuration) + "");
        mtvSongTitle.setText(Preferences.getCurrentSongTitle(sharedPreferences));
        mtvArtistName.setText(Preferences.getArtistName(sharedPreferences));
        mtvSongDuration.setText("");



        viewPager.setCurrentItem(songPosition-1);

      /*  Glide.with(this).load("file://" +
                sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_ART, ""))
                .placeholder(R.drawable.album_icon)
                .thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivAlbumArt);*/

        if (isPlaying) {

            ivPlayPause.setImageResource(R.drawable.ic_action_pause);
        } else {
            ivPlayPause.setImageResource(R.drawable.icon_play);
        }


        if (PlaybackService.mediaPlayer != null) {

            if ( PlaybackService.mediaPlayer.isPlaying()) {
                seekBar.setProgress(PlaybackService.mediaPlayer.getCurrentPosition());
            } else {
                seekBar.setProgress(currentPosition);
            }
        } else {
            seekBar.setProgress(currentPosition);
        }

        updateSeekBar();
    }



    private void updateSeekBar() {
        handler.postDelayed(Update, 200);
    }



    private Runnable Update = new Runnable() {

        @Override
        public void run() {

            if(isPlaying && PlaybackService.mediaPlayer != null) {
                try {
                    /*ivAlbumArt.animate().rotationBy(360).withEndAction(Update).setDuration(10000)
                            .setInterpolator(new LinearInterpolator()).start();*/

                    //Getting the mediaplayer position
                    long position = PlaybackService.mediaPlayer.getCurrentPosition();
                    //Updating the current song time in bottom extra layout
                    mtvRunTime.setText(Tools.getSongTime(position));
                    //Updating the current song time for seekbar
                    seekBar.setProgress((int) position);

                    //If lyrics are lyricsFound and lyric position should be less than lyrics length
                    //from LyricsFragment
                   /* if (Preferences.getLyricsEnabled(sharedPreferences)) {
                        if (LyricsFragment.lyricsFound && MainActivity.lyricPosition <= LyricsFragment.syncedLyrics.size()) {
                            if (position <= LyricsFragment.syncedTime.get(MainActivity.lyricPosition)) {

                                //Checking for top textview to not set text for 1st lyric.
                                if (mtvCenterLyricsNp != null) {
                                    mtvCenterLyricsNp.setText(LyricsFragment.syncedLyrics.get(MainActivity.lyricPosition));
                                }
                            } else {

                                if (MainActivity.lyricPosition < LyricsFragment.syncedLyrics.size() - 1) {
                                    if (mtvCenterLyricsNp != null) {
                                        mtvCenterLyricsNp.setText(LyricsFragment.syncedLyrics.get(MainActivity.lyricPosition));
                                    }
                                }
                            }
                        } else {
                            mtvCenterLyricsNp.setText(getResources().getString(R.string.lyrics_not_found));
                        }
                        handler.postDelayed(Update, 100);
                    } else {
                        mtvCenterLyricsNp.setText(getResources().getString(R.string.please_enable_lyrics));
                    }*/

                } catch (Exception e) {
                    Log.e("Exception Handler", e.toString());
                }
            }
        }
    };



    private void updateLyricPosition( int seekBarPosition ) {

        if ( LyricsFragment.lyricsFound){

            if ( seekBarPosition == 0 ){
                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(this, MainActivity.lyricPosition);
                LyricsFragment.textViewTop.setText("");
                return;
            }


            int i = 0;
            for (long time : LyricsFragment.syncedTime) {

                if ( time >= seekBarPosition ) {
                    MainActivity.lyricPosition = i;
                    if ( MainActivity.lyricPosition == 0 ) {
                        LyricsFragment.textViewTop.setText("");
                    }
                    Preferences.setLyricPosition(this, MainActivity.lyricPosition);
                    return;
                }
                i++;
            }
        }
    }



    //CHECKING IF THE PLAYBACK SERVICE IS ALREADY EXISTING
    public void checkService() {

        if(PlaybackService.mediaPlayer != null) {
            Log.i("Service", "IS Already Running");
        } else {
            MainActivity.startService = new MainActivity.StartService();
        }
    }


    private class UpdatePlaylist extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }



    private class FetchPlaylist extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("LyricalMusic",
                    MODE_PRIVATE, null);

            try {

                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM currentPlaylist", null);

                songs = new ArrayList<>();

                if (cursor != null && cursor.moveToFirst()) {

                    do {

                        Song song = new Song();

                        song.setId(cursor.getInt(cursor.getColumnIndexOrThrow("SongId")));
                        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("SongTitle")));
                        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow("SongPath")));
                        song.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow("SongDuration")));
                        song.setAlbumArt(cursor.getString(cursor.getColumnIndexOrThrow("AlbumArt")));
                        song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow("ArtistName")));
                        song.setAlbumTitle(cursor.getString(cursor.getColumnIndexOrThrow("AlbumTitle")));


                        songs.add(song);

                    } while (cursor.moveToNext());
                    cursor.close();
                }
            } catch (Exception e) {

                if (!e.toString().contains("SQLiteException")) {
                    Log.e("NOWPLAYINGACTIVITY", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {



                ViewPagerAdapter.songs = songs;
                viewPagerAdapter = new ViewPagerAdapter(NowPlayingActivity.this);
                viewPager.setAdapter(viewPagerAdapter);




            } catch (Exception e){
                Toast.makeText(NowPlayingActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }



            nowPlayingAdapter = new NowPlayingAdapter(NowPlayingActivity.this);
            NowPlayingAdapter.songs = songs;
            recyclerView.setLayoutManager(new LinearLayoutManager(NowPlayingActivity.this));
            recyclerView.setAdapter(nowPlayingAdapter);


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration( recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable( recyclerView.getContext(),
                    R.drawable.divider_small));
            recyclerView.addItemDecoration(dividerItemDecoration);


            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(NowPlayingActivity.this) - 1);
                }
            });



            if ( songs != null && !songs.isEmpty() ) {

                int numberOfTracks = songs.size();
                if (numberOfTracks > 1) {

                    String text = numberOfTracks + " Tracks";
                    mtvNumberOfTracks.setText(text);
                } else {

                    String text = numberOfTracks + " Track";
                    mtvNumberOfTracks.setText(text);
                }
            } else {
                String text = 0 + " Track";
                mtvNumberOfTracks.setText(text);
            }
        }


    }



    @Override
    public void recyclerViewItemClicked(View view, int position, long itemId) {

        SharedPreferences sharedPreferences = view.getContext()
                .getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);
        Preferences.setSongPath(sharedPreferences, songs.get(position).getPath());
        Preferences.setArtistName(sharedPreferences, songs.get(position).getArtistName());
        Preferences.setCurrentAlbumArt(sharedPreferences, songs.get(position).getAlbumArt());
        Preferences.setCurrentSongTitle(sharedPreferences, songs.get(position).getTitle());
        Preferences.setCurrentSongDuration(sharedPreferences, songs.get(position).getDuration());
        Preferences.setIsPlaying(sharedPreferences, true);
        Preferences.setIsSongCompleted(sharedPreferences, false);
        Preferences.setCurrentPlaylistSize(sharedPreferences, songs.size());
        Preferences.setCurrentSongId(sharedPreferences, songs.get(position).getId());
        Preferences.setCurrentAlbumTitle(sharedPreferences, songs.get(position).getAlbumTitle());
        Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, (position + 1));
        Preferences.setLyricPosition(view.getContext(), 0);


        //SENDING RECEIVER TO PLAY A SPECIFIC SONG
        Intent intentSetMedia = new Intent();
        intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
        sendBroadcast(intentSetMedia);


        //Sending receiver to set now playing details
        sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));


        //Sending receiver to set bottom details
        Intent intentSetBottomDetails = new Intent();
        intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        sendBroadcast(intentSetBottomDetails);


        viewPager.setCurrentItem(position, true);

    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }

    @Override
    public void recyclerViewOverflowClicked(View view, int position) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        handler.removeCallbacks(Update);
        unregisterReceiver(UpdateControls);
        finish();
        overridePendingTransition(R.anim.no_action, R.anim.up_to_down);
    }


    public void onViewPagerSwipe(int position){

        SharedPreferences sharedPreferences = this.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);
        Preferences.setSongPath(sharedPreferences, songs.get(position).getPath());
        Preferences.setArtistName(sharedPreferences, songs.get(position).getArtistName());
        Preferences.setCurrentAlbumArt(sharedPreferences, songs.get(position).getAlbumArt());
        Preferences.setCurrentSongTitle(sharedPreferences, songs.get(position).getTitle());
        Preferences.setCurrentSongDuration(sharedPreferences, songs.get(position).getDuration());
        Preferences.setIsPlaying(sharedPreferences, true);
        Preferences.setIsSongCompleted(sharedPreferences, false);
        Preferences.setCurrentPlaylistSize(sharedPreferences, songs.size());
        Preferences.setCurrentSongId(sharedPreferences, songs.get(position).getId());
        Preferences.setCurrentAlbumTitle(sharedPreferences, songs.get(position).getAlbumTitle());
        Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, (position + 1));
        Preferences.setLyricPosition(this, 0);


        //SENDING RECEIVER TO PLAY A SPECIFIC SONG
        Intent intentSetMedia = new Intent();
        intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
        sendBroadcast(intentSetMedia);


        //Sending receiver to set now playing details
        sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));


        //Sending receiver to set bottom details
        Intent intentSetBottomDetails = new Intent();
        intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        sendBroadcast(intentSetBottomDetails);

        viewPager.setCurrentItem(position, true);

    }


}
