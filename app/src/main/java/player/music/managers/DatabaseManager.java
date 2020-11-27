package player.music.managers;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.annotation.Nullable;
import android.util.Log;

import java.util.List;

import player.music.Constants;
import player.music.extras.Preferences;
import player.music.objects.Song;

public class DatabaseManager {

    private static final String DATABASE_NAME = "LyricalMusic";
    private static final String TABLE_NAME_CURRENT_PLAYLIST = "currentPlaylist";
    private static final String TABLE_NAME_LYRICS = "lyrics";
    private static final String TABLE_NAME_PLAYLISTS = "playlists";
    private static SQLiteDatabase sqLiteDatabase;

    private static final String PLAYLISTS_TC_1 = "id",
            PLAYLISTS_TC_2 = "playlistName";


    private static final String CURRENT_PLAYLIST_TC_1 = "id",
            CURRENT_PLAYLIST_TC_2 = "SongId",
            CURRENT_PLAYLIST_TC_3 = "SongTitle",
            CURRENT_PLAYLIST_TC_4 = "SongPath",
            CURRENT_PLAYLIST_TC_5 = "SongDuration",
            CURRENT_PLAYLIST_TC_6 = "AlbumArt",
            CURRENT_PLAYLIST_TC_7 = "ArtistName",
            CURRENT_PLAYLIST_TC_8 = "AlbumTitle" ;


    private static final String LYRICS_TC_1 = "songId",
            LYRICS_TC_2 = "songTitle",
            LYRICS_TC_3 = "lyrics";


    private static final String CREATE_TABLE_CURRENT_PLAYLIST = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME_CURRENT_PLAYLIST + "(" + CURRENT_PLAYLIST_TC_1 + " TEXT," + CURRENT_PLAYLIST_TC_2 +
            " TEXT," + CURRENT_PLAYLIST_TC_3 + " TEXT," + CURRENT_PLAYLIST_TC_4 + " TEXT, " +
            CURRENT_PLAYLIST_TC_5 + " TEXT," + CURRENT_PLAYLIST_TC_6 + " TEXT," +
            CURRENT_PLAYLIST_TC_7 +" TEXT, " + CURRENT_PLAYLIST_TC_8 + " TEXT" + ");" ;


    private static final String CREATE_TABLE_LYRICS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_LYRICS +
            "(" + LYRICS_TC_1 + " TEXT," + LYRICS_TC_2
            + " TEXT," + LYRICS_TC_3 + " TEXT);";

    private static final String CREATE_TABLE_PLAYLISTS = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PLAYLISTS
            + "(" + PLAYLISTS_TC_1 + " TEXT AI," + PLAYLISTS_TC_2 + " TEXT);";



    private static final String DROP_TABLE_CURRENT_PLAYLIST = "DROP TABLE IF EXISTS " + TABLE_NAME_CURRENT_PLAYLIST;
    private static final String DROP_TABLE_LYRICS = "DROP TABLE IF EXISTS " + TABLE_NAME_LYRICS;



    public DatabaseManager() {
    }


    public static void openDataBase(Context context) {
        sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
    }


    public static void closeDataBase() {

        if ( sqLiteDatabase != null && sqLiteDatabase.isOpen() ) {
            sqLiteDatabase.close();
        }
    }


    public static void updateCurrentPlaylistTable() {
     //   sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        try {
            sqLiteDatabase.execSQL(DROP_TABLE_CURRENT_PLAYLIST);
            sqLiteDatabase.execSQL(CREATE_TABLE_CURRENT_PLAYLIST);
        } catch (Exception e) {
            Log.e("ERROR_UPDATE_TABLE", e.toString());
        }
    }

    public static void insertCurrentPlaylist(List<Song> songs) {

        try {

            int i = 1;

            for ( Song song : songs ) {

                if ( song.getTitle() != null ) {
                    song.setTitle(song.getTitle().replaceAll("'", "''"));
                }
                if ( song.getPath() != null ) {
                    song.setPath(song.getPath().replaceAll("'", "''"));
                }
                if ( song.getAlbumArt() != null ) {
                    song.setAlbumArt(song.getAlbumArt().replaceAll("'", "''"));
                }
                if ( song.getArtistName() != null ) {
                    song.setArtistName(song.getArtistName().replaceAll("'", "''"));
                }
                if ( song.getAlbumTitle() != null ) {
                    song.setAlbumTitle(song.getAlbumTitle().replaceAll("'", "''"));
                }

        sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME_CURRENT_PLAYLIST + " VALUES('" + (i) +
                        "','" + song.getId() + "','" + song.getTitle() + "','" +
                        song.getPath() + "','" + song.getDuration() + "','" +
                        song.getAlbumArt() + "','" + song.getArtistName() + "','" + song.getAlbumTitle()
                        + "');");

                //For removing single quotes double times in the songs title.
                if ( song.getTitle() != null ) {
                    song.setTitle(song.getTitle().replaceAll("''", "'"));
                }
                if ( song.getPath() != null ) {
                    song.setPath(song.getPath().replaceAll("''", "'"));
                }
                if ( song.getAlbumArt() != null ) {
                    song.setAlbumArt(song.getAlbumArt().replaceAll("''", "'"));
                }
                if ( song.getArtistName() != null ) {
                    song.setArtistName(song.getArtistName().replaceAll("''", "'"));
                }
                if ( song.getAlbumTitle() != null ) {
                    song.setAlbumTitle(song.getAlbumTitle().replaceAll("''", "'"));
                }
                i++;
            }

            Log.e("INSERT_ DATABASE", "SUCCESSFULLY INSERTED");

        } catch ( Exception e ){
            Log.e("ERROR_INSERT_DATABASE", e.toString());
        }
    }



    public static void playTrackAtPosition(Context context, int position) {
        try {
            Cursor cursor;

            int playlistSize = Preferences.getCurrentPlaylistSize(context);

            Log.e("1st New asjdhakjs: ", position + " , " + playlistSize);

            if ( position >= playlistSize ) {
                position = 0;
            }
            if ( position == -1 ){
                position = playlistSize - 1;
            }

            Log.e("2nd New asjdhakjs: ", position + " , " + playlistSize);

          //  sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_CURRENT_PLAYLIST + " WHERE " + CURRENT_PLAYLIST_TC_1 + " LIKE " + position, null);
            if (cursor != null && cursor.moveToFirst()) {
                android.content.SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);

                Preferences.setCurrentAlbumArt(sharedPreferences, cursor.getString(cursor.
                        getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_6)));
                Preferences.setSongPath(sharedPreferences, cursor.getString(cursor.
                        getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_4)));

                Preferences.setCurrentSongTitle(sharedPreferences, cursor.getString(cursor.
                        getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_3)));
                Preferences.setArtistName(sharedPreferences, cursor.getString(cursor.
                        getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_7)));
                Preferences.setCurrentSongDuration(sharedPreferences, Long.parseLong(cursor.
                        getString(cursor.getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_5))));
                Preferences.setIsPlaying(sharedPreferences, true);

                Preferences.setLyricPosition(context, 0);


                Intent intentNextTrackPlay = new Intent();
                intentNextTrackPlay.setAction(Constants.ACTIONS.SET_MEDIA_PATH);
                context.sendBroadcast(intentNextTrackPlay);

                context.sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));


                Preferences.setCurrentSongPositionFromPlaylist(sharedPreferences, position);
                Preferences.setCurrentSongId(sharedPreferences, Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_2))));
                Preferences.setCurrentAlbumTitle(sharedPreferences, cursor.getString(cursor.getColumnIndexOrThrow(Constants.CURRENT_PLAYLIST_TC.TABLE_COLUMN_8)));
                Preferences.setIsSongCompleted(sharedPreferences, false);

                cursor.close();
            }
          //  sqLiteDatabase.close();
        } catch (Exception e) {
            if (!(e.toString().contains("no such table"))) {

                Log.e("ERROR_PLAY_NEXT_TRACK", e.toString());
            }
        }
    }



    public static void addSongToPlaylist(Context context, Song song,SharedPreferences sharedPreferences) {

        try {

            int playlistSize = Preferences.getCurrentPlaylistSize(context);


            if ( song.getTitle() != null ) {
                song.setTitle(song.getTitle().replaceAll("'", "''"));
            }
            if ( song.getPath() != null ) {
                song.setPath(song.getPath().replaceAll("'", "''"));
            }
            if ( song.getAlbumArt() != null ) {
                song.setAlbumArt(song.getAlbumArt().replaceAll("'", "''"));
            }
            if ( song.getArtistName() != null ) {
                song.setArtistName(song.getArtistName().replaceAll("'", "''"));
            }
            if ( song.getAlbumTitle() != null ) {
                song.setAlbumTitle(song.getAlbumTitle().replaceAll("'", "''"));
            }

            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME_CURRENT_PLAYLIST + " VALUES('" + (playlistSize) +
                    "','" + song.getId() + "','" + song.getTitle() + "','" +
                    song.getPath() + "','" + song.getDuration() + "','" +
                    song.getAlbumArt() + "','" + song.getArtistName() + "','" + song.getAlbumTitle()
                    + "');");

            //For removing single quotes double times in the songs title.
            if ( song.getTitle() != null ) {
                song.setTitle(song.getTitle().replaceAll("''", "'"));
            }
            if ( song.getPath() != null ) {
                song.setPath(song.getPath().replaceAll("''", "'"));
            }
            if ( song.getAlbumArt() != null ) {
                song.setAlbumArt(song.getAlbumArt().replaceAll("''", "'"));
            }
            if ( song.getArtistName() != null ) {
                song.setArtistName(song.getArtistName().replaceAll("''", "'"));
            }
            if ( song.getAlbumTitle() != null ) {
                song.setAlbumTitle(song.getAlbumTitle().replaceAll("''", "'"));
            }

            Preferences.setCurrentPlaylistSize(sharedPreferences, ++playlistSize);
            Log.e("INSERT_ DATABASE", "SUCCESSFULLY INSERTED");

        } catch ( Exception e ){
            Log.e("ERROR_INSERT_DATABASE", e.toString());
        }
    }


    //To get song position for playing song
    // when there are different repeat modes.
    private static int getSongPosition(Context context, int position) {


        String repeatTag = Preferences.getRepeatTag(context);



        switch ( repeatTag ) {


            case Constants.STRINGS.REPEAT_OFF :
                                break;

            case Constants.STRINGS.REPEAT_ONE :

                                break;

            case Constants.STRINGS.REPEAT_ALL :

                                break;
        }

        return position;
    }

    //DATABASE TABLE FOR LYRICS
    public static void updateLyricsTable(Context context) {
     //   sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        try {
            sqLiteDatabase.execSQL(DROP_TABLE_LYRICS);
            sqLiteDatabase.execSQL(CREATE_TABLE_LYRICS);
        } catch (Exception e) {
            Log.e("ERROR_UPDATE_TABLE", e.toString());
        }
    }

    public static void insertLyrics(long[] songId, String[] songTitle) {
        int length = songId.length;
        for(int i = 0; i < length; i++) {
            songTitle[i] = songTitle[i].replace("'", "\'");
            sqLiteDatabase.execSQL("INSERT INTO " + TABLE_NAME_LYRICS + " VALUES('" + songId[i] + "','" + songTitle[i] + "','" + "l" + "');");
        }
    }



    public static String getLyrics(long songId) {
        String s = "";
        try{
            Cursor cursor;
            cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME_LYRICS + " WHERE " + LYRICS_TC_1 + " LIKE " + songId, null);
            if(cursor != null && cursor.moveToFirst()) {
                s = cursor.getString(cursor.getColumnIndexOrThrow(LYRICS_TC_3));
                cursor.close();
            }
            Log.i("lyrics_DB", s);
        } catch (Exception e) {
            Log.e("ERR_GET_LYRICS_DATABASE", e.toString());
        }
        return s;
    }


    public static void createPlaylistsTable(){

        try {
            sqLiteDatabase.execSQL(CREATE_TABLE_LYRICS);
        } catch (Exception e) {
            Log.e("ERROR_PLAYLISTS_TABLE", e.toString());
        }
    }

    public static void getPlaylistsTable(){


    }


}
