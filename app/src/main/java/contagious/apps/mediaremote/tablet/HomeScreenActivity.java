package contagious.apps.mediaremote.tablet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeScreenActivity extends Activity {

    class VideoListItem {
        public String path;
        public String title;
        public String duration;

        public VideoListItem(String path, String title, String duration) {
            this.path = path;
            this.title = title;
            this.duration = duration;
        }
    }

    class VideoListAdapter extends ArrayAdapter<VideoListItem> {
        private class ViewHolder {
            ImageView videoThumbnail;
            TextView videoTitle;
            TextView videoDuration;
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
                viewHolder.videoThumbnail = (ImageView) convertView.findViewById(R.id.videos_list_item_thumbnail);
                viewHolder.videoTitle = (TextView) convertView.findViewById(R.id.videos_list_item_title);
                viewHolder.videoDuration = (TextView) convertView.findViewById(R.id.videos_list_item_duration);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.videoThumbnail.setImageBitmap(BitmapFactory.decodeFile(video.path));
            viewHolder.videoTitle.setText(video.title);
            viewHolder.videoDuration.setText(video.duration);

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ArrayList<VideoListItem> videos = new ArrayList<VideoListItem>();

        ////// populate the videos ArrayList //////
        ContentResolver contentResolver = getContentResolver();
        Uri videosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Video.Media._ID, MediaStore.Video.VideoColumns.DISPLAY_NAME,
                               MediaStore.Video.VideoColumns.DURATION, MediaStore.Video.VideoColumns.DATA,
                               MediaStore.Video.VideoColumns.RESOLUTION, MediaStore.Video.VideoColumns.SIZE};

        Cursor cursor = contentResolver.query(videosUri, projection, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String displayName = cursor.getString(1);
                int duration = cursor.getInt(2);
                String dataPath = cursor.getString(3);
                String resolution = cursor.getString(4);
                int size = cursor.getInt(5);

                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id, MediaStore.Video.Thumbnails.MICRO_KIND, null);
                if (thumbnail == null)
                    thumbnail = ThumbnailUtils.createVideoThumbnail(dataPath, MediaStore.Video.Thumbnails.MICRO_KIND);
            } while(cursor.moveToNext());
        }

//        String[] projection = { MediaStore.Video.Media._ID };
//        Cursor cursor = new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
//                null, null, null).loadInBackground();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.moveToNext()) {
//                StringBuilder cols = new StringBuilder();
//                for (int i = 0; i < cursor.getColumnCount(); i++)
//                    cols.append(cursor.getString(i));
//                Log.i("CursorData", cols.toString());
//            }
//        }

        VideoListAdapter adapter = new VideoListAdapter(this, videos);
        ListView videosList = (ListView) findViewById(R.id.videos_list);
        videosList.setAdapter(adapter);
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
