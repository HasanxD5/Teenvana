package by.hasanxd5.teenvana.chat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.Objects;
import by.hasanxd5.teenvana.R;

public class MediaViewerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private PhotoView imageView;
    private ImageButton backButton;
    private ImageButton moreButton;
    private ProgressBar progressBar;
    private boolean isUiVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Режим без границ (Fullscreen)
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
        playerView = findViewById(R.id.player_view); // Теперь PlayerView
        backButton = findViewById(R.id.btn_back);
        moreButton = findViewById(R.id.btn_more);
        progressBar = findViewById(R.id.loading_progress);
    }

    private void setupImage(String path) {
        imageView.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(this)
                .load(path)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);

        imageView.setOnPhotoTapListener((view, x, y) -> toggleUi());
    }

    private void setupVideo(String path) {
        imageView.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        initializePlayer(path);
    }

    private void initializePlayer(String path) {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(path));
        player.setMediaItem(mediaItem);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        player.prepare();
        player.play(); // Автозапуск

        // Слушатель нажатия для скрытия кнопок поверх видео
        playerView.setOnClickListener(v -> toggleUi());
    }

    @OptIn(markerClass = UnstableApi.class)
    private void toggleUi() {
        isUiVisible = !isUiVisible;
        float targetAlpha = isUiVisible ? 1f : 0f;

        // Анимируем кнопки назад и меню
        backButton.animate().alpha(targetAlpha).setDuration(250).start();
        moreButton.animate().alpha(targetAlpha).setDuration(250).start();

        // Управляем видимостью контроллера плеера
        if (isUiVisible) {
            playerView.showController();
        } else {
            playerView.hideController();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer(); // Освобождаем память при уходе с экрана
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    // Твои методы showPopupMenu, shareMedia, deleteMedia остаются без изменений
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Save");
        popupMenu.getMenu().add("Share");
        popupMenu.getMenu().add("Delete");

        popupMenu.setOnMenuItemClickListener(item -> {
            String title = Objects.requireNonNull(item.getTitle()).toString();
            String path = getIntent().getStringExtra("MEDIA_PATH");
            switch (title) {
                case "Save": saveMedia(); return true;
                case "Share": shareMedia(path); return true;
                case "Delete": deleteMedia(path); return true;
                default: return false;
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
}