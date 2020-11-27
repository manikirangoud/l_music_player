package player.music.notifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

import player.music.R;

public class NotificationService {

    private String songTitle, artistName, albumArt;
    private Context context;

    public NotificationService(Context context, String songTitle, String artistName, String albumArt) {
        this.songTitle = songTitle;
        this.artistName = artistName;
        this.albumArt = albumArt;
        this.context = context;
    }

    public void setNotification() {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_big_layout);

        notification.contentView = remoteViews;
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notificationManager.notify(1, notification);


    }
}
