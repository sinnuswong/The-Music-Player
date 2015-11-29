package com.Project100Pi.themusicplayer;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.media.browse.MediaBrowser;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class LockScreenNotification extends Service implements AudioManager.OnAudioFocusChangeListener{
    String LOG_CLASS = "LockScreenNotification";
    private MediaPlayer mp;
    static int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.tutorialsface.audioplayer.previous";
    public static final String NOTIFY_DELETE = "com.tutorialsface.audioplayer.delete";
    public static final String NOTIFY_PAUSE = "com.tutorialsface.audioplayer.pause";
    public static final String NOTIFY_PLAY = "com.tutorialsface.audioplayer.play";
    public static final String NOTIFY_NEXT = "com.tutorialsface.audioplayer.next";


    RemoteViews simpleContentView;
    RemoteViews expandedView;
    Notification notification;
    NotificationManager mNotificationManager;

    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    private static Timer timer;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;

    Handler layoutThreadHandler;
    static boolean shouldUpdateLayout=false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
       // mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        currentVersionSupportBigNotification = true;
        currentVersionSupportLockScreenControls = true; // I have plainly initialized it to true..have to check.;
        timer = new Timer();
        PlayHelperFunctions.mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //Controls.nextControl(getApplicationContext());
                PlayHelperFunctions.nextAction();
            }
        });
        super.onCreate();
        startLayoutThread();
    }

    /**
     * Send message from timer
     * @author jonty.ankit
     */
    private class MainTask extends TimerTask{
        public void run(){
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(mp != null){
                int progress = (mp.getCurrentPosition()*100) / mp.getDuration();
                Integer i[] = new Integer[3];
                i[0] = mp.getCurrentPosition();
                i[1] = mp.getDuration();
                i[2] = progress;
                try{
                 //   PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, i));
                }catch(Exception e){}
            }
        }
    };

    @SuppressLint("NewApi")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            Log.d(" LSNotif ONSTART","flags is "+String.valueOf(flags)+"  and start Id is "+String.valueOf(startId));
          //  MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            if(currentVersionSupportLockScreenControls){
                RegisterRemoteClient();
            }
            String songPath = songInfoObj.playPath;
           // PlayHelperFunctions.audioPlayer(songPath,songInfoObj.playerPostion);//Position has to be decided
            newNotification();
/*
            PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
                    String songPath = data.getPath();
                    newNotification();
                    try{
                        playSong(songPath, data);
                        MainActivity.changeUI();
                        AudioPlayerActivity.changeUI();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    String message = (String)msg.obj;
                    if(mp == null)
                        return false;
                    if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
                        PlayerConstants.SONG_PAUSED = false;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
                        }
                        mp.start();
                    }else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
                        PlayerConstants.SONG_PAUSED = true;
                        if(currentVersionSupportLockScreenControls){
                            remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
                        }
                        mp.pause();
                    }
                    newNotification();
                    try{
                        MainActivity.changeButton();
                        AudioPlayerActivity.changeButton();
                    }catch(Exception e){}
                    Log.d("TAG", "TAG Pressed: " + message);
                    return false;
                }
            });
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification() {
        createNotificationSubViews();
        getNotificationViews();




         // notification.flags |= Notification.FLAG_ONGOING_EVENT;
          notification.flags|=Notification.FLAG_NO_CLEAR;
         notification.defaults |= Notification.DEFAULT_LIGHTS;
          startForeground(NOTIFICATION_ID, notification);
       // stopForeground(true);
    }

    private void updateNotification() {

        Log.d("UPDATE NOTIF", " Within Update Notification Method");
        getNotificationViews();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

        }

    /**
     * Notification click listeners
     * @param view
     */
    public void setListeners(RemoteViews view) {
        Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent pause = new Intent(NOTIFY_PAUSE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notify_previous, pPrevious);

        PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notify_close, pDelete);

        PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPause, pPause);

        PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notify_next, pNext);

        PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.custom_notify_play_pause, pPlay);

    }

    private void getNotificationViews(){



        try{

            Bitmap albumArt = songInfoObj.bitmap;
            if(albumArt != null){
                notification.contentView.setImageViewBitmap(R.id.custom_notify_album_art, albumArt);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewBitmap(R.id.custom_notify_album_art, albumArt);
                }
            }/*(else{
                notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                if(currentVersionSupportBigNotification){
                    notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                }
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!PlayHelperFunctions.isSongPlaying){
            notification.contentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.play);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.play);
            }
        }else{
            notification.contentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.pause);

            if(currentVersionSupportBigNotification){
                notification.bigContentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.pause);

            }
        }

        notification.contentView.setTextViewText(R.id.custom_notify_song_name, songInfoObj.songName);
        notification.contentView.setTextViewText(R.id.custom_notify_album_name,songInfoObj.album);
        if(currentVersionSupportBigNotification){
            notification.bigContentView.setTextViewText(R.id.custom_notify_song_name, songInfoObj.songName);
            notification.bigContentView.setTextViewText(R.id.custom_notify_album_name, songInfoObj.album);
        }
    }

    private void createNotificationSubViews() {
        simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
        expandedView = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.big_notification);

        notification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.abc_switch_thumb_material)
                .setContentTitle(songInfoObj.songName).build();

        setListeners(simpleContentView);
        setListeners(expandedView);

        notification.contentView = simpleContentView;
        if(currentVersionSupportBigNotification){
            notification.bigContentView = expandedView;
        }
    }

    @Override
    public void onDestroy() {
        if(PlayHelperFunctions.mp != null){
            PlayHelperFunctions.mp.stop();

        }
        Log.d("SERVICE","I am in On destroy method for LockScreenNotification");
        stopSelf();
        super.onDestroy();
    }


    /*
    private void playSong(String songPath, MediaBrowser.MediaItem data) {
        try {
            if(currentVersionSupportLockScreenControls){
                UpdateMetadata(data);
                remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
            }
            mp.reset();
            mp.setDataSource(songPath);
            mp.prepare();
            mp.start();
            timer.scheduleAtFixedRate(new MainTask(), 0, 100);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */


    private void RegisterRemoteClient(){
        remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
        try {
            if(remoteControlClient == null) {
                audioManager.registerMediaButtonEventReceiver(remoteComponentName);
                Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                mediaButtonIntent.setComponent(remoteComponentName);
                PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
                remoteControlClient = new RemoteControlClient(mediaPendingIntent);
                audioManager.registerRemoteControlClient(remoteControlClient);
            }
            remoteControlClient.setTransportControlFlags(
                    RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
                            RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
                            RemoteControlClient.FLAG_KEY_MEDIA_STOP |
                            RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
                            RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
        }catch(Exception ex) {
        }
    }


    public void startLayoutThread(){

        layoutThreadHandler=new Handler();
        final Runnable updateNowPlayingLayoutThread = new Runnable() {
            public void run() {
                if(shouldUpdateLayout==true){
                    updateNotification();
                    shouldUpdateLayout=false;

                }
                                                  //should Understand
                    layoutThreadHandler.postDelayed(this, 100);



            }
        };


        layoutThreadHandler.postDelayed(updateNowPlayingLayoutThread,100);
    }



    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
        stopSelf();
        Log.d("On Task REmoved","Notification Id is "+String.valueOf(NOTIFICATION_ID));
        Log.d("On Task Removed","stop self already called");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {}
}