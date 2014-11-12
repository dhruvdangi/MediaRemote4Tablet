package contagious.apps.mediaremote.tablet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class HomeScreenActivity extends Activity {

    class VideoListItem {
        public String displayName;
        public String duration;
        public String dataPath;
        public String resolution;
        public String size;
        public Bitmap thumbnail;

        public VideoListItem(String displayName, String duration, String dataPath, String resolution, String size, Bitmap thumbnail) {
            this.displayName = displayName;
            this.duration = duration;
            this.dataPath = dataPath;
            this.resolution = resolution;
            this.size = size;
            this.thumbnail = thumbnail;
        }
    }

    class VideoListAdapter extends ArrayAdapter<VideoListItem> {
        private class ViewHolder {
            ImageView videoThumbnail;
            TextView videoTitle;
            TextView videoDuration;
            TextView videoSize;
        }

        public VideoListAdapter(Context context, ArrayList<VideoListItem> videos) {
            super(context, 0, videos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // get video at position
            VideoListItem video = getItem(position);

            // create an item if not being reused
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflator = LayoutInflater.from(getContext());
                convertView = inflator.inflate(R.layout.videos_list_item, parent, false);
                convertView.setTag(viewHolder);
                viewHolder.videoThumbnail = (ImageView) convertView.findViewById(R.id.videos_list_item_thumbnail);
                viewHolder.videoTitle = (TextView) convertView.findViewById(R.id.videos_list_item_title);
                viewHolder.videoDuration = (TextView) convertView.findViewById(R.id.videos_list_item_duration);
                viewHolder.videoSize = (TextView) convertView.findViewById(R.id.videos_list_item_size);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.videoThumbnail.setImageBitmap(video.thumbnail);
            viewHolder.videoTitle.setText(video.displayName);
            viewHolder.videoDuration.setText(video.duration);
            viewHolder.videoSize.setText(video.size);
            return convertView;
        }
    }

    public static String VIDEO_PATH_TAG = "video_path";
    public static String ORIENTATION_TAG = "orientation";
    public static String LANDSCAPE_TAG = "landscape";
    public static String PORTRAIT_TAG = "portrait";

    private ArrayList<VideoListItem> videoArrayList;
    private ListView videosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        videoArrayList = new ArrayList<VideoListItem>();

        ////// populate the videos ArrayList //////
        ContentResolver contentResolver = getContentResolver();
        final Uri videosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DISPLAY_NAME,
                               MediaStore.Video.VideoColumns.DURATION, MediaStore.Video.VideoColumns.DATA,
                               MediaStore.Video.VideoColumns.RESOLUTION, MediaStore.Video.VideoColumns.SIZE};

        Cursor cursor = contentResolver.query(videosUri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String displayName = cursor.getString(1);
                String duration = timeFormatter(cursor.getInt(2));
                String dataPath = cursor.getString(3);
                String resolution = cursor.getString(4);
                String size = humanReadableByteCount(cursor.getInt(5));

                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MINI_KIND, null);
                if (thumbnail == null)
                    thumbnail = ThumbnailUtils.createVideoThumbnail(dataPath, MediaStore.Video.Thumbnails.MINI_KIND);

                VideoListItem videoListItem = new VideoListItem(displayName, duration, dataPath, resolution, size, thumbnail);
                videoArrayList.add(videoListItem);
            } while(cursor.moveToNext());
        }

        VideoListAdapter adapter = new VideoListAdapter(this, videoArrayList);
        videosList = (ListView) findViewById(R.id.videos_list);
        videosList.setAdapter(adapter);
        videosList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), VideoActivity.class);
                intent.putExtra(VIDEO_PATH_TAG, videoArrayList.get(position).dataPath);
                intent.putExtra(ORIENTATION_TAG, getOrientation(videoArrayList.get(position).resolution));
                startActivity(intent);
            }
        });
    }

    private String getOrientation(String resolution) {
        int xIndex = resolution.indexOf("x");
        double width = Double.parseDouble(resolution.substring(0, xIndex));
        double height = Double.parseDouble(resolution.substring(xIndex + 1));
        return width / height > 1 ? LANDSCAPE_TAG : PORTRAIT_TAG;
    }

    private String timeFormatter(int milliseconds) {
        int seconds = milliseconds / 1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".substring(exp-1, exp);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
