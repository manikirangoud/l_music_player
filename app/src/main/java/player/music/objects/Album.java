package player.music.objects;

import java.io.Serializable;


public class Album implements Serializable{

    private String albumTitle, albumArt, artistName;
    private long albumId;
    private int numberOfTracks, year;

    public Album() {

    }

    public Album(String albumTitle, String albumArt, String artistName, long albumId,
                 int numberOfTracks, int year ) {
        this.albumTitle = albumTitle;
        this.albumArt = albumArt;
        this.artistName = artistName;
        this.albumId = albumId;
        this.numberOfTracks = numberOfTracks;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public int getNumberOfTracks() {
        return numberOfTracks;
    }

    public void setNumberOfTracks(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }

    @Override
    public boolean equals( Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
