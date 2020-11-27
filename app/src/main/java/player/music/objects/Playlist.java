package player.music.objects;

import java.io.Serializable;

public class Playlist implements Serializable {


    private long songId, songDuration;

    private  String albumArt, songTitle, songPath, artistName, albumTitle;


    public Playlist(long songId, long songDuration, String albumArt, String songTitle,
                    String songPath, String artistName, String albumTitle) {
        this.songId = songId;
        this.songDuration = songDuration;
        this.albumArt = albumArt;
        this.songTitle = songTitle;
        this.songPath = songPath;
        this.artistName = artistName;
        this.albumTitle = albumTitle;
    }


    public long getSongId() {
        return songId;
    }

    public void setSongId(long songId) {
        this.songId = songId;
    }

    public long getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(long songDuration) {
        this.songDuration = songDuration;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }
}
