package player.music.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.List;

import player.music.Constants;
import player.music.MainActivity;
import player.music.R;
import player.music.extras.Preferences;
import player.music.managers.DatabaseManager;


public class PlaybackService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
                    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
                    AudioManager.OnAudioFocusChangeListener{

    private final IBinder iBinder = new LocalBinder();
    public static MediaPlayer mediaPlayer;
    private String songPath;
    private int resumePosition;
    private AudioManager audioManager;
    private boolean ongoingCall = false;
    private android.content.SharedPreferences sharedPreferences;

    public static int currentPlaylistSize, currentSongPositionFromPlaylist;

    public class LocalBinder extends Binder {
        public PlaybackService getService() {
            return PlaybackService.this;
        }
    }

    @Override
    public void onCreate() {
        try {
            sharedPreferences = getSharedPreferences(Constants.STRINGS.PREFS_START_PAGER, MODE_PRIVATE);
            callStateListener();
            registerBecomingNoisyReceiver();
            register_MediaController();
            register_SetMediaWithPath();
            register_StopService();
            register_PlayPreviousNextTrack();
            showNotification();

            currentSongPositionFromPlaylist = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION_FROM_PLAYLIST, 0);
        } catch (Exception e) {
            Log.e("ERROR_PBS_OnCreate", e.toString());
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(mediaPlayer != null) {
            mediaPlayer.release();
        }
//Request audio focus
        if(!requestAudioFocus()) {
            stopSelf();
        }
        Log.i("SERVICE", "ON START COMMAND CALLED");
        return super.onStartCommand(intent, flags, startId);
    }




//TO INITIATE MEDIA PLAYER
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            songPath = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, songPath);
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.prepareAsync();
            sendReceiverStartLyrics();
        } catch (NullPointerException ne) {
            //Log.e("INIT_MEDIA_PLAYER", e.toString());
        } catch (Exception e) {
            Log.e("INIT_MEDIA_PLAYER", e.toString());
        }


    }

//TO PLAY MEDIA IF IT IS NOT PLATING
    private void playMedia() {

        if(!mediaPlayer.isPlaying()) {
            resumeMedia();
        }
    }


//TO PAUSE MEDIA IF IT IS PLAYING
    private void pauseMedia() {
        try {
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

                Preferences.setCurrentSongPosition(sharedPreferences, mediaPlayer.getCurrentPosition());
                Preferences.setSongPath(sharedPreferences, songPath);
                Preferences.setIsPlaying(sharedPreferences, false);
                Preferences.setLyricPosition(this, MainActivity.lyricPosition);
                showNotification();
            }
        } catch (Exception e) {
            Log.e("Service-pauseMedia()", e.toString());
        }
    }


    public boolean isPlaying() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
             return true;
            }
        } catch (Exception e) {
            Log.e("Service-pauseMedia()", e.toString());
        }
        return false;
    }


    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                return mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            Log.e("Service-pauseMedia()", e.toString());
        }
        return 0;
    }


//TO RESUME MEDIA IF NOT PLAYING
    private void resumeMedia() {
        if(!mediaPlayer.isPlaying()) {
            songPath = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, songPath);
            resumePosition = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION, 0);
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
            Preferences.setIsPlaying(sharedPreferences, true);
            showNotification();
            updateUserMediaControls();
            updateNowPlayingControls();
        }
    }

    private void updateNowPlayingControls(){
        sendBroadcast(new Intent(Constants.ACTIONS.UPDATE_NOW_PLAYING_CONTROLS));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {

    }

//SONG ON COMPLETION THIS METHOD IS CALLED
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        try {
//CALLING TO THE UPDATE UI CONTROLS IN MAIN ACTIVITY
            mediaPlayer.stop();
            mediaPlayer.release();
            callUpdateSharedPreferencesSongCompleted();


            String repeatTag = Preferences.getRepeatTag(this);
            switch ( repeatTag ) {

                case Constants.STRINGS.REPEAT_OFF :
                    break;

                case Constants.STRINGS.REPEAT_ONE :

                    DatabaseManager.playTrackAtPosition(this,
                            (currentSongPositionFromPlaylist));
                    break;

                case Constants.STRINGS.REPEAT_ALL :

                    DatabaseManager.playTrackAtPosition(this,
                            (currentSongPositionFromPlaylist + 1));
                    break;
            }
        } catch (Exception e) {
            Log.e("MP_OnCompletion", e.toString());
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK :
                Log.e("Media Player Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_SERVER_DIED :
                Log.e("Media Player Error", "MEDIA ERROR SERVER DIED" + extra);
                break;

            case MediaPlayer.MEDIA_ERROR_UNKNOWN :
                Log.e("Media Player Error", "MEDIA ERROR UNKNOWN" + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

        try {
            switch (focusChange) {

                case AudioManager.AUDIOFOCUS_GAIN:

                    //RESUME PLAYBACK WEN THE AUDIO FOCUS GAINED
                    if (mediaPlayer != null) {

                        if (!mediaPlayer.isPlaying()) {
                            MainActivity.isPlaying = true;
                            Preferences.setIsPlaying(sharedPreferences, MainActivity.isPlaying);
                            resumeMedia();
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
                            sendBroadcast(intent);
                        } else  {
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        }
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS:

                    //Lost focus for an unbounded amount of time:
                    // stop playback and release media player
                    if(mediaPlayer != null && mediaPlayer.isPlaying() ) {

                            MainActivity.isPlaying = false;
                            Preferences.setIsPlaying(sharedPreferences, MainActivity.isPlaying);
                            pauseMedia();
                            Intent intent = new Intent();
                            intent.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
                            sendBroadcast(intent);
                    }
                   // removeAudioFocus();
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    // Lost focus for a short time, but we have to stop
                    // playback. We don't release the media player because playback
                    // is likely to resume
                    if ( ( mediaPlayer != null ) && ( mediaPlayer.isPlaying() ) ) {
                        mediaPlayer.pause();
                    }
                    break;

                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    // Lost focus for a short time, but it's ok to keep playing
                    // at an attenuated level
                    if ( mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.3f, 0.3f);
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("ERROR_OnAudioFocusChang", e.toString());
        }
    }

    private boolean requestAudioFocus() {
        audioManager = ( AudioManager ) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    private boolean removeAudioFocus() {
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == audioManager.abandonAudioFocus(this);
    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    //PAUSE AUDIO ON  ACTION_AUDIO_BECOMING_NOISY
    private BroadcastReceiver BecomingNoisyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ( mediaPlayer != null && mediaPlayer.isPlaying() ){
               pauseMedia();
            }
        }
    };


//REGISTER AFTER GETTING THE AUDIO FOCUS
    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(BecomingNoisyReceiver, intentFilter);
    }

//UPDATE PLAYBACK WITH ACTIONS BROADCAST RECEIVER
    private BroadcastReceiver MediaController = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS)) {
                boolean isSongCompleted = sharedPreferences.getBoolean(Constants.STRINGS.IS_SONG_COMPLETED, false);
                boolean isPlaying = sharedPreferences.getBoolean(Constants.STRINGS.IS_PLAYING, false);
                try {
                    if (mediaPlayer.isPlaying()) {
                        pauseMedia();
                        MainActivity.isPlaying = false;
                        Preferences.setIsPlaying(sharedPreferences, MainActivity.isPlaying);
                        showNotification();
                        updateUserMediaControls();
                    } else if (!mediaPlayer.isPlaying() && !isSongCompleted) {
                        resumeMedia();
                        MainActivity.isPlaying = true;
                        Preferences.setIsPlaying(sharedPreferences, MainActivity.isPlaying);
                        showNotification();
                        updateUserMediaControls();
                    } else if (isSongCompleted && !isPlaying) {
                        exceptionHandlingPlay();
                    }
                } catch (NullPointerException e) {
                    exceptionHandlingPlay();
                } catch (IllegalStateException e) {
                    exceptionHandlingPlay();
                } catch (Exception e) {
                    Log.e("Bottom Action Clicked", e.toString());
                }
            }
        }
    };
//REGISTERING THE MEDIA CONTROLLERS BROADCAST RECEIVER
    private void register_MediaController() {
        IntentFilter intentPause = new IntentFilter(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS);
        registerReceiver(MediaController, intentPause);
    }

//SENDING THE BROADCAST RECEIVER ACTIONS TO THE MAIN ACTIVITY WHICH IS USER UI MEDIA CONTROLS
    private void updateUserMediaControls() {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        sendBroadcast(intent);
    }

    private void sendReceiverStartLyrics() {
        try{

            sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));
        } catch (Exception e) {
            Log.i("Service-Start Lyrics", e.toString());
        }
    }

//EXCEPTION HANDLING WHILE THE ACTIONS RECEIVED FROM ANY MEDIA CONTROLS : PLAYING THE SONG
    private void exceptionHandlingPlay() {
        songPath = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, songPath);
        resumePosition = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION, 0);
        Preferences.setCurrentSongPosition(sharedPreferences, resumePosition);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            initMediaPlayer();
        } else {
            initMediaPlayer();
        }
        MainActivity.isPlaying = true;
        Preferences.setIsPlaying(sharedPreferences, MainActivity.isPlaying);
        MainActivity.isSongCompleted = false;
        Preferences.setIsSongCompleted(sharedPreferences, MainActivity.isSongCompleted);
        showNotification();
        updateUserMediaControls();

    }


//SETTING MEDIA TO MEDIA PLAYER BROADCAST RECEIVER WITH PAT AND UPDATING THE SHARED PREFERENCES
    private BroadcastReceiver SetMediaWithPath = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            songPath = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_PATH, songPath);
            Preferences.setCurrentSongPosition(sharedPreferences, 0);
            currentPlaylistSize = sharedPreferences.getInt(Constants.STRINGS.CURRENT_PLAYLIST_SIZE, 0);
            currentSongPositionFromPlaylist = sharedPreferences.getInt(Constants.STRINGS.CURRENT_SONG_POSITION_FROM_PLAYLIST, 0);
            Preferences.setLyricPosition(PlaybackService.this, 0);
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            if(action.equalsIgnoreCase(Constants.ACTIONS.SET_MEDIA_PATH)) {
                if (songPath != null && !songPath.equals("")) {
                    initMediaPlayer();
                }
            }
           // showNotification();
        }
    };


//REGISTERING THE SET MEDIA WITH PATH RECEIVER
    private void register_SetMediaWithPath() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTIONS.SET_MEDIA_PATH);
        registerReceiver(SetMediaWithPath, intentFilter);
    }


//CALL STATE LISTENER FOR THE MUSIC
    private void callStateListener() {

        PhoneStateListener phoneStateListener;
        TelephonyManager telephonyManager;
// Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
//if at least one call exists or the phone is ringing
//pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
// Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
// Register the listener with the telephony manager
// Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

//ON DESTROYED OF MUSIC PLAYING SERVICE


//SETTING THE SONG DETAILS IN SHARED PREFERENCES
    public void setSharedPreferences() {
        try {
            if (mediaPlayer != null) {
                if (MainActivity.isSongCompleted) {
                    Preferences.setCurrentSongPosition(sharedPreferences, 0);
                    Preferences.setIsSongCompleted(sharedPreferences, true);

                } else {
                    try {
                        Preferences.setCurrentSongPosition(sharedPreferences,
                                mediaPlayer.getCurrentPosition());
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        Log.e("ERR_PLAYBACK_SERVICE", "Error at set shared preferences\n" + e.toString());
                    }
                }
                Preferences.setSongPath(sharedPreferences, songPath);
                Preferences.setIsPlaying(sharedPreferences, false);
                showNotification();
            }
        } catch (Exception e) {
            Log.e("ERR_PLAYBACK_SERVICE", "Error at set shared preferences\n" + e.toString());
        }
    }

//SETTING THE MEDIA CONTROLLERS WHILE THE SONG PLAYING IS COMPLETED
    public void callUpdateSharedPreferencesSongCompleted() {
        MainActivity.isPlaying = false;
        MainActivity.isSongCompleted = true;
        MainActivity.currentSongPosition = 0;
        MainActivity.currentSongPath = songPath;
        Preferences.setLyricPosition(PlaybackService.this, 0);
        Preferences.setIsPlaying(sharedPreferences, false);
        Preferences.setIsSongCompleted(sharedPreferences, true);
        Preferences.setCurrentSongPosition(sharedPreferences, 0);
        Preferences.setSongPath(sharedPreferences, songPath);

//UPDATING FOR MEDIA CONTROLS
        Intent intent = new Intent();
        intent.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
        sendBroadcast(intent);
        showNotification();
    }

//SHOWING THE NOTIFICATION TO TE USER TO CONTROL MEDIA
    NotificationManager notificationManager;

    public void showNotification () {

        Notification notification;
        final RemoteViews remoteViewsSmall, remoteViewsBig;
        try {
            remoteViewsSmall = new RemoteViews(getPackageName(), R.layout.notification_small_layout);
            remoteViewsBig = new RemoteViews(getPackageName(), R.layout.notification_big_layout);

            String albumArt = sharedPreferences.getString(Constants.STRINGS.CURRENT_ALBUM_ART, null);
            String songTitle = sharedPreferences.getString(Constants.STRINGS.CURRENT_SONG_TITLE, null);
            String artistName = sharedPreferences.getString(Constants.STRINGS.CURRENT_ARTIST_NAME, null);
            boolean isPlaying = sharedPreferences.getBoolean(Constants.STRINGS.IS_PLAYING, false);

            remoteViewsBig.setTextViewText(R.id.text_view_song_title_notification_big_layout, songTitle);
            remoteViewsBig.setTextViewText(R.id.text_view_artist_name_notification_big_layout, artistName);
            remoteViewsSmall.setTextViewText(R.id.mtv_track_title, songTitle);
            remoteViewsSmall.setTextViewText(R.id.mtv_track_artist, artistName);
            if (isPlaying) {
                remoteViewsBig.setImageViewResource(R.id.image_view_play_pause_notification_big_layout, R.drawable.icon_pause);
                remoteViewsSmall.setImageViewResource(R.id.iv_play_pause, R.drawable.icon_pause);
            } else {
                remoteViewsBig.setImageViewResource(R.id.image_view_play_pause_notification_big_layout, R.drawable.icon_play);
                remoteViewsSmall.setImageViewResource(R.id.iv_play_pause, R.drawable.icon_play);
            }
            try {
                if(!albumArt.equalsIgnoreCase("null")) {
                    remoteViewsBig.setImageViewUri(R.id.image_view_album_notification_big_layout, Uri.parse(albumArt));
                    remoteViewsSmall.setImageViewUri(R.id.iv_album_art, Uri.parse(albumArt));
                } else {
                    remoteViewsBig.setImageViewResource(R.id.image_view_album_notification_big_layout, R.drawable.album_icon);
                    remoteViewsSmall.setImageViewResource(R.id.iv_album_art, R.drawable.album_icon);
                }
            } catch (Exception e) {
                remoteViewsBig.setImageViewResource(R.id.image_view_album_notification_big_layout, R.drawable.album_icon);
                remoteViewsSmall.setImageViewResource(R.id.iv_album_art, R.drawable.album_icon);
            }

            Intent intent = new Intent();
            intent.setAction(Constants.ACTIONS.UPDATE_PLAYBACK_WITH_ACTIONS);
            PendingIntent pendingIntentPlayPauseAction = PendingIntent.getBroadcast(this, 0, intent, 0);
            remoteViewsBig.setOnClickPendingIntent(R.id.image_view_play_pause_notification_big_layout, pendingIntentPlayPauseAction);
            remoteViewsSmall.setOnClickPendingIntent(R.id.iv_play_pause, pendingIntentPlayPauseAction);

            Intent intentStopService = new Intent();
            intentStopService.setAction(Constants.ACTIONS.STOP_SERVICE);
            PendingIntent pendingIntentStopService = PendingIntent.getBroadcast(this, 0, intentStopService, 0);
            remoteViewsBig.setOnClickPendingIntent(R.id.image_button_service_stop_big_notification, pendingIntentStopService);
            remoteViewsSmall.setOnClickPendingIntent(R.id.iv_stop_service, pendingIntentStopService);

            Intent intentPreviousTrack = new Intent();
            intentPreviousTrack.setAction(Constants.ACTIONS.PLAY_PREVIOUS);
            PendingIntent pendingIntentPreviousTrack = PendingIntent.getBroadcast(this, 0, intentPreviousTrack, 0);
            remoteViewsBig.setOnClickPendingIntent(R.id.image_view_previous_track_notification_big_layout, pendingIntentPreviousTrack);
            remoteViewsSmall.setOnClickPendingIntent(R.id.iv_previous_track, pendingIntentPreviousTrack);


            Intent intentNextTrack = new Intent();
            intentNextTrack.setAction(Constants.ACTIONS.PLAY_NEXT);
            PendingIntent pendingIntentNextTrack = PendingIntent.getBroadcast(this, 0, intentNextTrack, 0);
            remoteViewsBig.setOnClickPendingIntent(R.id.image_view_next_track_notification_big_layout, pendingIntentNextTrack);
            remoteViewsSmall.setOnClickPendingIntent(R.id.iv_next_track, pendingIntentNextTrack);


//SETTING THE NOTIFICATION DEFAULT PROPERTIES

            String CHANNEL_ID = "my_channel_01";// The id of the channel.
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.


            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                mNotificationManager.createNotificationChannel(mChannel);
            }



            notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setCustomContentView(remoteViewsSmall)
                    .setCustomBigContentView(remoteViewsBig)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .build();
           /* notification.contentView = remoteViewsSmall;
            notification.bigContentView = remoteViewsBig;
            //notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.icon = R.mipmap.ic_launcher;
*/
//CHECKING FOR THE LOLLIPOP VERSION TO ENABLE PUBLIC NOTIFICATION
           /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                notification.visibility = Notification.VISIBILITY_PUBLIC;
            }*/



//SETTING THE PENDING INTENT AS MAIN ACTIVITY TO NAVIGATE TO THE USER WEN HE CLICKS ON NOTIFICATION


            if (!getStatusOfMainActivity()) {
                Intent intentNotification = new Intent(this, MainActivity.class);
                intentNotification.setAction(Constants.NOTIFICATION_ACTION.MAIN_ACTION);
                intentNotification.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                notification.contentIntent = PendingIntent.getActivity(this, 0, intentNotification, 0);
            }
//BUILDING THE NOTIFICATION  TO THE USER

            notificationManager.notify(1, notification);



        } catch (Exception e) {
            Log.e("ERROR_NOTIFICATION", e.toString());
        }

    }

//GETTING THE STATUS OF MAIN ACTIVITY THAT IT IS ACTIVE OR NOT
    private boolean getStatusOfMainActivity() {
        final ActivityManager activityManager = ( ActivityManager ) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTaskInfos = activityManager.getRecentTasks(1024, 0);
        String packageName = getPackageName();

        if(!recentTaskInfos.isEmpty()) {
            for(ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfos) {
                if(packageName.equals(recentTaskInfo.baseIntent.getComponent().getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

//CALLING THE STOP SERVICE
    private BroadcastReceiver StopService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equalsIgnoreCase(Constants.ACTIONS.STOP_SERVICE)) {
                setSharedPreferences();
                Intent intentUpdateUserControls = new Intent();
                intentUpdateUserControls.setAction(Constants.ACTIONS.UPDATE_BOTTOM_CONTROLS);
                sendBroadcast(intentUpdateUserControls);
                notificationManager.cancel(1);
                if(mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                stopSelf();
            }
        }
    };
//REGISTERING THE STOP SERVICE BROADCAST RECEIVER
    private void register_StopService() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTIONS.STOP_SERVICE);
        registerReceiver(StopService, intentFilter);
    }

 //BROADCAST RECEIVER FOR PLAYING NEXT TRACK AND PREVIOUS TRACK
    public BroadcastReceiver PlayPreviousNextTrack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase(Constants.ACTIONS.PLAY_PREVIOUS)) {
                DatabaseManager.playTrackAtPosition(PlaybackService.this,
                        (currentSongPositionFromPlaylist - 1));
            } else if(action.equalsIgnoreCase(Constants.ACTIONS.PLAY_NEXT)) {
                DatabaseManager.playTrackAtPosition(PlaybackService.this,
                        (currentSongPositionFromPlaylist + 1));
            }
            sendBroadcast(new Intent().setAction(Constants.ACTIONS.START_LYRICS));

        }
    };
    private void register_PlayPreviousNextTrack() {
        registerReceiver(PlayPreviousNextTrack, new IntentFilter(Constants.ACTIONS.PLAY_PREVIOUS));
        registerReceiver(PlayPreviousNextTrack, new IntentFilter(Constants.ACTIONS.PLAY_NEXT));
    }


    @Override
    public void onDestroy() {
        try {
            Log.i("PlaybackService", "On Destroy method called");
            setSharedPreferences();
            unregisterReceiver(BecomingNoisyReceiver);
            unregisterReceiver(MediaController);
            unregisterReceiver(SetMediaWithPath);
            unregisterReceiver(StopService);
            unregisterReceiver(PlayPreviousNextTrack);
            if( mediaPlayer != null) {
                mediaPlayer.release();
            }
        } catch (Exception e) {
            Log.e("Service_OnDestroy", e.toString());
        }
        super.onDestroy();
    }
}
