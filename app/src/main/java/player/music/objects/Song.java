package player.music.objects;

import java.io.Serializable;

public class Song implements Serializable{


    private String title, path, albumTitle, artistName, albumArt;
    private long duration, id, artistId, albumId;
    private int year;

    public Song() {

    }

    public Song(String title, String path, String albumTitle, String artistName,
                String albumArt, long duration, long id, long artistId, long albumId, int year) {
        this.title = title;
        this.path = path;
        this.albumTitle = albumTitle;
        this.artistName = artistName;
        this.albumArt = albumArt;
        this.duration = duration;
        this.id = id;
        this.artistId = artistId;
        this.albumId = albumId;
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }
}
