package by.hasanxd5.teenvana.chat;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.AppBarLayout;
import by.hasanxd5.teenvana.R;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MediaViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private AppBarLayout controlsTop;
    private TextView toolbarTitle;
    private boolean isUiVisible = true;
    private List<String> mediaUrls;
    private String mediaType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_media_viewer);

        initViews();
        applyWindowInsets();
        setupFullscreenBehavior();

        mediaUrls = getIntent().getStringArrayListExtra("MEDIA_URLS");
        if (mediaUrls == null) {
            mediaUrls = new ArrayList<>();
            String singlePath = getIntent().getStringExtra("MEDIA_PATH");
            if (singlePath != null) {
                mediaUrls.add(singlePath);
            }
        }
        
        mediaType = getIntent().getStringExtra("MEDIA_TYPE");
        int startPosition = getIntent().getIntExtra("START_POSITION", 0);

        if (mediaUrls.isEmpty()) {
            finish();
            return;
        }

        MediaPagerAdapter adapter = new MediaPagerAdapter(mediaUrls, mediaType, this::toggleUi);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(startPosition, false);

        updateTitle(startPosition);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTitle(position);
            }
        });
    }

    private void initViews() {
        viewPager = findViewById(R.id.media_view_pager);
        controlsTop = findViewById(R.id.controls_top);
        toolbarTitle = findViewById(R.id.toolbar_title);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        findViewById(R.id.btn_more).setOnClickListener(this::showMD3Menu);
    }

    private void updateTitle(int position) {
        if (mediaUrls.size() > 1) {
            toolbarTitle.setText((position + 1) + " of " + mediaUrls.size());
        } else {
            toolbarTitle.setText("");
        }
    }

    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(controlsTop, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, insets.top, 0, 0);
            return windowInsets;
        });
    }

    private void setupFullscreenBehavior() {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    private void toggleUi() {
        isUiVisible = !isUiVisible;
        if (isUiVisible) {
            controlsTop.setVisibility(View.VISIBLE);
            controlsTop.animate().alpha(1.0f).setDuration(200).start();
        } else {
            controlsTop.animate().alpha(0.0f).setDuration(200).withEndAction(() -> controlsTop.setVisibility(View.GONE)).start();
        }
        
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (isUiVisible) {
            controller.show(WindowInsetsCompat.Type.systemBars());
        } else {
            controller.hide(WindowInsetsCompat.Type.systemBars());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewPager != null) {
            viewPager.setAdapter(null);
        }
    }

    private void showMD3Menu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenu().add(0, 0, 0, "Save to Gallery");
        popup.getMenu().add(0, 1, 1, "Share");

        popup.setOnMenuItemClickListener(item -> {
            String currentUrl = mediaUrls.get(viewPager.getCurrentItem());
            if (item.getItemId() == 0) {
                saveMedia(currentUrl);
                return true;
            } else if (item.getItemId() == 1) {
                shareMedia(currentUrl);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void saveMedia(String path) {
        if (path == null) return;
        Uri sourceUri = path.startsWith("/") ? Uri.fromFile(new File(path)) : Uri.parse(path);
        String fileName = "Teenvana_" + System.currentTimeMillis();
        String mimeType = URLConnection.guessContentTypeFromName(path);
        if (mimeType == null) mimeType = "image/jpeg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Teenvana");
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri collection = (mimeType.startsWith("video"))
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Uri resultUri = getContentResolver().insert(collection, values);

        if (resultUri != null) {
            try (InputStream is = getContentResolver().openInputStream(sourceUri);
                 OutputStream os = getContentResolver().openOutputStream(resultUri)) {
                if (is != null && os != null) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }
                values.clear();
                values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                getContentResolver().update(resultUri, values, null, null);
                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareMedia(String path) {
        if (path == null) return;
        Uri uri;
        if (path.startsWith("/")) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(path));
        } else {
            uri = Uri.parse(path);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String mimeType = URLConnection.guessContentTypeFromName(path);
        shareIntent.setType(mimeType != null ? mimeType : "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Media"));
    }
}