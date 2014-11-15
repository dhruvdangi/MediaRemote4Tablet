package contagious.apps.mediaremote.tablet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by dhruvdangi on 15/11/14.
 */
public class PhoneService extends Service{
    @Override
    public IBinder onBind(Intent intent) {

        String ACTION = intent.getStringExtra("ACTION");
        if(ACTION.equals("PAUSE"))
        {
            VideoActivity.videoView.pause();
        }
        else if(ACTION.equals("PLAY"))
        {
            VideoActivity.videoView.start();
        }
        return null;

    }
}
