package player.music.objects;

import java.io.Serializable;

public class Artist implements Serializable {

    private String artistName;
    private int numberOfAlbums, numberOfTracks;
    private long id;

    public Artist() {

    }

    public Artist(String artistName, int numberOfAlbums, int numberOfTracks, long id) {
        this.artistName = artistName;
        this.numberOfAlbums = numberOfAlbums;
        this.numberOfTracks = numberOfTracks;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public void setNumberOfAlbums(int numberOfAlbums) {
        this.numberOfAlbums = numberOfAlbums;
    }

    public int getNumberOfTracks() {
        return numberOfTracks;
    }

    public void setNumberOfTracks(int numberOfTracks) {
        this.numberOfTracks = numberOfTracks;
    }
}
