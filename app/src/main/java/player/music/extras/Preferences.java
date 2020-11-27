package player.music.extras;


import android.content.Context;
import android.content.SharedPreferences;

import player.music.Constants;

public class Preferences {

    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPreferences;



    public static void setSongPath(SharedPreferences sharedPreferences, String songPath) {
        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.CURRENT_SONG_PATH, songPath);
        editor.apply();
    }

    public static String getSongPath(SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, "");
    }

    public static void setCurrentSongPosition(SharedPreferences sharedPreferences, int currentSongPosition) {
        editor = sharedPreferences.edit();
        editor.putInt(Constants.STRINGS.CURRENT_SONG_POSITION, currentSongPosition);
        editor.apply();
    }

    public static int getCurrentSongPosition(SharedPreferences sharedPreferences ) {
        return sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION, 0);
    }



    public static void setArtistName(SharedPreferences sharedPreferences, String currentArtistName){
        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.CURRENT_ARTIST_NAME, currentArtistName);
        editor.apply();
    }

    public static String getArtistName( SharedPreferences sharedPreferences ){

        return sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, "");
    }

    public static void setCurrentSongDuration(SharedPreferences sharedPreferences, long currentSongDuration){
        editor = sharedPreferences.edit();
        editor.putLong(Constants.STRINGS.CURRENT_SONG_DURATION, currentSongDuration);
        editor.apply();
    }

    public static long getCurrentSongDuration(SharedPreferences sharedPreferences){
        return sharedPreferences.getLong(Constants.STRINGS.CURRENT_SONG_DURATION, 0);
    }
    public static void setViewPagerPosition(SharedPreferences sharedPreferences, int viewPagerPosition){
        editor = sharedPreferences.edit();
        editor.putInt(Constants.STRINGS.VIEW_PAGER_POSITION, viewPagerPosition);
        editor.apply();
    }

    public static void setCurrentAlbumArt(SharedPreferences sharedPreferences, String currentAlbumArt){
        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.CURRENT_ALBUM_ART, currentAlbumArt);
        editor.apply();
    }


    public static String getCurrentAlbumArt( SharedPreferences sharedPreferences ){
        return sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_ART, "");
    }

    public static void setIsSongCompleted(SharedPreferences sharedPreferences, boolean isSongCompleted){
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STRINGS.IS_SONG_COMPLETED, isSongCompleted);
        editor.apply();
    }

    public static void setCurrentSongTitle(SharedPreferences sharedPreferences, String currentSongTitle){
        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.CURRENT_SONG_TITLE, currentSongTitle);
        editor.apply();
    }


    public static String getCurrentSongTitle(SharedPreferences sharedPreferences){
        return sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, "");
    }


    public static void setIsPlaying(SharedPreferences sharedPreferences, boolean isPlaying){
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STRINGS.IS_PLAYING, isPlaying);
        editor.apply();
    }

    public static boolean getIsPlaying(SharedPreferences sharedPreferences){
        return sharedPreferences.getBoolean(Constants.STRINGS.IS_PLAYING, false);
    }

    public static void setCurrentSongId(SharedPreferences sharedPreferences, long songId){
        editor = sharedPreferences.edit();
        editor.putLong(Constants.STRINGS.CURRENT_SONG_ID, songId);
        editor.apply();
    }

    public static void setCurrentPlaylistSize(SharedPreferences sharedPreferences, int playlistSize){
        editor = sharedPreferences.edit();
        editor.putInt(Constants.STRINGS.CURRENT_PLAYLIST_SIZE, playlistSize);
        editor.apply();
    }

    public static int getCurrentPlaylistSize(Context context){

        sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.STRINGS.CURRENT_PLAYLIST_SIZE, 0);
    }

    public static void setCurrentSongPositionFromPlaylist(SharedPreferences sharedPreferences, int songPosition) {
        editor = sharedPreferences.edit();
        editor.putInt(Constants.STRINGS.CURRENT_SONG_POSITION_FROM_PLAYLIST, songPosition);
        editor.apply();
    }

    public static int getCurrentSongPositionFromPlaylist(Context context) {

        sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION_FROM_PLAYLIST, 0);

    }

    public static void setShuffle(SharedPreferences sharedPreferences, boolean isShuffleOn) {
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STRINGS.IS_SHUFFLE_ON, isShuffleOn);
        editor.apply();
    }

    public static void setCurrentAlbumTitle(SharedPreferences sharedPreferences, String currentAlbumName) {
        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.CURRENT_ALBUM_TITLE, currentAlbumName);
        editor.apply();
    }

    public static String getCurrentAlbumTitle(SharedPreferences sharedPreferences) {

        return sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_TITLE, "");
    }

    public static void setLyricPosition(Context context, int position) {

        sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER,
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(Constants.STRINGS.LYRIC_POSITION, position);
        editor.apply();
    }

    public static int getLyricPosition(Context context){

        sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER,
                Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Constants.STRINGS.LYRIC_POSITION, 0);
    }

    public static void setRepeatTag(SharedPreferences sharedPreferences, String repeatTag) {

        editor = sharedPreferences.edit();
        editor.putString(Constants.STRINGS.REPEAT_TAG, repeatTag);
        editor.apply();
    }

    public static String getRepeatTag(Context context) {

        sharedPreferences = context.getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER,
                Context.MODE_PRIVATE);
        return sharedPreferences.getString(Constants.STRINGS.REPEAT_TAG, "");
    }


    public static void setLyricsEditing(SharedPreferences sharedPreferences, boolean checked) {

        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STRINGS.LYRICS_EDITING, checked);
        editor.apply();
    }

    public static boolean getLyricsEditing( SharedPreferences sharedPreferences ) {

        return sharedPreferences.getBoolean(Constants.STRINGS.LYRICS_EDITING, false);
    }

    public static void setLyricsEnabled(SharedPreferences sharedPreferences, boolean enable) {

        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.STRINGS.LYRICS_ENABLED, enable);
        editor.apply();
    }

    public static boolean getLyricsEnabled( SharedPreferences sharedPreferences ) {

        return sharedPreferences.getBoolean(Constants.STRINGS.LYRICS_ENABLED, true);
    }

    /*
    public static int getTheme() {

        int a = 0;



        return a;
    }


    public static void setTheme() {

    }

    */







}




