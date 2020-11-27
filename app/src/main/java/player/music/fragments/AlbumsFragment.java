package player.music.fragments;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;


import java.util.ArrayList;
import java.util.List;

import player.music.Constants;
import player.music.R;
import player.music.activities.AlbumDetailsActivity;
import player.music.adapters.AlbumsAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Album;

public class AlbumsFragment extends Fragment implements RecyclerViewOnItemClickListener {


    private RecyclerView recyclerView;
    private AlbumsAdapter albumsAdapter;
    private GridLayoutManager layoutManager;
    private ProgressBar progressBar;


    List<Album> albums = new ArrayList<>();

    private RecyclerViewOnItemClickListener recyclerViewOnItemClickListener;


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {

        View view;


        view = inflater.inflate(R.layout.albums_fragment_layout, container, false);

        recyclerView = view.findViewById(R.id.rv_albums_fragment_layout);
        progressBar = view.findViewById(R.id.progress_bar_albums);


        recyclerViewOnItemClickListener = this;


        new FetchAlbums().execute();


        return view;
    }



    private class FetchAlbums extends AsyncTask<Void, Void, Void>{


        @Override
        protected Void doInBackground(Void... voids) {

            Cursor cursor;
            String[] projection = {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM_ART, MediaStore.Audio.Albums.ARTIST,
                    MediaStore.Audio.Albums.NUMBER_OF_SONGS};

            cursor = getActivity().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection, null, null,
                    "LOWER(" + MediaStore.Audio.Media.ALBUM + ")ASC");


            if (cursor != null && cursor.moveToFirst()) {

                do {

                    Album album = new Album();

                    album.setAlbumTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
                    album.setAlbumArt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)));
                    album.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));
                    album.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
                    album.setNumberOfTracks(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));

                    albums.add(album);

                }while (cursor.moveToNext());
                cursor.close();
            }
        return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressBar.setVisibility(View.GONE);
            AlbumsAdapter.albums = albums;
            albumsAdapter = new AlbumsAdapter(recyclerViewOnItemClickListener);
            layoutManager = new GridLayoutManager(getActivity(), 2);


            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(albumsAdapter);


            register_Search();
        }
    }



    private void register_Search() {
        getActivity().registerReceiver(SearchItem, new IntentFilter(Constants.ACTIONS.SEARCH));
    }



    private BroadcastReceiver SearchItem = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTIONS.SEARCH)) {

                String target = intent.getStringExtra("target");
                try {

                    List<Album> filteredAlbums = new ArrayList<>();
                    for (Album album : albums) {

                        if (album.getAlbumTitle().toLowerCase().contains(target.toLowerCase())) {

                            filteredAlbums.add(album);
                        }
                    }

                    if (albumsAdapter != null) {

                        AlbumsAdapter.albums = filteredAlbums;
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("ERR_Search_albums", e.toString());
                }

            }

        }
    };



    @Override
    public void recyclerViewItemClicked(View view, int position, long albumId) {

        try {


            Intent intentAlbumDetails = new Intent(getActivity(), AlbumDetailsActivity.class);
            intentAlbumDetails.putExtra("albumId", AlbumsAdapter.albums.get(position).getAlbumId());
            intentAlbumDetails.putExtra("albumTitle", AlbumsAdapter.albums.get(position).getAlbumTitle());
            intentAlbumDetails.putExtra("albumArt", AlbumsAdapter.albums.get(position).getAlbumArt());
            intentAlbumDetails.putExtra("numberOfTracks", AlbumsAdapter.albums.get(position).getNumberOfTracks());

            Pair[] pairs = new Pair[4];

            ImageView tempAlbum = view.findViewById(R.id.iv_album);
            ImageView tempOverflow = view.findViewById(R.id.iv_overflow_menu);
            UbuntuTextView tempTitle = view.findViewById(R.id.text_view_album_name_album_layout);
            UbuntuTextView tempTracks = view.findViewById(R.id.text_view_number_of_tracks_album_layout);

            pairs[0] = new Pair<View, String>(tempAlbum, getResources().getString(R.string.album_thumbnail));
            pairs[1] = new Pair<View, String>(tempOverflow, getResources().getString(R.string.overflow_button));
            pairs[2] = new Pair<View, String>(tempTitle, getResources().getString(R.string.album_title));
            pairs[3] = new Pair<View, String>(tempTracks, getResources().getString(R.string.track_count));

            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
            startActivity(intentAlbumDetails, activityOptions.toBundle());

        } catch ( Exception e ) {
            Log.e("AlbumsFragment-RVIC", e.toString());
        }

    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }

    @Override
    public void recyclerViewOverflowClicked(View view, final int position) {



        Context context = new ContextThemeWrapper( getActivity(), R.style.PopupMenuStyle);

        PopupMenu popupMenu = new PopupMenu( context, view );
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());


        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();



                return false;
            }
        });


    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

/*
        SearchView searchView = getActivity().findViewById(R.id.search_view);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                Log.e("AlbumsSearch", s);

                return true;
            }
        });

        */
    }



    @Override
    public void onDetach() {

        getActivity().unregisterReceiver(SearchItem);
        super.onDetach();
    }



}
