package player.music.activities;


import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.List;

import player.music.Constants;
import player.music.R;
import player.music.adapters.ArtistAlbumAdapter;
import player.music.adapters.ArtistSongsAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Preferences;
import player.music.extras.Tools;
import player.music.inerface.ArtistDetailsAlbumClick;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.managers.DatabaseManager;
import player.music.objects.Album;
import player.music.objects.Song;

public class ArtistDetailsActivity extends AppCompatActivity implements RecyclerViewOnItemClickListener,
                            ArtistDetailsAlbumClick {

    private CardView cvFabBack;
    private Bundle bundle;
    private String artistName;
    private long artistId;
    private UbuntuTextView mtvArtistStartLetters, mtvArtistName,
            mtvNumberOfAlbums, mtvNumberOfTracks, mtvArtistDuration;
    private UbuntuTextView tvTitle;


    private long totalSongsDuration = 0;

    private List<Album> albums, albumsWithDuplicate;
    private List<Song> songs;
    private RecyclerView recyclerViewAlbums, recyclerViewSongs;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Local variables declaration
        int numberOfAlbums, numberOfTracks;


        //Setting the layout
        setContentView(R.layout.artist_details_layout);

        //Initializing views in layout
        cvFabBack = findViewById(R.id.cv_fab_back);
        tvTitle = findViewById(R.id.utv_title);
        mtvArtistStartLetters = findViewById(R.id.mtv_artist_start_letters);
        mtvArtistName = findViewById(R.id.utv_artist_name);
        mtvNumberOfAlbums = findViewById(R.id.mtv_albums_count);
        mtvArtistDuration = findViewById(R.id.artist_duration_artist_detail_row);
        recyclerViewAlbums = findViewById(R.id.rv_artist_albums);
        recyclerViewSongs = findViewById(R.id.rv_artist_tracks);
        mtvNumberOfTracks = findViewById(R.id.utv_track_count);
        sharedPreferences = getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);


        //Setting toolbar to activity
       /* toolbar.setTitle("Artist Details");
        toolbar.setNavigationIcon(R.drawable.left_arrow);
        setSupportActionBar(toolbar);*/

        //Getting extras from bundle
        bundle = getIntent().getExtras();
        artistName = bundle.getString("artistName", "");
        artistId = bundle.getLong("artistId", 0);
        numberOfAlbums = bundle.getInt("artistNumberOfAlbums", 0);
        numberOfTracks = bundle.getInt("artistNumberOfTracks", 0);

        tvTitle.setText(artistName);


        //Setting the artist details at the top
        String tempString1, tempString2;
        if (numberOfAlbums == 1) {
            tempString1 = numberOfAlbums + " Album";
        } else {
            tempString1 = numberOfAlbums + " Albums";
        }
        if (numberOfTracks == 1) {
            tempString2 = numberOfTracks + " Track";
        } else {
            tempString2 = numberOfTracks + " Tracks";
        }
        try {
            mtvArtistStartLetters.setText(artistName.substring(0, 2));
        } catch (Exception e) {
            Log.e("ArtistDetailsActivity", e.toString());
        }
        mtvArtistName.setText(artistName);
        mtvNumberOfAlbums.setText(tempString1);
        mtvNumberOfTracks.setText(tempString2);



        new SetAlbumsAndSongs().execute();


        cvFabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    //Setting albums and songs to the UI through adapters.
    private class SetAlbumsAndSongs extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            Cursor cursor;

            String[] projectionSongs = {MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA, MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM};

            String[] projectionAlbums = {MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.LAST_YEAR, MediaStore.Audio.Albums.NUMBER_OF_SONGS};

            try {

                cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projectionSongs, MediaStore.Audio.Media.ARTIST_ID + " = " + artistId,
                        null, null);

                if (cursor != null && cursor.moveToFirst()) {

                    songs = new ArrayList<>();
                    albums = new ArrayList<>();

                    do {

                        Song song = new Song();
                        Album album = new Album();

                        song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                        song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                        song.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                        song.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                        song.setAlbumId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                        song.setAlbumTitle( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) );
                        song.setArtistName( artistName );

                        album.setAlbumTitle( cursor.getString(cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.ALBUM)) );
                        album.setAlbumId(cursor.getInt(cursor.getColumnIndexOrThrow(
                                MediaStore.Audio.Media.ALBUM_ID)));


                        Cursor cursorAlbums = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                                projectionAlbums, MediaStore.Audio.Albums._ID + " = " +
                                        album.getAlbumId(),null,null);

                        if (cursorAlbums != null && cursorAlbums.moveToFirst()) {

                            album.setAlbumArt( cursorAlbums.getString(cursorAlbums.getColumnIndexOrThrow(
                                    MediaStore.Audio.Albums.ALBUM_ART)));
                            album.setYear( cursorAlbums.getInt( cursorAlbums.getColumnIndexOrThrow(
                                    MediaStore.Audio.Albums.LAST_YEAR)));
                            album.setNumberOfTracks(cursorAlbums.getInt( cursorAlbums.getColumnIndexOrThrow(
                                    MediaStore.Audio.Albums.NUMBER_OF_SONGS)));


                            song.setAlbumArt(album.getAlbumArt());
                            cursorAlbums.close();
                        }

                        albums.add(album);
                        songs.add(song);
                        totalSongsDuration += song.getDuration();

                    } while (cursor.moveToNext());
                    cursor.close();

                    if ( albumsWithDuplicate != null ){

                        albumsWithDuplicate.clear();
                    }
                    albumsWithDuplicate = albums;
                }

            } catch (Exception e) {
                Log.e("New", e.toString());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mtvArtistDuration.setText(Tools.getSongTime(totalSongsDuration));


            List<Album> albumsNew = new ArrayList<>();
            int length = albums.size(), i = 0;


            do{

                Album album = albums.get(i);

                int count = 0;
                for (Album album1 : albumsNew) {

                    if (album.getAlbumId() == album1.getAlbumId())
                    {
                        count = 1;
                    }
                }

                if (count == 0) {
                    albumsNew.add(album);
                }

                i++;
            } while ( i < length );


            albums.clear();
            albums.addAll(albumsNew);


            //For setting albums to the ui
            LinearLayoutManager layoutManager = new LinearLayoutManager(ArtistDetailsActivity.this);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerViewAlbums.setLayoutManager( layoutManager );
            ArtistAlbumAdapter artistAlbumAdapter = new ArtistAlbumAdapter(albums);
            recyclerViewAlbums.setAdapter(artistAlbumAdapter);
            ArtistAlbumAdapter.artistDetailsAlbumClick = ArtistDetailsActivity.this;


            DividerItemDecoration didAlbums = new DividerItemDecoration( recyclerViewAlbums.getContext(),
                    layoutManager.getOrientation());
            didAlbums.setOrientation(DividerItemDecoration.HORIZONTAL);
            didAlbums.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.divider_small));
            recyclerViewAlbums.addItemDecoration(didAlbums);



            //For setting songs to the ui
            ArtistSongsAdapter adapterSongs = new ArtistSongsAdapter(songs,
                    ArtistDetailsActivity.this);
            recyclerViewSongs.setLayoutManager(new LinearLayoutManager(ArtistDetailsActivity.this));
            recyclerViewSongs.setAdapter(adapterSongs);


            DividerItemDecoration didSongs = new DividerItemDecoration( recyclerViewSongs.getContext(),
                    DividerItemDecoration.VERTICAL);
            didSongs.setDrawable(ContextCompat.getDrawable( getApplicationContext(), R.drawable.divider_small));
            recyclerViewSongs.addItemDecoration(didSongs);

        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    public void recyclerViewItemClicked(View view, int position, long id) {

    }



    @Override
    public void recyclerViewItemClicked(View view, int position) {

        try {

            //Setting the bottom action bar song details using shared preferences
            Preferences.setSongPath(sharedPreferences, songs.get(position).getPath());
            Preferences.setArtistName(sharedPreferences, songs.get(position).getArtistName());
            Preferences.setCurrentAlbumArt(sharedPreferences, songs.get(position).getAlbumArt() );
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


            //Sending receiver to set bottom details
            Intent intentSetBottomDetails = new Intent();
            intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
            sendBroadcast(intentSetBottomDetails);


            //Updating the playlist data to the database
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    DatabaseManager.updateCurrentPlaylistTable();
                    DatabaseManager.insertCurrentPlaylist( songs);
                }
            });
            thread.start();

        }catch (Exception e) {
            Log.e("SongsFragment_RCV - ", e.toString());
        }
    }



    @Override
    public void recyclerViewOverflowClicked(View view, int pos) {

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());

        popupMenu.show();

        final int position = pos - 2;

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                int menuId = item.getItemId();

                switch (menuId){

                    case R.id.song_menu_item_1:
                        Intent intentAlbumDetails = new Intent( ArtistDetailsActivity.this, AlbumDetailsActivity.class);
                        intentAlbumDetails.putExtra("albumId", albumsWithDuplicate.get(position).getAlbumId());
                        intentAlbumDetails.putExtra("albumTitle", albumsWithDuplicate.get(position).getAlbumTitle());
                        intentAlbumDetails.putExtra("albumArt", albumsWithDuplicate.get(position).getAlbumArt());
                        intentAlbumDetails.putExtra("numberOfTracks", albumsWithDuplicate.get(position).getNumberOfTracks());
                        startActivity(intentAlbumDetails);

                        overridePendingTransition(R.anim.right_to_left, R.anim.no_action);

                        break;


                }
                return false;
            }
        });
    }



    @Override
    public void onAlbumClick(View view, int position) {

        Intent intentAlbumDetails = new Intent( this, AlbumDetailsActivity.class);
        intentAlbumDetails.putExtra("albumId", albums.get(position).getAlbumId());
        intentAlbumDetails.putExtra("albumTitle", albums.get(position).getAlbumTitle());
        intentAlbumDetails.putExtra("albumArt", albums.get(position).getAlbumArt());
        intentAlbumDetails.putExtra("numberOfTracks", albums.get(position).getNumberOfTracks());


        Pair[] pairs = new Pair[4];

        ImageView tempAlbum = view.findViewById(R.id.iv_album_art);
        ImageView tempOverflow = view.findViewById(R.id.iv_overflow_menu);
        UbuntuTextView temptitle = view.findViewById(R.id.mtv_album_title);
        UbuntuTextView tempTracks = view.findViewById(R.id.mtv_no_of_tracks);

        pairs[0] = new Pair<View, String>(tempAlbum, getResources().getString(R.string.album_thumbnail));
        pairs[1] = new Pair<View, String>(tempOverflow, getResources().getString(R.string.album_overflow));
        pairs[2] = new Pair<View, String>(temptitle, getResources().getString(R.string.album_title));
        pairs[3] = new Pair<View, String>(tempTracks, getResources().getString(R.string.number_of_tracks));

        ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(this, pairs);
        startActivity(intentAlbumDetails, activityOptions.toBundle());


        overridePendingTransition(R.anim.right_to_left, R.anim.no_action);
    }



    @Override
    public void onAlbumMenuClick(View view, final int position) {

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());

        popupMenu.show();


    }



}
