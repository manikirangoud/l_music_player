package player.music.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;


import java.util.ArrayList;
import java.util.List;

import player.music.Constants;
import player.music.R;
import player.music.adapters.AlbumSongsAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Tools;
import player.music.extras.Preferences;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.managers.DatabaseManager;
import player.music.objects.Song;


public class AlbumDetailsActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener {


    private Toolbar toolbar;
    private String albumName, albumArt;
    private int albumYear = 0, numberOfTracks;
    private long albumId = 0, totalSongsDuration = 0;
    private Bundle bundle;
    private UbuntuTextView textViewAlbumName, textViewAlbumYear, textViewAlbumDuration,
            textViewNumberOfTracks;

    private UbuntuTextView mtvAlbumTitleCollapsed;

    private ImageView imageViewAlbumArt, imageViewActionBack;

    private String[] projection = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media._ID, MediaStore.Audio.Artists._ID};


    private List<Song> songs = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlbumSongsAdapter albumSongsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.album_details_layout);
        toolbar = findViewById(R.id.toolbar);
        textViewAlbumName = findViewById(R.id.text_view_album_name_album_detail);
        imageViewAlbumArt = findViewById(R.id.image_view_album_album_detail);
        recyclerView = findViewById(R.id.recycler_view_album_details);
        textViewAlbumYear = findViewById(R.id.text_view_album_year_album_detail);
        textViewAlbumDuration = findViewById(R.id.album_duration_album_detail_row);
        textViewNumberOfTracks = findViewById(R.id.utv_track_count);
        imageViewActionBack = findViewById(R.id.iv_action_back);
        mtvAlbumTitleCollapsed = findViewById( R.id.mtv_album_title_collapsed );


        //GETTING THE VALUES FROM INTENT
        bundle = getIntent().getExtras();
        albumName = bundle.getString("albumTitle", "");
        albumId = bundle.getLong("albumId", 0);
        albumArt = bundle.getString("albumArt", "");
        numberOfTracks = bundle.getInt("numberOfTracks", 0);

        initCollapsingToolbar();

        //SETTING UP THE TOOLBAR
    /*    toolbar.setTitle("Album Details");
        toolbar.setNavigationIcon(R.drawable.ic_action_left);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTextPrimary));
        toolbar.setTitleTextAppearance(this, R.style.MyToolbarTextView);
        */
        setSupportActionBar(toolbar);


        //SETTING SONGS DETAILS
        setSongs();

        //SETTING ALBUM DETAILS
        setAlbums();




        //SETTING THE ON CLICK LISTENER TO NAVIGATION ICON
        imageViewActionBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    private void setAlbums() {

        try {
            textViewAlbumName.setText(albumName);
            textViewAlbumDuration.setText(Tools.getSongTime(totalSongsDuration));
            imageViewAlbumArt.setImageURI(Uri.parse(albumArt));

            if ( numberOfTracks == 1 ) {
                textViewNumberOfTracks.setText( numberOfTracks + " " +
                        getResources().getString(R.string.track) );
            } else {
                textViewNumberOfTracks.setText( numberOfTracks + " " +
                        getResources().getString(R.string.tracks) );
            }
        } catch (Exception e) {
            Log.e("AlbumDetailsActivity-:", e.toString());
        }

    }

    private void setSongs() {

        Cursor cursor;

        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                MediaStore.Audio.Media.ALBUM_ID + " = " + albumId, null,
                "LOWER(" + MediaStore.Audio.Media.TITLE + ")ASC");

        if (cursor != null && cursor.moveToFirst()) {

            do {

                Song song = new Song();

                song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                song.setAlbumArt(albumArt);
                song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setAlbumId(albumId);
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                song.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)));
                song.setArtistId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)));
                song.setAlbumTitle(albumName);

                totalSongsDuration += song.getDuration();
                songs.add(song);

            } while ( cursor.moveToNext() );
            cursor.close();

            albumYear = songs.get(0).getYear();
            if(albumYear == 0) {
                textViewAlbumYear.setText(getResources().getString(R.string.year_not_available));
            } else {
                textViewAlbumYear.setText(albumYear + "");
            }
            textViewAlbumDuration.setText( totalSongsDuration + "");

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            albumSongsAdapter = new AlbumSongsAdapter(songs, this);
            recyclerView.setAdapter(albumSongsAdapter);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                    DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable( this, R.drawable.divider_small));
            recyclerView.addItemDecoration(dividerItemDecoration);

        }
    }

    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void recyclerViewItemClicked(View view, final int position, long itemId) {

        try {

            final int length = songs.size();

            final SharedPreferences sharedPreferences = getSharedPreferences(
                    Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);


            Preferences.setSongPath(sharedPreferences, songs.get(position).getPath() );
            Preferences.setIsPlaying(sharedPreferences, true );
            Preferences.setIsSongCompleted(sharedPreferences, false );
            Preferences.setArtistName(sharedPreferences, songs.get(position).getArtistName() );
            Preferences.setCurrentAlbumArt(sharedPreferences, albumArt );
            Preferences.setCurrentSongTitle(sharedPreferences, songs.get(position).getTitle() );
            Preferences.setCurrentSongDuration(sharedPreferences, songs.get(position).getDuration() );
            Preferences.setCurrentPlaylistSize(sharedPreferences, length) ;
            Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, ( position+1 ) );
            Preferences.setCurrentAlbumTitle(sharedPreferences, songs.get(position).getAlbumTitle() );
            Preferences.setCurrentSongId(sharedPreferences, songs.get(position).getId() );

 //           MainActivity.lyricPosition = 0;
 //           Preferences.setLyricPosition(this, MainActivity.lyricPosition);


            //SENDING RECEIVER TO PLAY A SPECIFIC SONG
            Intent intentSetMedia = new Intent();
            intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
            sendBroadcast(intentSetMedia);


            //Sending broadcast receiver to start lyrics
            sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));


            //SETTING THE BOTTOM ACTIONS BAR SONG DETAILS USING SHARED PREFERENCES
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {

                    DatabaseManager.updateCurrentPlaylistTable();
                    DatabaseManager.insertCurrentPlaylist(songs);

                }
            });
            thread.start();

        }catch (Exception e) {
            Log.e("AlbumDetail_OnClick", e.toString());
        }

    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }

    @Override
    public void recyclerViewOverflowClicked(View view, int position) {

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());

        popupMenu.show();
    }




    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        final Animation animFadeIn = AnimationUtils.loadAnimation(AlbumDetailsActivity.this, R.anim.fade_in);
        final Animation animFadeOut = AnimationUtils.loadAnimation(AlbumDetailsActivity.this, R.anim.fade_out);

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
                    toolbar.setVisibility(View.VISIBLE);
                    toolbar.setAnimation( animFadeIn );
                    mtvAlbumTitleCollapsed.setText( albumName );
                    isShow = true;
                } else if (isShow) {
                    toolbar.setAnimation( animFadeOut );
                    toolbar.setVisibility(View.INVISIBLE);
                    isShow = false;
                }
            }
        });
    }

}

