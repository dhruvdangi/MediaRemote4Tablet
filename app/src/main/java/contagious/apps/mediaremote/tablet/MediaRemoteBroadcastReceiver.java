package contagious.apps.mediaremote.tablet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaRemoteBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getStringExtra("ACTION");

        if (action.equals("PAUSE"))
            VideoActivity.videoView.pause();
        else if (action.equals("PLAY"))
            VideoActivity.videoView.start();
    }

}