package contagious.apps.mediaremote.tablet;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoActivity extends Activity {

    private String videoPath;
    public static VideoView videoView;
    public static MediaController mediaController;
    private MediaMetadataRetriever mediaMetadataRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoPath = getIntent().getStringExtra(HomeScreenActivity.VIDEO_PATH_TAG);
        videoView = (VideoView) findViewById(R.id.video_view);
        mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(videoPath);
        mediaController = new MediaController(this, false);

        String rotation = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        boolean forceLandscape = rotation.equals("90");

        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();

        // Set Orientation
        String orientation = getIntent().getStringExtra(HomeScreenActivity.ORIENTATION_TAG);
        if (forceLandscape || orientation.equals(HomeScreenActivity.PORTRAIT_TAG))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        else if (orientation.equals(HomeScreenActivity.LANDSCAPE_TAG))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onPause() {
        videoView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > 10) {
            // set UI visibility flags
            getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.INVISIBLE);
        }
        videoView.start();
    }

}
