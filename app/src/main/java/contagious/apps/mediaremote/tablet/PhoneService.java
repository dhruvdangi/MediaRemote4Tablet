package contagious.apps.mediaremote.tablet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PhoneService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        String action = intent.getStringExtra("ACTION");

        if (action.equals("PAUSE"))
            VideoActivity.videoView.pause();
        else if (action.equals("PLAY"))
            VideoActivity.videoView.start();

        return null;
    }

}
