package player.music.managers;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import player.music.objects.Song;

public class SongsManager{


    private HashMap<Long, String> albumsArt = new HashMap<>();

    private List<Song> songs = new ArrayList<>();

    private String[] projection1 = {MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM_ID, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST_ID},
            projection2 = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART};

    public SongsManager(Context context) {
         Cursor cursor, cursor2;

        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection1, null, null,
                    "LOWER(" + MediaStore.Audio.Media.TITLE + ")ASC");

            if (cursor != null && cursor.moveToFirst()) {
    /*           int cursorLength = cursor.getCount();
                songsTitle = new String[cursorLength];
    //            albumsId = new long[cursorLength];
    ///            songsPath = new String[cursorLength];
    //            albumsTitle = new String[cursorLength];
    //            artistsName = new String[cursorLength];
    //            songsDuration = new long[cursorLength];
    //            songsId = new long[cursorLength];
    //            artistsId = new long[cursorLength];
                int i =0;
                do {
                    songsTitle[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    albumsId[i] = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    albumsTitle[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    artistsName[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    songsPath[i] = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    songsDuration[i] = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                    songsId[i] = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    artistsId[i] = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));
                    i++;
                }while(cursor.moveToNext());

                */

                do {
   /*                 Song song = new Song();
                    song.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                    song.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    song.setAlbumTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                    song.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                    song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                    song.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                    song.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                    song.setArtistId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)));

                    songs.add(song);*/

                }while(cursor.moveToNext());






                cursor.close();
//GETTING THE ALBUMS ART OF ALL ALBUMS
                cursor2 = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection2, null, null,
                        "LOWER(" + MediaStore.Audio.Albums._ID + ")ASC");

                if(cursor2 != null && cursor2.moveToFirst() ) {

                    do {
                        albumsArt.put(cursor2.getLong(cursor2.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)),
                                cursor2.getString(cursor2.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)));

                    } while (cursor2.moveToNext());
                    cursor2.close();
                }

            }
        }catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }


    public List<Song> getSongs() {
        return songs;
    }


    public HashMap<Long, String> getAlbumsArtWithALbumsId() {
        return albumsArt;
    }


}
