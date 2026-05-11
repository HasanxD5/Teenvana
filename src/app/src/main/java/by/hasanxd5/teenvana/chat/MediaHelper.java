package by.hasanxd5.teenvana.chat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import java.util.ArrayList;
import java.util.List;

public class MediaHelper {
    public static final String TYPE_IMAGE = "IMAGE";
    public static final String TYPE_VIDEO = "VIDEO";
    public List<String> fetchMedia(String type) {
        List<String> paths = new ArrayList<>();
        Uri collection;

        if (type.equals(TYPE_VIDEO)) {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] projection = {MediaStore.MediaColumns._ID};
        String sortOrder = MediaStore.MediaColumns.DATE_ADDED + " DESC";

        try (Cursor cursor = contentResolver.query(collection, projection, null, null, sortOrder)) {
            if (cursor != null) {
                android.util.Log.d("CHAT_DEBUG", "Real phone check. Found in DB: " + cursor.getCount());
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    Uri contentUri = android.content.ContentUris.withAppendedId(collection, id);
                    paths.add(contentUri.toString());
                }
            } else {
                android.util.Log.e("CHAT_DEBUG", "Cursor is NULL. Check permissions!");
            }
        } catch (Exception e) {
            android.util.Log.e("CHAT_DEBUG", "Query error: " + e.getMessage());
        }
        return paths;
    }

    public static final String TYPE_GIF = "GIF";

    private final ContentResolver contentResolver;

    public MediaHelper(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }
}