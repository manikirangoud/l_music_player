package player.music.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import player.music.Constants;
import player.music.R;
import player.music.adapters.TracksAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.managers.DatabaseManager;
import player.music.extras.Preferences;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Song;


public class TracksFragment extends Fragment implements RecyclerViewOnItemClickListener{

    private static final String TAG = "SONGS_FRAGMENT";

    private ProgressBar progressBar;


    public static HashMap<Long, String> albumsArt = new HashMap<>();

    private RecyclerView recyclerView;
    private TracksAdapter tracksAdapter;
    private SharedPreferences sharedPreferences;
    private ImageView imageViewShuffle;
    private UbuntuTextView utvTrackCount;
    private UbuntuTextView utvTrack;


    private List<Song> songs = new ArrayList<>();


    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Declaring local variables
        View view;



        //Initializing variables
        view = inflater.inflate(R.layout.songs_fragment_layout, container, false);
        recyclerView = view.findViewById(R.id.rv_songs);
        progressBar = view.findViewById(R.id.progress_bar_songs);
        imageViewShuffle = view.findViewById(R.id.iv_shuffle);
        utvTrackCount = view.findViewById(R.id.utv_track_count);
        utvTrack = view.findViewById(R.id.utv_track);
        recyclerViewOnItemClickListener = this;


        sharedPreferences = view.getContext().getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);



        new FetchSongs().execute();



            /*
        //UPDATING THE LYRICS
       //     DatabaseManager.updateLyricsTable(getActivity());
        //    DatabaseManager.insertLyrics(songsId, songsTitle);

        }catch (Exception e) {
            Log.e("SONGS FRAGMENT : ", e.toString());
        }
        */



            imageViewShuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int size = songs.size();
                    int random;

                    random = new Random().nextInt(size);
                    Song song = songs.get(random);

                    Preferences.setSongPath(sharedPreferences, song.getPath());
                    Preferences.setArtistName(sharedPreferences, song.getArtistName());
                    Preferences.setCurrentAlbumArt(sharedPreferences, albumsArt.get(song.getAlbumId()));
                    Preferences.setCurrentSongTitle(sharedPreferences, song.getTitle());
                    Preferences.setCurrentSongDuration(sharedPreferences, song.getDuration());
                    Preferences.setIsPlaying(sharedPreferences, true);
                    Preferences.setIsSongCompleted(sharedPreferences, false);
                    Preferences.setCurrentPlaylistSize(sharedPreferences, size);
                    Preferences.setCurrentSongId(sharedPreferences, song.getId());
                    Preferences.setCurrentAlbumTitle(sharedPreferences, song.getAlbumTitle());
                    Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, 1);
                    Preferences.setLyricPosition(getContext(), 0);
                    Preferences.setShuffle(sharedPreferences, true);


                    //SENDING RECEIVER TO PLAY A SPECIFIC SONG
                    Intent intentSetMedia = new Intent();
                    intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
                    getActivity().sendBroadcast(intentSetMedia);


                    //Sending receiver to set bottom details
                    Intent intentSetBottomDetails = new Intent();
                    intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
                    getActivity().sendBroadcast(intentSetBottomDetails);


                    final List<Song> shuffledSongs = new ArrayList<>();

                    shuffledSongs.add( song );


                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            int random;

                            do {

                                boolean found = false;

                                random = new Random().nextInt(size);
                                Song song = songs.get(random);

                                for (Song check : shuffledSongs) {

                                    if ( check.getId() == song.getId() ) {

                                        found = true;
                                    }
                                }

                                if ( !found ) {
                                    shuffledSongs.add(song);
                                }

                            } while ( shuffledSongs.size() < size );


                            //Updating the playlist data to the database
                            Thread threadUpdateDatabase = new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    DatabaseManager.updateCurrentPlaylistTable();
                                    DatabaseManager.insertCurrentPlaylist( shuffledSongs);
                                }
                            });
                            threadUpdateDatabase.start();
                        }
                    });
                    thread.start();

                }
            });

        return view;
    }


    private class FetchSongs extends AsyncTask<Void, Void, Void>{

        String[] projection1 = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST_ID},
                projection2 = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART};
        Cursor cursor, cursor2;

        @Override
        protected Void doInBackground(Void... voids) {

            try {

                //Getting the album arts of all albums
                cursor2 = getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                        projection2, null, null,
                        "LOWER(" + MediaStore.Audio.Albums._ID + ")ASC");

                if (cursor2 != null && cursor2.moveToFirst()) {

                    do {
                        albumsArt.put(cursor2.getLong(cursor2.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                                cursor2.getString(cursor2.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)));

                    } while (cursor2.moveToNext());
                    cursor2.close();
                }

                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        projection1, null, null,
                        "LOWER(" + MediaStore.Audio.Media.TITLE + ")ASC");

                if (cursor != null && cursor.moveToFirst()) {

                    do {

                        Song song = new Song();

                        song.setTitle( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                        song.setAlbumTitle( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                        song.setArtistName( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                        song.setAlbumId( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                        song.setPath( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                        song.setDuration( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                        song.setId( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                        song.setArtistId( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));

                        song.setAlbumArt(albumsArt.get( song.getAlbumId() ));

                        songs.add(song);

                    } while (cursor.moveToNext());

                    cursor.close();
                }
            } catch (Exception e) {

                Log.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                //Setting the recycler view to the fragment layout
                TracksAdapter.songs = songs;
                tracksAdapter = new TracksAdapter(/*songs, */ recyclerViewOnItemClickListener);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(tracksAdapter);
                progressBar.setVisibility(View.GONE);

                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                        DividerItemDecoration.VERTICAL);
                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_small));
                recyclerView.addItemDecoration(dividerItemDecoration);

                int count = TracksAdapter.songs.size();
                String track = "Tracks";
                if ( count == 1 ) {
                    track = "Track";
                }
                utvTrackCount.setText(count + "");
                utvTrack.setText(track);


                register_Search();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void recyclerViewItemClicked(View view, int position, long songId) {


        final List<Song> playlist = new ArrayList<>();


        try {

            for ( Song song : songs ) {

                if ( song.getId() == songId ) {
                    //Setting the bottom action bar song details using shared preferences
                    Preferences.setSongPath(sharedPreferences, song.getPath());
                    Preferences.setArtistName(sharedPreferences, song.getArtistName());
                    Preferences.setCurrentAlbumArt(sharedPreferences, albumsArt.get(song.getAlbumId()));
                    Preferences.setCurrentSongTitle(sharedPreferences, song.getTitle());
                    Preferences.setCurrentSongDuration(sharedPreferences, song.getDuration());
                    Preferences.setIsPlaying(sharedPreferences, true);
                    Preferences.setIsSongCompleted(sharedPreferences, false);
                    Preferences.setCurrentPlaylistSize(sharedPreferences, 1);
                    Preferences.setCurrentSongId(sharedPreferences, song.getId());
                    Preferences.setCurrentAlbumTitle(sharedPreferences, song.getAlbumTitle());
                    Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, 0);
                    Preferences.setLyricPosition(view.getContext(), 0);

                    //Adding current clicked song to playlist
                    playlist.add(song);

                    break;
                }
            }

            //SENDING RECEIVER TO PLAY A SPECIFIC SONG
            Intent intentSetMedia = new Intent();
            intentSetMedia.setAction(Constants.ACTIONS.SET_MEDIA_PATH);

            if (getActivity() != null){
                getActivity().sendBroadcast(intentSetMedia);
            }


            //Sending receiver to set bottom details
            Intent intentSetBottomDetails = new Intent();
            intentSetBottomDetails.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
            getActivity().sendBroadcast(intentSetBottomDetails);


           //Updating the playlist data to the database
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {

                   DatabaseManager.updateCurrentPlaylistTable();
                    DatabaseManager.insertCurrentPlaylist(playlist);

                }
            });
            thread.start();


        }catch (Exception e) {
            Log.e("SongsFragment_RCV - ", e.toString());
        }
    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {



    }



    private void register_Search() {

        if (getActivity() != null) {
            getActivity().registerReceiver(SearchItem, new IntentFilter(Constants.ACTIONS.SEARCH));
        }
    }


    private BroadcastReceiver SearchItem = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTIONS.SEARCH)) {

                String target = intent.getStringExtra("target");

                try {

                    List<Song> filteredSongs = new ArrayList<>();
                    for (Song song : songs) {

                        if (song.getTitle().toLowerCase().contains(target.toLowerCase())) {

                            filteredSongs.add(song);
                        }
                    }

                    if (tracksAdapter != null) {

                        TracksAdapter.songs = filteredSongs;
                        if (recyclerView.getAdapter() != null){
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }

                        int count = TracksAdapter.songs.size();
                        String track = "Tracks";
                        if ( count == 1 ) {
                            track = "Track";
                        }
                        utvTrackCount.setText(count + "");
                        utvTrack.setText(track);


                        // songsAdapter.onFilteredSongs(filteredSongs);
                    }
                } catch (Exception e) {
                    Log.e("ERR_Search_songs", e.toString());
                }

            }

        }
    };



    @Override
    public void recyclerViewOverflowClicked(View view, final int position) {

        final Context context = new ContextThemeWrapper( getActivity(), R.style.PopupMenuStyle);

        PopupMenu popupMenu = new PopupMenu( context, view );
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                switch (id) {
                    case R.id.song_menu_item_1:

                        break;

                    case R.id.song_menu_item_2:

                        break;

                    case R.id.song_menu_item_3:
                        break;

                    case R.id.song_menu_item_4:


                    case R.id.song_menu_item_5:
                        DatabaseManager.addSongToPlaylist(context, songs.get(position), sharedPreferences);
                        break;

                    case R.id.song_menu_item_6:

                        break;

                }
                return true;
            }
        });

    }



    @Override
    public void onDetach() {

        if (getActivity() != null){
            getActivity().unregisterReceiver(SearchItem);
        }

        super.onDetach();

    }


}
