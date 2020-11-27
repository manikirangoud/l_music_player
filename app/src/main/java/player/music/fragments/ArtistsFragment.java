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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import player.music.Constants;
import player.music.R;
import player.music.activities.ArtistDetailsActivity;
import player.music.adapters.ArtistsAdapter;
import player.music.customviews.UbuntuTextView;
import player.music.inerface.RecyclerViewOnItemClickListener;
import player.music.objects.Artist;


public class ArtistsFragment extends Fragment implements RecyclerViewOnItemClickListener{


    private List<Artist> artists;
    private ArtistsAdapter artistsAdapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view;

        view = inflater.inflate(R.layout.artists_fragment_layout, container, false);
        recyclerView = view.findViewById(R.id.rv_artists);
        progressBar = view.findViewById(R.id.pb_artists);


        ArtistsAdapter.recyclerViewOnItemClickListener = this;

        new FetchArtists().execute();


        return view;
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

                    List<Artist> filteredArtists = new ArrayList<>();
                    for (Artist artist : artists) {

                        if (artist.getArtistName().toLowerCase().contains(target.toLowerCase())) {

                            filteredArtists.add(artist);
                        }
                    }

                    if (artistsAdapter != null) {

                        ArtistsAdapter.artists = filteredArtists;
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("ERR_Search_albums", e.toString());
                }

            }

        }
    };


    private class FetchArtists extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            Cursor cursor;
            String[] projection = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST,
                    MediaStore.Audio.Artists.NUMBER_OF_ALBUMS, MediaStore.Audio.Artists.NUMBER_OF_TRACKS};


            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                        projection, null, null,
                        "LOWER(" + MediaStore.Audio.Artists.ARTIST+ ")ASC");

                if (cursor != null && cursor.moveToFirst()) {

                    artists = new ArrayList<>();

                    do {
                        Artist artist = new Artist();

                        artist.setArtistName( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)));
                        artist.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)));
                        artist.setNumberOfAlbums(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                        artist.setNumberOfTracks(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));

                        artists.add(artist);
                    } while ( cursor.moveToNext() );
                    cursor.close();
                }

            } catch (Exception e) {
                Toast.makeText(getActivity(),"Artists Manager Activity : " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            artistsAdapter = new ArtistsAdapter( artists );
            recyclerView.setLayoutManager( new LinearLayoutManager(getActivity()) );
            recyclerView.setAdapter(artistsAdapter);
            progressBar.setVisibility(View.GONE);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL);
            dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_small));
            recyclerView.addItemDecoration(dividerItemDecoration);

            register_Search();
        }

    }



    @Override
    public void recyclerViewItemClicked(final View view, final int position, long id) {

/*
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {*/

                Intent artistDetails = new Intent(getActivity(), ArtistDetailsActivity.class);
                artistDetails.putExtra("artistName", ArtistsAdapter.artists.get(position).getArtistName());
                artistDetails.putExtra("artistId", ArtistsAdapter.artists.get(position).getId());
                artistDetails.putExtra("artistNumberOfAlbums", ArtistsAdapter.artists.get(position).getNumberOfAlbums() );
                artistDetails.putExtra("artistNumberOfTracks", ArtistsAdapter.artists.get(position).getNumberOfTracks() );


                Pair[] pairs = new Pair[5];

                CardView cvArtistShortName = view.findViewById(R.id.cv_artist_short_name);
                ImageView overflowMenu = view.findViewById(R.id.iv_overflow_menu);
                UbuntuTextView artistName = view.findViewById(R.id.utv_artist_name);
                UbuntuTextView albumCount = view.findViewById(R.id.utv_album_count);
                UbuntuTextView trackCount = view.findViewById(R.id.utv_track_count);

                pairs[0] = new Pair<View, String>(cvArtistShortName, getResources().getString(R.string.artist_image));
                pairs[1] = new Pair<View, String>(overflowMenu, getResources().getString(R.string.overflow_button));
                pairs[2] = new Pair<View, String>(artistName, getResources().getString(R.string.toolbar_title));
                pairs[3] = new Pair<View, String>(albumCount, getResources().getString(R.string.album_count));
                pairs[4] = new Pair<View, String>(trackCount, getResources().getString(R.string.track_count));

                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(getActivity(), pairs);
                startActivity(artistDetails, activityOptions.toBundle());
      /*      }
        });

        thread.start();*/

    }

    @Override
    public void recyclerViewItemClicked(View view, int position) {

    }


    @Override
    public void recyclerViewOverflowClicked(View view, int position) {

        Context context = new ContextThemeWrapper( getActivity(), R.style.PopupMenuStyle);

        PopupMenu popupMenu = new PopupMenu( context, view );
        popupMenu.getMenuInflater().inflate(R.menu.song_menu, popupMenu.getMenu());

        popupMenu.show();
    }

    @Override
    public void onDetach() {

        getActivity().unregisterReceiver(SearchItem);
        super.onDetach();

    }



}
