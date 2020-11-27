package player.music.managers;


import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import player.music.objects.Album;


public class AlbumsManager{

  //  private String[] albumsTitle, albumsArt, artistsName;
  //  private long[] albumsId;
  //  private int[] albumsNumberOfSongs;

   // private List<Album> albums = new ArrayList<>();


    public AlbumsManager(Context context) {

        Cursor cursor;
        String[] projection = {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS};
      //  StringBuilder stringBuilder = new StringBuilder();

        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null,
                    "LOWER(" + MediaStore.Audio.Media.ALBUM + ")ASC");

            if (cursor != null && cursor.moveToFirst()) {

                int cursorLength = cursor.getCount();
           //     albumsTitle = new String[cursorLength];
           //     albumsArt = new String[cursorLength];
           //     artistsName = new String[cursorLength];
           //     albumsId = new long[cursorLength];
           //     albumsNumberOfSongs = new int[cursorLength];
           //     stringBuilder.append("{albumDetails:[");


                //    int i =0;
                    do {
           //          albumsTitle[i] = cursorAlbumDetails.getString(cursorAlbumDetails.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM));
           //         albumsArt[i] = cursorAlbumDetails.getString(cursorAlbumDetails.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART));
           //         artistsName[i] = cursorAlbumDetails.getString(cursorAlbumDetails.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST));
           //         albumsId[i] = Long.parseLong(cursorAlbumDetails.getString(cursorAlbumDetails.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
            //        albumsNumberOfSongs[i] = Integer.parseInt(cursorAlbumDetails.getString(cursorAlbumDetails.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));
                //      i++

                    Album album = new Album();
                    album.setAlbumTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)));
                    album.setAlbumArt(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM_ART)));
                    album.setArtistName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)));
                    album.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)));
                    album.setNumberOfTracks(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)));

                     //   AlbumsFragment.albums.add(album);



                /*    stringBuilder.append("{\"albumsId\":\"" + albumsId[i] + "\",\"albumsArt\":\"" + albumsArt[i] +
                            "\",\"albumsTitle\":\"" + albumsTitle[i] + "\",\"artistsName\":\"" + artistsName[i] +
                            "\",\"albumNumberOfSongs\":\"" + albumsNumberOfSongs[i] +"\"},");*/

                }while(cursor.moveToNext());
                cursor.close();


            }


        } catch (Exception e1) {
            Toast.makeText(context, "Album Manager Activity : " + e1.toString(), Toast.LENGTH_LONG).show();
        }
    }


  //  public List<Album> getAlbums() {
    //    return AlbumsFragment.albums;
   // }

    /*

    public long[] getAlbumId() {
        return albumsId;
    }

    public String[] getAlbumTitle() {
        return albumsTitle;
    }

    public String[] getAlbumsArt() {
        return albumsArt;
    }

    public String[] getArtistName() {
        return artistsName;
    }

    public int[] getAlbumsNumberOfSongs() {
        return albumsNumberOfSongs;
    }


    */

}
