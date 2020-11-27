package player.music.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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

import player.music.Constants;
import player.music.MainActivity;
import player.music.R;
import player.music.activities.SyncerActivity;
import player.music.customviews.UbuntuTextView;
import player.music.extras.Preferences;
import player.music.services.PlaybackService;


public class LyricsFragment extends Fragment {

    private View view;
    public static UbuntuTextView textViewTop, textViewBottom;

    public static UbuntuTextView textViewCenter;
    private EditText editTextCenterLyric;
    private CoordinatorLayout coordinatorLayout;
    private ImageView imageViewSubmitLyric;
    public static ArrayList<String> syncedLyrics = new ArrayList<>();
    public static ArrayList<Long> syncedTime = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    public static boolean lyricsFound = false;
    private static final String FOLDER_NAME = "LyricSyncer";

    public static ListView listViewLyrics;

    private String songTitle, artistName, albumTitle;

    private SwitchCompat switchLyricsEditing, switchLyrics;
    private TableRow trLyricsEditing, trLyrics;
    private UbuntuTextView mtvLyricsEditing, mtvLyrics;
    private LinearLayout llLyricsDisplay;
    private LinearLayout llLyrics;
    private ScrollView svLyrics;


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.lyrics_fragment_layout, container, false);

        textViewTop = view.findViewById(R.id.tv_lyrics_top);
        textViewCenter = view.findViewById(R.id.tv_lyrics_center);
        textViewBottom = view.findViewById(R.id.tv_lyrics_bottom);
        editTextCenterLyric = view.findViewById(R.id.et_center_lyric);
        coordinatorLayout = view.findViewById(R.id.coordinator_layout);
        imageViewSubmitLyric = view.findViewById(R.id.iv_submit_lyric);
        switchLyricsEditing = view.findViewById(R.id.switch_lyrics_editing);
        trLyricsEditing = view.findViewById(R.id.tr_lyrics_editing);
        mtvLyricsEditing = view.findViewById(R.id.mtv_lyrics_editing);
        switchLyrics = view.findViewById(R.id.switch_lyrics);
        trLyrics = view.findViewById(R.id.tr_lyrics);
        mtvLyrics = view.findViewById(R.id.mtv_lyrics);
        llLyricsDisplay = view.findViewById(R.id.ll_lyrics_display);
        llLyrics = view.findViewById( R.id.ll_lyrics );
        svLyrics = view.findViewById( R.id.sv_lyrics );

        sharedPreferences = getActivity().getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);

        register_StartLyrics();
        getActivity().sendBroadcast(new Intent(Constants.ACTIONS.START_LYRICS));


        songTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, "");
        artistName = sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, "");
        albumTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_TITLE, "");


        textViewCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Calling getPermissions() method multiple times in order to
                //not get the error permission denied
                getPermissions();
                String text = textViewCenter.getText().toString();

                if (text.equals(getResources().getString(R.string.lyrics_not_found))) {

                    if (PlaybackService.mediaPlayer != null) {

                        if (PlaybackService.mediaPlayer.isPlaying()) {
                            getActivity().sendBroadcast(new
                                    Intent(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS));
                            getActivity().sendBroadcast(new
                                    Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
                        }
                    }
                    Intent intentSyncerActivity = new Intent(getActivity(), SyncerActivity.class);
                    intentSyncerActivity.putExtra("albumArt",
                            Preferences.getCurrentAlbumArt(sharedPreferences));
                    intentSyncerActivity.putExtra("songTitle",
                            Preferences.getCurrentSongTitle(sharedPreferences));
                    intentSyncerActivity.putExtra("albumTitle",
                            Preferences.getCurrentAlbumTitle(sharedPreferences));
                    intentSyncerActivity.putExtra("artistName",
                            Preferences.getArtistName(sharedPreferences));
                    intentSyncerActivity.putExtra("songPath",
                            Preferences.getSongPath(sharedPreferences));
                    intentSyncerActivity.putExtra("songDuration",
                            Preferences.getCurrentSongDuration(sharedPreferences));
                    startActivity(intentSyncerActivity);
                } else {
                    textViewCenter.setVisibility(View.GONE);

                    coordinatorLayout.setVisibility(View.VISIBLE);
                    editTextCenterLyric.setText(text);
                    editTextCenterLyric.setSelection(text.length());
                }
            }
        });

        imageViewSubmitLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Calling getPermissions() method multiple times in order to
                //not get the error permission denied
                getPermissions();

                String updatedLyric = editTextCenterLyric.getText().toString();
                if ( !updatedLyric.isEmpty() ) {

                    editTextCenterLyric.clearFocus();
                    coordinatorLayout.setVisibility(View.GONE);

                    textViewCenter.setVisibility(View.VISIBLE);
                    textViewCenter.setText(updatedLyric);

                    syncedLyrics.remove( MainActivity.lyricPosition );
                    syncedLyrics.add( MainActivity.lyricPosition, updatedLyric );

                    onLyricChanged();
                }
            }
        });



        switchLyricsEditing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Preferences.setLyricsEditing(sharedPreferences, isChecked);
                updateLyricsEditing();

            }
        });


        trLyricsEditing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchLyricsEditing.isEnabled()) {
                    switchLyricsEditing.performClick();
                } else {
                    Snackbar.make(view, getResources().getString(R.string.please_enable_lyrics), Snackbar.LENGTH_LONG).show();
                }

            }
        });



        switchLyrics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                Preferences.setLyricsEnabled(sharedPreferences, isChecked);
                updateLyricsEnabled();
            }
        });


        trLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchLyrics.performClick();
            }
        });


        updateLyricsEnabled();
        updateLyricsEditing();


        setLyrics();


        return view;
    }

    private void setLyrics() {

        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root + "/" + FOLDER_NAME + "/" + artistName.replace("/", "-") + "/" +
                albumTitle.replace("/", "-") + "/" + songTitle + ".txt");

        if (file.exists()) {

            try {


                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));


                String lyrics = bufferedReader.readLine();

                if (lyrics != null){


                    JSONArray jsonArray = new JSONArray(lyrics);
                    int length = jsonArray.length();
                    //Clearing the previous lyrics if lyrics are available.
                    syncedLyrics.clear();
                    syncedTime.clear();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        syncedLyrics.add(jsonObject.getString("lyric"));
                        syncedTime.add(jsonObject.getLong("endTime"));

                   /* MyTextView myTextView = new MyTextView(getActivity());
                    myTextView.setText(syncedLyrics.get(i));
                    myTextView.setTextSize(32.0f);
                    myTextView.setPadding(8, 16, 8, 16 );
                    myTextView.setTextAlignment( View.TEXT_ALIGNMENT_CENTER );
                    llLyrics.addView(myTextView);*/

                    }
                }



            } catch (Exception e) {
                Toast.makeText(getContext(), "new yrics : " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }


    }


    private void updateLyricsEnabled() {

        boolean enabled = Preferences.getLyricsEnabled(sharedPreferences);
        switchLyrics.setChecked(enabled);
        if (enabled){
            mtvLyrics.setText(getResources().getString(R.string.disable_lyrics));
            llLyricsDisplay.setVisibility(View.VISIBLE);
        } else {
            mtvLyrics.setText(getResources().getString(R.string.enable_lyrics));
            llLyricsDisplay.setVisibility(View.INVISIBLE);
        }
        switchLyricsEditing.setEnabled(enabled);
        //getActivity().sendBroadcast(new Intent(Constants.ACTIONS.START_LYRICS));
    }


    private void updateLyricsEditing() {

        boolean isChecked = Preferences.getLyricsEditing(sharedPreferences);
        switchLyricsEditing.setChecked(isChecked);
        if (isChecked) {
            //Checking for lyrics lyricsFound or not and setting textview for enabled.
            mtvLyricsEditing.setText(getResources().getString(R.string.disable_lyrics_editing));
        }else {
            mtvLyricsEditing.setText(getResources().getString(R.string.enable_lyrics_editing));
        }
        textViewCenter.setEnabled(isChecked);
    }



    private void onLyricChanged() {

        try{

            JSONArray jsonArraySyncedLyrics = new JSONArray();
            int i = 0;
            for (String lyric : syncedLyrics) {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("lyric", lyric);

                jsonObject.put("endTime", syncedTime.get(i++));
                jsonArraySyncedLyrics.put(jsonObject);
            }

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

                Toast.makeText(getActivity(), "Lyrics saved successfully!", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void getPermissions() {

        if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }




    public BroadcastReceiver StartLyrics = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equalsIgnoreCase(Constants.ACTIONS.START_LYRICS)
                  /*  && Preferences.getLyricsEnabled(sharedPreferences) */ ) {

                lyricsFound = false;
                try {

                    songTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, "");
                    artistName = sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, "");
                    albumTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_TITLE, "");

                    String root = Environment.getExternalStorageDirectory().toString();
                    File file = new File(root + "/" + FOLDER_NAME + "/" + artistName.replace("/", "-") + "/" +
                            albumTitle.replace("/", "-") + "/" + songTitle + ".txt");

                    if (file.exists()) {

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                        String lyrics = bufferedReader.readLine();
                        JSONArray jsonArray = new JSONArray(lyrics);
                        int length = jsonArray.length();
                        //Clearing the previous lyrics if lyrics are available.
                        syncedLyrics.clear();
                        syncedTime.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            syncedLyrics.add( jsonObject.getString("lyric") );
                            syncedTime.add( jsonObject.getLong("endTime") );
                        }
                        lyricsFound = true;

                   //     arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                   //             syncedLyrics);
                   //     listViewLyrics.setAdapter(arrayAdapter);

                        int position = Preferences.getLyricPosition( context );
                        MainActivity.lyricPosition = position;
                        if ( position > 0 ) {
                            textViewTop.setText( syncedLyrics.get( position - 1 ) );
                        } else {
                            textViewTop.setText("");
                        }
                        textViewCenter.setText( syncedLyrics.get( position ) );


                        /*svLyrics.scrollTo( llLyrics.getChildAt(position).getScrollX(), llLyrics.getChildAt(position).getScrollY());
                        llLyrics.scrollTo( llLyrics.getChildAt(position).getScrollX(), llLyrics.getChildAt(position).getScrollY());

                        svLyrics.scrollBy( llLyrics.getChildAt(position).getScrollX(), llLyrics.getChildAt(position).getScrollY());
                        llLyrics.scrollBy( llLyrics.getChildAt(position).getScrollX(), llLyrics.getChildAt(position).getScrollY());*/

                        if(position < syncedLyrics.size()-1) {
                            textViewBottom.setText(syncedLyrics.get(position + 1));
                        }

                    } else {

                        textViewTop.setText("");
                        textViewCenter.setText(getResources().getString(R.string.lyrics_not_found));
                        textViewBottom.setText("");
                    }

                } catch (Exception e) {
                    textViewCenter.setText(getResources().getString(R.string.lyrics_not_found));
                    Log.e("ERR_LYRICS_FRAGMENT", e.toString());
                }

                updateLyricsEditing();
            }
        }
    };

    private void register_StartLyrics() {
        Log.i("Register", "Lyrics registered");
        getActivity().registerReceiver(StartLyrics , new IntentFilter(Constants.ACTIONS.START_LYRICS));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }




    @Override
    public void onDetach() {

        Preferences.setLyricPosition(getActivity(), MainActivity.lyricPosition);
        getActivity().unregisterReceiver(StartLyrics);
        super.onDetach();

    }
}