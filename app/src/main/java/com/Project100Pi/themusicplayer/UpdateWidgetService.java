package com.Project100Pi.themusicplayer;

/**
 * Created by Goutham Srinivas on 05-10-2015.
 */
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
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

public class UpdateWidgetService extends Service implements   AudioManager.OnAudioFocusChangeListener{

    String LOG_CLASS = "LockScreenNotification";
    private MediaPlayer mp;
    int NOTIFICATION_ID = 1111;
    public static final String NOTIFY_PREVIOUS = "com.tutorialsface.audioplayer.previous";
    public static final String NOTIFY_DELETE = "com.tutorialsface.audioplayer.delete";
    public static final String NOTIFY_PAUSE = "com.tutorialsface.audioplayer.pause";
    public static final String NOTIFY_PLAY = "com.tutorialsface.audioplayer.play";
    public static final String NOTIFY_NEXT = "com.tutorialsface.audioplayer.next";

    private ComponentName remoteComponentName;
    private RemoteControlClient remoteControlClient;
    AudioManager audioManager;
    Bitmap mDummyAlbumArt;
    private static Timer timer;
    private static boolean currentVersionSupportBigNotification = false;
    private static boolean currentVersionSupportLockScreenControls = false;



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // mp = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

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

            //  MediaItem data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
            if(currentVersionSupportLockScreenControls){
                RegisterRemoteClient();
            }
            String songPath = songInfoObj.playPath;
       /*     try {
                //PlayHelperFunctions.audioPlayer(songPath,0);//Position has to be decided
            } catch (IOException e1) {
                e1.printStackTrace();
            }*/
            newNotification(intent);



            // super.onStart(intent, startId);

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
        return START_STICKY;
    }

    /**
     * Notification
     * Custom Bignotification is available from API 16
     */
    @SuppressLint("NewApi")
    private void newNotification(Intent intent) {
        String songName = songInfoObj.songName;
        String albumName = songInfoObj.album;
        RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification);
        RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

       

        setListeners(simpleContentView);
        setListeners(expandedView);


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
                .getApplicationContext());

        int[] allWidgetIds = intent
                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

//    ComponentName thisWidget = new ComponentName(getApplicationContext(),
//        MyWidgetProvider.class);
//    int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            // create some random data
            //  int number = (new Random().nextInt(100));


            Log.w("WidgetExample", "Widget ID is "+widgetId);
            // Set the text


            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(),
                    MyWidgetProvider.class);

            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds);

            appWidgetManager.updateAppWidget(widgetId, expandedView);
        }





        simpleContentView = simpleContentView;
        if(currentVersionSupportBigNotification){
            expandedView = expandedView;
        }

        try{

            Bitmap albumArt = songInfoObj.bitmap;
            if(albumArt != null){
                simpleContentView.setImageViewBitmap(R.id.custom_notify_album_art, albumArt);
                if(currentVersionSupportBigNotification){
                    expandedView.setImageViewBitmap(R.id.custom_notify_album_art, albumArt);
                }
            }/*(else{
                simpleContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                if(currentVersionSupportBigNotification){
                    expandedView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.default_album_art);
                }
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        if(!PlayHelperFunctions.isSongPlaying){
            simpleContentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.play);

            if(currentVersionSupportBigNotification){
                expandedView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.play);

            }
        }else{
            simpleContentView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.pause);

            if(currentVersionSupportBigNotification){
                expandedView.setImageViewResource(R.id.custom_notify_play_pause, R.drawable.pause);
            }
        }

        simpleContentView.setTextViewText(R.id.custom_notify_song_name, songName);
        simpleContentView.setTextViewText(R.id.custom_notify_album_name, albumName);
        if(currentVersionSupportBigNotification){
            expandedView.setTextViewText(R.id.custom_notify_song_name, songName);
            expandedView.setTextViewText(R.id.custom_notify_album_name, albumName);
        }
      //  notification.flags |= Notification.FLAG_ONGOING_EVENT;
      //  startForeground(NOTIFICATION_ID, notification);
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

    @Override
    public void onDestroy() {
        if(mp != null){
            mp.stop();
            mp = null;
        }
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


    @Override
    public void onAudioFocusChange(int focusChange) {}
}
