package player.music.managers;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

public class ArtistsManager {

    private Cursor cursor;
    private String[] artistsName;
    private int[] artistsId, numberOfAlbums, numberOfTracks;
    private String[] projection = {MediaStore.Audio.Artists._ID, MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS};

    public ArtistsManager(Context context) {
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, null, null,
                    "LOWER(" + MediaStore.Audio.Artists.ARTIST+ ")ASC");

            if (cursor != null && cursor.moveToFirst()) {
                int length = cursor.getCount();
                artistsName = new String [length];
                artistsId = new int[length];
                numberOfAlbums = new int[length];
                numberOfTracks = new int[length];
                for (int i = 0; i < length; i++) {
                   artistsName[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST));
                    artistsId[i] = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)));
                    numberOfAlbums[i] = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)));
                    numberOfTracks[i] = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)));
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            Toast.makeText(context,"Artists Manager Activity : " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public int[] getArtistsId() {
        return artistsId;
    }

    public String[] getArtistsName() {
        return artistsName;
    }

    public int[] getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public int[] getNumberOfTracks() {
        return numberOfTracks;
    }


}
