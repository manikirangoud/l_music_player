package player.music;



public class Constants {

    public interface ACTIONS {
        String PLAY = "player.music.action.PLAY";
        String PAUSE = "player.music.action.PAUSE" ;
        String RESUME = "player.music.action.RESUME" ;
        String STOP_SERVICE = "player.music.action.STOP_SERVICE" ;
        String SEEK_PLAY = "player.music.action.SEEK_PLAY" ;
        String SET_MEDIA_PATH = "player.music.action.SET_MEDIA_PATH" ;
        String UPDATE_BOTTOM_CONTROLS = "player.music.action.UPDATE_BOTTOM_CONTROLS" ;
        String UPDATE_SHARED_PREFERENCES = "player.action.Update_Shared_Preferences" ;
        String SONG_COMPLETED = "player.music.action.Song_Completed" ;
        String UPDATE_PLAYBACK_WITH_ACTIONS = "player.music.action.UPDATE_PLAYBACK_WITH_ACTIONS" ;
        String PLAY_PREVIOUS = "player.music.action.PLAY_PREVIOUS";
        String PLAY_NEXT = "player.music.action.PLAY_NEXT";
        String START_LYRICS =  "player.music.action.START_LYRICS";
        String UPDATE_NOW_PLAYING_CONTROLS = "player.music.action.UPDATE_NOW_PLAYING_CONTROLS" ;
        String SEARCH = "player.music.action.SEARCH";
        String CHANGE_BACKGROUND = "player.music.action.CHANGE_BACKGROUND";
    }

    public interface NOTIFICATION_ACTION {
        String START_FOREGROUND_ACTION = "player.music.notification.action.START_FOREGROUND_ACTION" ;
        String MAIN_ACTION = "player.music.notification.action.MAIN_ACTION" ;
        String UPDATE_NOTIFICATION = "player.music.notification.action.UPDATE_NOTIFICATION" ;
        String UPDATE_NOTIFICATION_SONG_COMPLETED = "player.music.notification.action.UPDATE_NOTIFICATION_SONG_COMPLETED" ;

    }

    public interface STRINGS{
        String PREFS_START_PAGER = "prefsStartPager" ;
        String CURRENT_ALBUM_ART = "currentAlbumArt" ;
        String CURRENT_SONG_TITLE = "currentSongTitle" ;
        String CURRENT_ARTIST_NAME = "artistName" ;
        String CURRENT_ALBUM_TITLE = "albumTitle" ;
        String CURRENT_SONG_DURATION = "songDuration" ;
        String SONG_SEEK_POSITION = "songSeekPosition" ;
        String CURRENT_SONG_PATH = "songPath" ;
        String CURRENT_SONG_POSITION = "currentSongPosition" ;
        String VIEW_PAGER_POSITION = "viewPagerPosition" ;
        String IS_PLAYING = "isPlaying" ;
        String IS_SONG_COMPLETED = "isSongCompleted" ;
        String CURRENT_PLAYLIST_SIZE = "currentPlaylistSize";
        String CURRENT_SONG_POSITION_FROM_PLAYLIST = "currentSongPositionFromPlaylist";
        String IS_SHUFFLE_ON = "isShuffleOn";
        String CURRENT_SONG_ID = "currentSongId";
        String LYRIC_POSITION = "lyricPosition";
        String REPEAT_TAG = "repeatTag";
        String REPEAT_OFF = "repeatOff";
        String REPEAT_ONE = "repeatOne";
        String REPEAT_ALL = "repeatAll";
        String LYRICS_EDITING = "lyricsEditing";
        String LYRICS_ENABLED = "lyricsEnabled";
    }

    public interface CURRENT_PLAYLIST_TC {
        String TABLE_COLUMN_1 = "id",
                TABLE_COLUMN_2 = "SongId",
                TABLE_COLUMN_3 = "SongTitle",
                TABLE_COLUMN_4 = "SongPath",
                TABLE_COLUMN_5 = "SongDuration",
                TABLE_COLUMN_6 = "AlbumArt",
                TABLE_COLUMN_7 = "ArtistName",
                TABLE_COLUMN_8 = "AlbumTitle" ;
    }
}
