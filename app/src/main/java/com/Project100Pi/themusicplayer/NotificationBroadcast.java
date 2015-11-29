package com.Project100Pi.themusicplayer;

/**
 * Created by Goutham Srinivas on 26-09-2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;


public class NotificationBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    if(!PlayHelperFunctions.isSongPlaying){
                        PlayHelperFunctions.pauseMusicPlayer();
                    }else{
                        PlayHelperFunctions.startMusicPlayer();
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                    PlayHelperFunctions.nextAction();
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                    PlayHelperFunctions.prevAction();
                    break;
            }
        }  else{
            Log.d("NotifBroadCast.Java",intent.getAction().toString());
            if(songInfoObj.album==null &&  songInfoObj.playPath==null ) {
                UtilFunctions.loadPreference(context);
                PlayHelperFunctions.mContext=context; // mContext throws NPE lots of times
                CursorClass.mContext=context;
                Log.d("PREFERENCES", "preferences loaded from shared preferences");
            }
            if (intent.getAction().equals(LockScreenNotification.NOTIFY_PLAY)) {
                PlayHelperFunctions.startMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PAUSE)) {
                PlayHelperFunctions.pauseMusicPlayer();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_NEXT)) {
                PlayHelperFunctions.nextAction();
            } else if (intent.getAction().equals(LockScreenNotification.NOTIFY_DELETE)) {
                Intent i = new Intent(context, LockScreenNotification.class);
                context.stopService(i);
                Intent in = new Intent(context, MainActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
            }else if (intent.getAction().equals(LockScreenNotification.NOTIFY_PREVIOUS)) {
                PlayHelperFunctions.prevAction();
            }
        }
    }

    public String ComponentName() {
        return this.getClass().getName();
    }
}