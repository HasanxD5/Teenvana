package by.hasanxd5.teenvana.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.Objects;
import by.hasanxd5.teenvana.R;

public class MediaViewerActivity extends AppCompatActivity {

    private VideoView videoView;
    private PhotoView imageView;
    private ImageButton playButton;
    private ImageButton backButton;
    private ImageButton moreButton;
    private ProgressBar progressBar;
    private boolean isUiVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_media_viewer);

        initViews();

        String mediaPath = getIntent().getStringExtra("MEDIA_PATH");
        String mediaType = getIntent().getStringExtra("MEDIA_TYPE");

        if (mediaPath == null) {
            Toast.makeText(this, "Media not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        backButton.setOnClickListener(v -> finish());
        moreButton.setOnClickListener(this::showPopupMenu);

        if ("VIDEO".equals(mediaType)) {
            setupVideo(mediaPath);
        } else {
            setupImage(mediaPath);
        }
    }

    private void initViews() {
        imageView = findViewById(R.id.full_image_view);
        videoView = findViewById(R.id.full_video_view);
        playButton = findViewById(R.id.play_button);
        backButton = findViewById(R.id.btn_back);
        moreButton = findViewById(R.id.btn_more);
        progressBar = findViewById(R.id.loading_progress);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupImage(String path) {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(path)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(MediaViewerActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        imageView.setOnTouchListener(new SwipeDismissTouchListener(this, imageView) {
            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    v.performClick();
                }
                return super.onTouch(v, event);
            }
        });

        imageView.setOnPhotoTapListener((view, x, y) -> toggleUi());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupVideo(String path) {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        videoView.setVideoURI(Uri.parse(path));

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> {
            progressBar.setVisibility(View.GONE);
            playButton.setVisibility(View.VISIBLE);
        });

        videoView.setOnTouchListener(new SwipeDismissTouchListener(this, videoView));

        playButton.setOnClickListener(v -> {
            videoView.start();
            playButton.setVisibility(View.GONE);
        });

        videoView.setOnCompletionListener(mp -> playButton.setVisibility(View.VISIBLE));
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Save");
        popupMenu.getMenu().add("Share");
        popupMenu.getMenu().add("Delete");

        popupMenu.setOnMenuItemClickListener(item -> {
            String title = Objects.requireNonNull(item.getTitle()).toString();
            String path = getIntent().getStringExtra("MEDIA_PATH");

            switch (title) {
                case "Save":
                    saveMedia();
                    return true;
                case "Share":
                    shareMedia(path);
                    return true;
                case "Delete":
                    deleteMedia(path);
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    private void shareMedia(String path) {
        if (path == null) return;
        Intent intent = new Intent(Intent.ACTION_SEND);
        String type = "VIDEO".equals(getIntent().getStringExtra("MEDIA_TYPE")) ? "video/*" : "image/*";
        intent.setType(type);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(Intent.createChooser(intent, "Share via..."));
    }

    private void saveMedia() {
        // Dummy implementation for now
        Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    private void deleteMedia(String path) {
        new AlertDialog.Builder(this)
                .setTitle("Delete?")
                .setMessage("Are you sure you want to delete this media?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("DELETED_PATH", path);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleUi() {
        float targetAlpha = isUiVisible ? 0f : 1f;

        backButton.animate().alpha(targetAlpha).setDuration(200).withStartAction(() -> {
            if (!isUiVisible) backButton.setVisibility(View.VISIBLE);
        }).withEndAction(() -> {
            if (isUiVisible) backButton.setVisibility(View.GONE);
        });

        moreButton.animate().alpha(targetAlpha).setDuration(200).withStartAction(() -> {
            if (!isUiVisible) moreButton.setVisibility(View.VISIBLE);
        }).withEndAction(() -> {
            if (isUiVisible) moreButton.setVisibility(View.GONE);
        });

        isUiVisible = !isUiVisible;
    }
}