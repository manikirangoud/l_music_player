package player.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.palette.graphics.Palette;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import player.music.activities.FeedbackActivity;
import player.music.activities.NowPlayingActivity;
import player.music.activities.PlaylistsActivity;
import player.music.activities.SearchActivity;
import player.music.activities.SettingsActivity;
import player.music.adapters.NowPlayingAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Tools;
import player.music.extras.Preferences;
import player.music.fragments.AlbumsFragment;
import player.music.fragments.ArtistsFragment;
import player.music.fragments.LyricsFragment;
import player.music.fragments.TracksFragment;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.managers.DatabaseManager;
import player.music.objects.Song;
import player.music.services.PlaybackService;

public class MainActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {


    private static final int VIEWPAGER_OFF_SCREEN_PAGE_LIMIT = 5;

    private static String TAG = "MAIN_ACTIVITY";

    private Toolbar toolbarMain;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPagerMain;
    private TabLayout tabLayout;
    private SearchView searchView;
    private SharedPreferences sharedPreferences;
    private int viewPagerPosition;
    private SlidingPaneLayout slidingPaneLayout;
    public static StartService startService;
    public static int lyricPosition;
    private ViewPager vpNowPlaying;


    private static PlaybackService playbackService;
    public static String currentAlbumArt;
    public static String currentSongTitle;
    public static String currentArtistName;
    public static String currentSongPath;
    public static boolean isPlaybackServiceBound = false;
    public static boolean isPlaying;
    public static boolean isSongCompleted;
    public static boolean isShuffleOn = false;

    public static String repeatTag = Constants.STRINGS.REPEAT_ALL;
    public static long currentSongDuration;
    public static int currentSongPosition;

    //VARIABLES OF BOTTOM ACTIONS BAR
    //  private ImageView bottomAction;
    private Handler handler = new Handler();
    private TextView tvSongTitle, tvArtistName;
    private ImageView imageViewAlbumArt;
    private ProgressBar pbBottom;
    private CardView cvBottomActions;

    //VARIABLES OF EXTRA BOTTOM ACTIONS BAR
    private TickerView mtvRunTimeBottom;
    private TickerView mtvTotalTimeBottom;
    private ImageView playPauseBottom;
    private ImageView imageViewPreviousTrack;
    private ImageView imageViewNextTrack;
    private ImageView imageViewShuffle;
    private ImageView imageViewRepeatMedia;
    private ImageView imageViewPlaylistBottom;


    private static Activity activity;
    private Vibrator vibrator;
    private CoordinatorLayout clMain;
    private BottomSheetBehavior bottomSheetBehavior;
    private boolean bsExpanded = false;


    //Variables of now playing bottom sheet.
    private RecyclerView recyclerView;
    private NowPlayingAdapter nowPlayingAdapter;
    private AppCompatSeekBar sbNowPlaying;
    private List<Song> songs;

    public UbuntuTextView mtvCenterLyricsNp;

    private UbuntuTextView utvTrackCountNp;
    private UbuntuTextView utvTrackNp;
    private UbuntuTextView mtvSongTitleNp;
    private UbuntuTextView mtvArtistNameNp;
    private TickerView mtvRunTimeNp;
    private TickerView mtvTotalTimeNp;
    private ImageView ivPlayPauseNp;
    private ImageView ivPreviousTrackNp;
    private ImageView ivNextTrackNp;
    private ImageView ivShuffleNp;
    private int currentPosition;
    private long currentDuration;

    private MainActivity.FetchPlaylist fetchPlaylist;
    private FloatingActionButton fabTraverseUp;

    private List<Song> shuffledSongs;
    private CoordinatorLayout clNowPlaying;
    private ImageView ivBottomNavigator;
    private LinearLayout llBottom;
    private LinearLayout llRVNowPlaying;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);


        setContentView(R.layout.activity_main);


        toolbarMain = findViewById(R.id.toolbar_main);
        viewPagerMain = findViewById(R.id.view_pager_main);
        slidingPaneLayout = findViewById(R.id.sliding_pane_main_layout);
        tabLayout = findViewById(R.id.tab_layout_main);
        //  bottomAction = findViewById(R.id.action_bottom_song_playing_layout);
        tvArtistName = findViewById(R.id.artist_name_bottom_playing_layout);
        tvSongTitle = findViewById(R.id.song_title_bottom_playing_layout);
        imageViewAlbumArt = findViewById(R.id.iv_album_bottom);
        mtvRunTimeBottom = findViewById(R.id.mtv_run_time);
        mtvTotalTimeBottom = findViewById(R.id.mtv_total_time);
        playPauseBottom = findViewById(R.id.image_view_extra_action_play_pause);
        pbBottom = findViewById(R.id.pb_song_main);
        imageViewPreviousTrack = findViewById(R.id.image_view_play_previous_bottom_extra);
        imageViewNextTrack = findViewById(R.id.image_view_play_next_bottom_extra);
        imageViewShuffle = findViewById(R.id.iv_shuffle_bottom);
        imageViewRepeatMedia = findViewById(R.id.iv_repeat_media_bottom);
        cvBottomActions = findViewById(R.id.ll_bottom_actions);
        imageViewPlaylistBottom = findViewById(R.id.iv_playlist_bottom);
        searchView = findViewById(R.id.search_view);
        clMain = findViewById(R.id.cl_main);
        ivBottomNavigator = findViewById(R.id.view_bottom);
        clNowPlaying = findViewById(R.id.cl_now_playing);
        llBottom = findViewById(R.id.ll_bottom);
        llRVNowPlaying = findViewById(R.id.ll_rv_now_playing);


        vpNowPlaying = findViewById(R.id.vp_now_playing);


        //Content of bottom sheet now playing.
        recyclerView = findViewById(R.id.recycler_view_playlist_activity);
        mtvCenterLyricsNp = findViewById(R.id.tv_lyrics_center);
        sbNowPlaying = findViewById(R.id.sbNowPlaying);
        ivPlayPauseNp = findViewById(R.id.iv_play_pause_now_playing);
        ivPreviousTrackNp = findViewById(R.id.iv_previous_now_playing);
        ivNextTrackNp = findViewById(R.id.iv_next_now_playing);
        mtvRunTimeNp = findViewById(R.id.tv_run_time_now_playing);
        mtvTotalTimeNp = findViewById(R.id.tv_total_time_now_playing);
        ivShuffleNp = findViewById(R.id.iv_shuffle);
        utvTrackCountNp = findViewById(R.id.utv_track_count);
        utvTrackNp = findViewById(R.id.utv_track);
        mtvSongTitleNp = findViewById(R.id.utv_track_title);
        mtvArtistNameNp = findViewById(R.id.utv_artist_name);
       // collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        fabTraverseUp = findViewById(R.id.fab_traverse_up);


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


        setSupportActionBar(toolbarMain);

        lyricPosition = sharedPreferences.getInt(Constants.STRINGS.LYRIC_POSITION, 0);
        repeatTag = sharedPreferences.getString(Constants.STRINGS.REPEAT_TAG, repeatTag);


        mtvRunTimeBottom.setCharacterLists(TickerUtils.provideNumberList());
        mtvTotalTimeBottom.setCharacterLists(TickerUtils.provideNumberList());


        new FetchData().execute();

        activity = this;

        registerReceiver(ChangeBackground, new IntentFilter(Constants.ACTIONS.CHANGE_BACKGROUND));

        try {
            DatabaseManager.openDataBase(this);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        //initCollapsingToolbar();

        switch (repeatTag) {
            case Constants.STRINGS.REPEAT_OFF:
                imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_off);
                break;

            case Constants.STRINGS.REPEAT_ONE:
                imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_one);
                break;

            case Constants.STRINGS.REPEAT_ALL:
                imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_all);
                break;
        }


        //PLAYING THE PREVIOUS TRACKS IN THE CURRENT PLAYLIST DATABASE
        imageViewPreviousTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrator.vibrate(50);
                lyricPosition = 0;
                Preferences.setLyricPosition(MainActivity.this, lyricPosition);

                DatabaseManager.playTrackAtPosition(MainActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist - 1));
            }
        });


        //PLAYING THE NEXT TRACKS IN THE CURRENT PLAYLIST DATABASE
        imageViewNextTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrator.vibrate(50);
                lyricPosition = 0;
                Preferences.setLyricPosition(MainActivity.this, lyricPosition);

                DatabaseManager.playTrackAtPosition(MainActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist + 1));
            }
        });


        //SETTING THE SHUFFLE IN THE SHARED PREFERENCES
        imageViewShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vibrator.vibrate(50);
                if (isShuffleOn) {
                    isShuffleOn = false;
                    imageViewShuffle.setImageResource(R.drawable.ic_action_shuffle_off);
                } else {
                    isShuffleOn = true;
                    imageViewShuffle.setImageResource(R.drawable.ic_action_shuffle_on);
                }
                Preferences.setShuffle(sharedPreferences, isShuffleOn);
            }
        });


        imageViewRepeatMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                switch (repeatTag) {

                    case Constants.STRINGS.REPEAT_OFF:
                        repeatTag = Constants.STRINGS.REPEAT_ONE;
                        imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_one);
                        break;

                    case Constants.STRINGS.REPEAT_ONE:
                        repeatTag = Constants.STRINGS.REPEAT_ALL;
                        imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_all);
                        break;

                    case Constants.STRINGS.REPEAT_ALL:
                        repeatTag = Constants.STRINGS.REPEAT_OFF;
                        imageViewRepeatMedia.setImageResource(R.drawable.ic_action_repeat_off);
                        break;
                }
                Preferences.setRepeatTag(sharedPreferences, repeatTag);
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                sendBroadcast(new Intent(Constants.ACTIONS.SEARCH).putExtra("target", s));
                return true;
            }
        });


        cvBottomActions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
                overridePendingTransition(R.anim.down_to_up, R.anim.no_action);
            }
        });


        imageViewPlaylistBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(50);
                startActivity(new Intent(MainActivity.this, PlaylistsActivity.class));
                overridePendingTransition(R.anim.down_to_up, R.anim.no_action);
            }
        });



        sendBroadcast(new Intent(Constants.ACTIONS.CHANGE_BACKGROUND));


        View bottomSheet = findViewById(R.id.ll_bottom_actions);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                // React to state change
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    bsExpanded = true;
                    //Making completely invisible the sliding pane layout.
                    slidingPaneLayout.setAlpha(0.0f);
                } else if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    //Making completely invisible the sliding pane layout.
                    slidingPaneLayout.setAlpha(0.5f);
                } else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    //Making completely invisible the sliding pane layout.
                    slidingPaneLayout.setAlpha(1.0f);
                    bsExpanded = false;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events

                if (!bsExpanded) {
                    setNowPlayingContent();
                    bsExpanded = true;
                }
            }
        });




        slidingPaneLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(@NonNull View view, float v) {

            }

            @Override
            public void onPanelOpened(@NonNull View view) {

                ivBottomNavigator.setImageResource( R.drawable.ic_action_left_chevron_dark);

            }

            @Override
            public void onPanelClosed(@NonNull View view) {

                ivBottomNavigator.setImageResource( R.drawable.ic_action_right_chevron_dark);

            }
        });


        ivBottomNavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( slidingPaneLayout.isOpen() ){
                    slidingPaneLayout.closePane();
                } else {
                    slidingPaneLayout.openPane();
                }
            }
        });


    }

    //SETTING MAIN MENU OF APP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }



    private void updateLyricPosition(int seekBarPosition) {

        if (LyricsFragment.lyricsFound) {

            if (seekBarPosition == 0) {
                lyricPosition = 0;
                Preferences.setLyricPosition(this, lyricPosition);
                LyricsFragment.textViewTop.setText("");
                return;
            }


            int i = 0;
            for (long time : LyricsFragment.syncedTime) {

                if (time >= seekBarPosition) {
                    lyricPosition = i;
                    if (lyricPosition == 0) {
                        LyricsFragment.textViewTop.setText("");
                    }
                    if (lyricPosition >= LyricsFragment.syncedTime.size() - 1) {
                        LyricsFragment.textViewBottom.setText("");
                    }
                    Preferences.setLyricPosition(this, lyricPosition);
                    return;
                }
                i++;
            }
        }
    }


    private BroadcastReceiver ChangeBackground = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if ( intent.getAction() != null && intent.getAction().equals(Constants.ACTIONS.CHANGE_BACKGROUND)) {

                int position = sharedPreferences.getInt("background", 0);
                clMain.setBackgroundColor(getResources().getIntArray(R.array.themeColors)[position]);
            }
        }
    };


    private class FetchData extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {


            getPermission();

            slidingPaneLayout.openPane();

            //Registering the bottom actions bar broadcast
            register_UpdateBottomControls();

            getSharedPreferences();


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            checkService();

        }
    }


    //Asking and getting permissions from the user
    public void getPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != (PackageManager.PERMISSION_GRANTED) &&
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CALL_PHONE}, 1);
        } else {
            setUpFragments();
        }
    }


    //On permission given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        setUpFragments();
    }


    //CHECKING IF THE PLAYBACK SERVICE IS ALREADY EXISTING
    public void checkService() {

        if (PlaybackService.mediaPlayer != null) {
            Log.i("Service", "IS Already Running");
        } else {
            startService = new StartService();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //super.onConfigurationChanged(newConfig);
        // onRestart();
    }


    //Setting the fragments
    private void setUpFragments() {

        //Setting viewpager fragments
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new LyricsFragment(), getResources().getString(R.string.main_tab_layout_label_1));
        viewPagerAdapter.addFragments(new AlbumsFragment(), getResources().getString(R.string.main_tab_layout_label_2));
        viewPagerAdapter.addFragments(new TracksFragment(), getResources().getString(R.string.main_tab_layout_label_3));
        viewPagerAdapter.addFragments(new ArtistsFragment(), getResources().getString(R.string.main_tab_layout_label_4));
        viewPagerMain.setAdapter(viewPagerAdapter);
        viewPagerMain.setOffscreenPageLimit(VIEWPAGER_OFF_SCREEN_PAGE_LIMIT);

        viewPagerMain.setCurrentItem(sharedPreferences.getInt(Constants.STRINGS.VIEW_PAGER_POSITION,
                viewPagerPosition));
        tabLayout.setupWithViewPager(viewPagerMain);



    }


   /* //Setting sliding panes
    private void setUpSlidingPanes() {
        slidingPaneLayout.openPane();
    }
*/

    //Static class for starting service
    public static final class StartService {


        //Constructor with 1 parameter [ Activity ]

        public StartService() {
            Intent intentPlaybackService = new Intent(activity, PlaybackService.class);
            activity.startService(intentPlaybackService);
            Log.i("Main Activity : ", "Playback Service Started");
            activity.bindService(intentPlaybackService, serviceConnectionPlaybackService,
                    Context.BIND_AUTO_CREATE);
        }


        //Setting service connection
        static ServiceConnection serviceConnectionPlaybackService = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {

                // We've bound to LocalService, cast the IBinder and get LocalService instance
                PlaybackService.LocalBinder binder = (PlaybackService.LocalBinder) service;
                playbackService = binder.getService();
                isPlaybackServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                isPlaybackServiceBound = false;
                if (serviceConnectionPlaybackService != null) {
                    playbackService.unbindService(serviceConnectionPlaybackService);
                }
                Log.i("Service Connection", "Disconnected called");
            }
        };
    }


    //Broadcast receiver for setting bottom playing details updating
    public BroadcastReceiver UpdateBottomControls = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();

                if ( action != null &&  action.equalsIgnoreCase(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS)) {

                    currentAlbumArt = sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_ART, "");
                    currentSongTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, "");
                    currentArtistName = sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, "");
                    currentSongDuration = sharedPreferences.getLong(Constants.STRINGS.CURRENT_SONG_DURATION, 0);
                    currentSongPosition = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION, 0);
                    isPlaying = sharedPreferences.getBoolean(Constants.STRINGS.IS_PLAYING, false);
                    isSongCompleted = sharedPreferences.getBoolean(Constants.STRINGS.IS_SONG_COMPLETED, false);
                    isShuffleOn = sharedPreferences.getBoolean(Constants.STRINGS.IS_SHUFFLE_ON, false);
                    int position = Preferences.getCurrentSongPositionFromPlaylist(MainActivity.this);

                    try {
                        pbBottom.setMax((int) currentSongDuration);
                        mtvTotalTimeBottom.setText(Tools.getSongTime(currentSongDuration));

                        if (bsExpanded){
                            sbNowPlaying.setMax((int) currentDuration);
                            mtvRunTimeNp.setText(Tools.getSongTime(currentPosition) + "");
                            mtvTotalTimeNp.setText(Tools.getSongTime(currentDuration) + "");
                            mtvSongTitleNp.setText(Preferences.getCurrentSongTitle(sharedPreferences));
                            mtvArtistNameNp.setText(Preferences.getArtistName(sharedPreferences));
                            //mtvSongDuration.setText("");

                            vpNowPlaying.setCurrentItem( position - 1);

                            if (isPlaying) {
                                ivPlayPauseNp.setImageResource(R.drawable.ic_action_pause);
                            } else {
                                ivPlayPauseNp.setImageResource(R.drawable.icon_play);
                            }

                            //pbBottom.setMax((int) currentSongDuration);
                            //mtvTotalTimeBottom.setText(Tools.getSongTime(currentSongDuration));
                        }

                        if (isSongCompleted && !isPlaying) {
                            //        bottomAction.setImageResource(R.drawable.icon_play);
                            playPauseBottom.setImageResource(R.drawable.icon_play);
                            pbBottom.setProgress((int) currentSongDuration);
                            mtvRunTimeBottom.setText(Tools.getSongTime(currentSongDuration));

                            if (bsExpanded){
                                playPauseBottom.setImageResource(R.drawable.icon_play);
                                sbNowPlaying.setProgress((int) currentSongDuration);
                                mtvRunTimeNp.setText(Tools.getSongTime(currentSongDuration));
                            }
                        }
                        if (isPlaying && !isSongCompleted) {
                            playPauseBottom.setImageResource(R.drawable.icon_pause);
                            setSongProgress(currentSongDuration);
                            pbBottom.setProgress(currentSongPosition);
                            mtvRunTimeBottom.setText(Tools.getSongTime(currentSongPosition));

                            if (bsExpanded){
                                ivPlayPauseNp.setImageResource(R.drawable.icon_pause);
                                //setSongProgress(currentSongDuration);
                                sbNowPlaying.setProgress(currentSongPosition);
                                mtvRunTimeNp.setText(Tools.getSongTime(currentSongPosition));
                            }
                        } else if (!isPlaying && !isSongCompleted) {
                            playPauseBottom.setImageResource(R.drawable.icon_play);
                            imageViewAlbumArt.clearAnimation();
                            pbBottom.setProgress(currentSongPosition);
                            mtvRunTimeBottom.setText(Tools.getSongTime(currentSongPosition));

                            if (bsExpanded){
                                ivPlayPauseNp.setImageResource(R.drawable.icon_play);
                                sbNowPlaying.setProgress(currentSongPosition);
                                mtvRunTimeNp.setText(Tools.getSongTime(currentSongPosition));
                            }
                        }
                        tvSongTitle.setText(currentSongTitle);
                        tvArtistName.setText(currentArtistName);
                        try {
                            if (!currentAlbumArt.equalsIgnoreCase("null")) {
                                imageViewAlbumArt.setImageURI(Uri.parse(currentAlbumArt));

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                          Drawable drawable = Drawable.createFromPath(currentAlbumArt);
                                            Bitmap bitmap = drawable != null ? ((BitmapDrawable) drawable).getBitmap() : null;

                                            if(bitmap != null){
                                                Palette.Builder palette = Palette.from(bitmap);

                                                palette.generate(new Palette.PaletteAsyncListener() {
                                                    @Override
                                                    public void onGenerated(@Nullable Palette palette) {

                                                        String hexCode = Integer.toHexString(palette.getLightMutedColor(
                                                                getResources().getColor(R.color.colorPrimary)));

                                                        int colorCode = (Integer.parseInt( "55", 16) << 24) +
                                                                Integer.parseInt(hexCode.substring(2), 16);

                                                        llBottom.setBackgroundColor(colorCode);
                                                        clNowPlaying.setBackgroundColor(colorCode);
                                                        llRVNowPlaying.setBackgroundColor(colorCode);
                                                    }
                                                });
                                            }
                                    }
                                }).start();

                            } else {
                                imageViewAlbumArt.setImageResource(R.drawable.album_icon);
                            }
                        } catch (Exception e) {
                            imageViewAlbumArt.setImageResource(R.drawable.album_icon);
                        }
                        if (isShuffleOn) {
                            imageViewShuffle.setImageResource(R.drawable.ic_action_shuffle_on);
                        }
                    } catch (Exception e) {
                        Log.e("RECEIVER BOTTOM DETAILS", e.toString());
                    }
                } else if ( action != null && action.equalsIgnoreCase(Constants.ACTIONS.SONG_COMPLETED)) {
                    pbBottom.setProgress((int) currentSongDuration);
                    //    bottomAction.setImageResource(R.drawable.ic_play);
                    playPauseBottom.setImageResource(R.drawable.ic_play);
                }
            } catch (Exception e) {
                Log.e("ERROR_UUMControls(BR)", e.toString());
            }
        }
    };


    //REGISTERING SET BOTTOM DETAILS RECEIVER
    public void register_UpdateBottomControls() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        registerReceiver(UpdateBottomControls, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // getSharedPreferences();
        Log.i("Main Activity", "Started");
    }

    @Override
    protected void onPause() {
        Log.i("Main Activity", "Paused");
        setSharedPreferences();
        super.onPause();
    }


    @Override
    protected void onResume() {

        Log.i("Main Activity", "Resumed");
        getSharedPreferences();
        super.onResume();
    }


    @Override
    protected void onRestart() {
        Log.i("Main Activity", "Restarted");
        //   getSharedPreferences();
        super.onRestart();
    }


    @Override
    public void onBackPressed() {

        if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            moveTaskToBack(true);
        }
    }


    //SETTING THE SHARED PREFERENCES FOR THE MAIN ACTIVITY VIEW
    private void setSharedPreferences() {
        Preferences.setIsPlaying(sharedPreferences, isPlaying);
        Preferences.setIsSongCompleted(sharedPreferences, isSongCompleted);
        Preferences.setCurrentAlbumArt(sharedPreferences, currentAlbumArt);
        Preferences.setArtistName(sharedPreferences, currentArtistName);
        Preferences.setCurrentSongTitle(sharedPreferences, currentSongTitle);
        Preferences.setCurrentSongDuration(sharedPreferences, currentSongDuration);
        Preferences.setViewPagerPosition(sharedPreferences, viewPagerMain.getCurrentItem());
        Preferences.setLyricPosition(this, lyricPosition);
    }


    //GETTING THE SHARED PREFERENCES FOR THE MAIN ACTIVITY VIEW
    private void getSharedPreferences() {
        viewPagerPosition = sharedPreferences.getInt(Constants.STRINGS.VIEW_PAGER_POSITION, 0);
        currentArtistName = sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, "");
        currentAlbumArt = sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_ART, null);
        currentSongTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, "");
        currentSongDuration = sharedPreferences.getLong(Constants.STRINGS.CURRENT_SONG_DURATION, 0);
        currentSongPath = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, "");
        currentSongPosition = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION, 0);
        isPlaying = sharedPreferences.getBoolean(Constants.STRINGS.IS_PLAYING, false);
        isSongCompleted = sharedPreferences.getBoolean(Constants.STRINGS.IS_SONG_COMPLETED, false);
        lyricPosition = sharedPreferences.getInt(Constants.STRINGS.LYRIC_POSITION, 0);


        //CALLING THE BROADCAST RECEIVER TO UPDATE USER  MEDIA CONTROLLERS
        Intent intentSetBottomDetails = new Intent();
        intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        sendBroadcast(intentSetBottomDetails);
    }


    //On bottom action [ Play / Pause ] clicked
    public void onBottomActionClicked(View view) {

        vibrator.vibrate(50);
        checkService();
        Intent intent = new Intent();
        intent.setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS);
        sendBroadcast(intent);
    }


    //Setting the progress of song
    public void setSongProgress(long initial) {
        mtvTotalTimeBottom.setText(Tools.getSongTime(currentSongDuration));
        pbBottom.setMax((int) initial);

        if (bsExpanded){
            sbNowPlaying.setMax((int) initial);
        }
        handler.postDelayed(Update, 200);
        imageViewAlbumArt.animate().rotationBy(360).withEndAction(Update).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }


    private Runnable Update = new Runnable() {

        @Override
        public void run() {
            if ( playbackService != null && playbackService.isPlaying()) {
                try {
                    //Getting the mediaplayer position
                    long songPosition = playbackService.getCurrentPosition();
                    //Updating the current song time in bottom layout and now playing layout
                    mtvRunTimeBottom.setText(Tools.getSongTime(songPosition));

                    if (bsExpanded){
                        mtvRunTimeNp.setText(Tools.getSongTime(songPosition));
                    }
                    //Updating the current song time for seekbar
                    pbBottom.setProgress((int) songPosition);
                    if (bsExpanded){
                        sbNowPlaying.setProgress((int) songPosition);
                    }
                    try {

                        //If lyrics are lyricsFound and lyric position should be less than lyrics length
                        //from LyricsFragment
                        if (LyricsFragment.lyricsFound && (lyricPosition <= LyricsFragment.syncedLyrics.size())) {
                            if ( songPosition <= LyricsFragment.syncedTime.get(lyricPosition) ) {

                                //Checking for top textview to not set text for 1st lyric.
                                if (lyricPosition > 0) {
                                    LyricsFragment.textViewTop.setText(LyricsFragment.syncedLyrics.get(lyricPosition - 1));
                                }
                                LyricsFragment.textViewCenter.setText(LyricsFragment.syncedLyrics.get(lyricPosition));

                                //Checking for the last position when song has more time to play.
                                if (lyricPosition < LyricsFragment.syncedTime.size() - 1) {
                                    LyricsFragment.textViewBottom.setText(LyricsFragment.syncedLyrics.get(lyricPosition + 1));
                                }

                            } else {

                                if (lyricPosition < LyricsFragment.syncedLyrics.size() - 1) {
                                    lyricPosition++;
                                    LyricsFragment.textViewTop.setText(LyricsFragment.syncedLyrics.get(lyricPosition - 1));
                                    LyricsFragment.textViewCenter.setText(LyricsFragment.syncedLyrics.get(lyricPosition));
                                }
                                //Checking for bottom textview to not set text for last lyric.
                                if (lyricPosition < LyricsFragment.syncedLyrics.size() - 2) {
                                    LyricsFragment.textViewBottom.setText(LyricsFragment.syncedLyrics.get(lyricPosition + 1));
                                } else {
                                    //If it is last lyric set bottom text empty.
                                    LyricsFragment.textViewBottom.setText("");
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



    //ON CLICKED MAIN MENU ITEMS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.main_menu_item_1:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                startActivity(intentSearch);
                overridePendingTransition(R.anim.right_to_left, R.anim.no_action);
                break;

            case R.id.main_menu_item_2:
                Intent intentFeedback = new Intent(this, FeedbackActivity.class);
                startActivity(intentFeedback);
                overridePendingTransition(R.anim.right_to_left, R.anim.no_action);
                break;

            case R.id.main_menu_item_3:

            case R.id.main_menu_item_4:
                break;

            case R.id.main_menu_item_5:
                Intent intentSettings = new Intent(this, SettingsActivity.class);
                startActivity(intentSettings);
                overridePendingTransition(R.anim.right_to_left, R.anim.no_action);
                break;
        }
        return true;
    }


    @Override
    protected void onDestroy() {

        Log.e("Main Activity", "Destroyed Called");
        try {
            isPlaying = false;
            unregisterReceiver(UpdateBottomControls);
            try {
                if (StartService.serviceConnectionPlaybackService != null) {
                    unbindService(StartService.serviceConnectionPlaybackService);
                }
            } catch (Exception e) {
                Log.e(TAG + "OnDestroy()", e.toString());
            }
            setSharedPreferences();
            playbackService.setSharedPreferences();
        } catch (Exception e) {
            Log.e("ERROR_MA_OnDestroy", e.toString());
        }

        try{
            unregisterReceiver(ChangeBackground);
            unregisterReceiver(UpdateBottomControls);
        } catch (IllegalArgumentException e) {
            // Need to check if broadcast receivers are registered or not.
            // Then we don't need this try-catch block.
        }
        super.onDestroy();
    }


//Now playing content.

    public void setNowPlayingContent() {

        register_UpdateControls();

        mtvRunTimeNp.setCharacterLists(TickerUtils.provideNumberList());
        mtvTotalTimeNp.setCharacterLists(TickerUtils.provideNumberList());

        sbNowPlaying.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int seekBarPosition = seekBar.getProgress();

                if (PlaybackService.mediaPlayer != null) {

                    PlaybackService.mediaPlayer.seekTo(seekBarPosition);
                    if (!PlaybackService.mediaPlayer.isPlaying()) {
                        Preferences.setCurrentSongPosition(sharedPreferences, seekBarPosition);
                        sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS));
                    }
                } else {
                    Preferences.setCurrentSongPosition(sharedPreferences, seekBarPosition);
                    sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS));
                }
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
/*
                updateLyricPositionNowPlaying(seekBarPosition);
*/

            }
        });

        fetchPlaylist = new MainActivity.FetchPlaylist();
        fetchPlaylist.execute();

        sendBroadcast(new Intent().setAction(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));

        ivPlayPauseNp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ON BOTTOM ACTIONS [ PLAY / PAUSE ] CLICKED
                checkServiceNowPlaying();
                Intent intent = new Intent();
                intent.setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS);
                sendBroadcast(intent);

                if (isPlaying) {
                    ivPlayPauseNp.setImageResource(R.drawable.icon_play);
                    isPlaying = false;
                } else {
                    ivPlayPauseNp.setImageResource(R.drawable.ic_action_pause);
                    isPlaying = true;
                }
                Preferences.setIsPlaying(sharedPreferences, isPlaying);
                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
            }
        });


        //PLAYING THE PREVIOUS TRACKS IN THE CURRENT PLAYLIST DATABASE
        ivPreviousTrackNp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(MainActivity.this, MainActivity.lyricPosition);

                DatabaseManager.playTrackAtPosition(MainActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist - 1));
                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(MainActivity.this) - 1);
                    }
                });
            }
        });


        //PLAYING THE NEXT TRACKS IN THE CURRENT PLAYLIST DATABASE
        ivNextTrackNp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(MainActivity.this, MainActivity.lyricPosition);

                DatabaseManager.playTrackAtPosition(MainActivity.this,
                        (PlaybackService.currentSongPositionFromPlaylist + 1));
                sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));

                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.smoothScrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(MainActivity.this) + 6);
                    }
                });
            }
        });


        ivShuffleNp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            final int size = songs.size();
            int random;

            if (size > 0) {

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
                Preferences.setLyricPosition(MainActivity.this, 0);
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


        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);

                if (dy < 0) {
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


            if ( intent.getAction() != null && intent.getAction().equals(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS)) {

                updateUserInterface();
            }
        }
    };

    private void updateUserInterface() {

        isPlaying = Preferences.getIsPlaying(sharedPreferences);
        currentPosition = Preferences.getCurrentSongPosition(sharedPreferences);
        currentDuration = Preferences.getCurrentSongDuration(sharedPreferences);

        int position = Preferences.getCurrentSongPositionFromPlaylist(this);

        sbNowPlaying.setMax((int) currentDuration);
        mtvRunTimeNp.setText(Tools.getSongTime(currentPosition) + "");
        mtvTotalTimeNp.setText(Tools.getSongTime(currentDuration) + "");
        mtvSongTitleNp.setText(Preferences.getCurrentSongTitle(sharedPreferences));
        mtvArtistNameNp.setText(Preferences.getArtistName(sharedPreferences));
        //mtvSongDuration.setText("");

        vpNowPlaying.setCurrentItem(position-1);

        if (isPlaying) {
            ivPlayPauseNp.setImageResource(R.drawable.ic_action_pause);
        } else {
            ivPlayPauseNp.setImageResource(R.drawable.icon_play);
        }


        if (PlaybackService.mediaPlayer != null) {

            if (PlaybackService.mediaPlayer.isPlaying()) {
                sbNowPlaying.setProgress(PlaybackService.mediaPlayer.getCurrentPosition());
                updateSeekBar();
            } else {
                sbNowPlaying.setProgress(currentPosition);
            }
        } else {
            sbNowPlaying.setProgress(currentPosition);
        }
    }


    private void updateSeekBar() {
        handler.postDelayed(UpdateNowPlaying, 200);
    }


    private Runnable UpdateNowPlaying = new Runnable() {

        @Override
        public void run() {

            if (isPlaying && PlaybackService.mediaPlayer != null) {
                try {

                    //Getting the mediaplayer position
                    long position = PlaybackService.mediaPlayer.getCurrentPosition();
                    //Updating the current song time in bottom extra layout
                    mtvRunTimeNp.setText(Tools.getSongTime(position));
                    //Updating the current song time for seekbar
                    sbNowPlaying.setProgress((int) position);

                } catch (Exception e) {
                    Log.e("Exception Handler", e.toString());
                }
            }
        }
    };


    private void updateLyricPositionNowPlaying(int seekBarPosition) {

        if (LyricsFragment.lyricsFound) {

            if (seekBarPosition == 0) {
                MainActivity.lyricPosition = 0;
                Preferences.setLyricPosition(this, MainActivity.lyricPosition);
                LyricsFragment.textViewTop.setText("");
                return;
            }


            int i = 0;
            for (long time : LyricsFragment.syncedTime) {

                if (time >= seekBarPosition) {
                    MainActivity.lyricPosition = i;
                    if (MainActivity.lyricPosition == 0) {
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
    public void checkServiceNowPlaying() {

        if (PlaybackService.mediaPlayer != null) {
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

            SQLiteDatabase sqLiteDatabase = openOrCreateDatabase("LyricalMusic", MODE_PRIVATE, null);

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
                player.music.adapters.ViewPagerAdapter.songs = songs;
                player.music.adapters.ViewPagerAdapter viewPagerAdapter = new
                        player.music.adapters.ViewPagerAdapter(MainActivity.this);
                vpNowPlaying.setAdapter(viewPagerAdapter);

                vpNowPlaying.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

            } catch (Exception e){
                Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }


            nowPlayingAdapter = new NowPlayingAdapter(MainActivity.this);
            NowPlayingAdapter.songs = songs;
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            recyclerView.setAdapter(nowPlayingAdapter);


            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL);


            dividerItemDecoration.setDrawable( ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.divider_small) );
            recyclerView.addItemDecoration(dividerItemDecoration);

            recyclerView.scrollToPosition(Preferences.getCurrentSongPositionFromPlaylist(MainActivity.this) - 1);

            if (songs != null && !songs.isEmpty()) {

                int count = songs.size();
                String track = "Tracks";
                if ( count == 1 ) {
                    track = "Track";
                }
                utvTrackCountNp.setText(count + "");
                utvTrackNp.setText(track);
            }
        }

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

        vpNowPlaying.setCurrentItem(position, true);

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


        vpNowPlaying.setCurrentItem(position, true);

    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }

    @Override
    public void recyclerViewOverflowClicked(View view, int position) {

    }


   /* @Override
    public void onBackPressed() {
        super.onBackPressed();

        handler.removeCallbacks(Update);
        unregisterReceiver(UpdateControls);
        finish();
        overridePendingTransition(R.anim.no_action, R.anim.up_to_down);
    }*/



    private void initCollapsingToolbar() {

        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);


        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if ( scrollRange == -1 ) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if ( scrollRange + verticalOffset == 0 ) {
                    isShow = true;
                } else if (isShow) {
                    isShow = false;
                }
            }
        });
    }

}




