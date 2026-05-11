package by.hasanxd5.teenvana.chat;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
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
import io.getstream.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import by.hasanxd5.teenvana.R;

public class MediaViewerActivity extends AppCompatActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private PhotoView imageView;
    private AppBarLayout controlsTop;
    private View loadingProgress;
    private boolean isUiVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_media_viewer);

        initViews();
        applyWindowInsets();
        setupFullscreenBehavior();

        String path = getIntent().getStringExtra("MEDIA_PATH");
        String type = getIntent().getStringExtra("MEDIA_TYPE");

        if (path == null) {
            finish();
            return;
        }

        if ("VIDEO".equals(type)) {
            setupVideo(path);
        } else {
            setupImage(path);
        }
    }

    private void initViews() {
        imageView = findViewById(R.id.full_image_view);
        playerView = findViewById(R.id.player_view);
        loadingProgress = findViewById(R.id.loading_progress);
        controlsTop = findViewById(R.id.controls_top);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        View moreButton = findViewById(R.id.btn_more);
        if (moreButton != null) {
            moreButton.setOnClickListener(this::showMD3Menu);
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

    private void setupImage(String path) {
        imageView.setVisibility(View.VISIBLE);
        loadingProgress.setVisibility(View.VISIBLE);

        Glide.with(this).load(path).listener(new RequestListener<>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object m, @NonNull Target<Drawable> t, boolean f) {
                loadingProgress.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(@NonNull Drawable r, @NonNull Object m, Target<Drawable> t, @NonNull DataSource d, boolean f) {
                loadingProgress.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);

        imageView.setOnPhotoTapListener((view, x, y) -> toggleUi());
    }

    private void setupVideo(String path) {
        playerView.setVisibility(View.VISIBLE);
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(Uri.parse(path)));

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    loadingProgress.setVisibility(View.GONE);
                }
            }
        });

        player.prepare();
        player.play();

        playerView.setControllerVisibilityListener((PlayerView.ControllerVisibilityListener) visibility -> {
            isUiVisible = (visibility == View.VISIBLE);
            updateCustomControls();
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void toggleUi() {
        if (player != null) {
            if (playerView.isControllerFullyVisible()) {
                playerView.hideController();
            } else {
                playerView.showController();
            }
        } else {
            isUiVisible = !isUiVisible;
            updateCustomControls();
        }
    }

    private void updateCustomControls() {
        float alpha = isUiVisible ? 1f : 0f;

        controlsTop.animate()
                .alpha(alpha)
                .setDuration(250)
                .withStartAction(() -> {
                    if (isUiVisible) controlsTop.setVisibility(View.VISIBLE);
                })
                .withEndAction(() -> {
                    if (!isUiVisible) controlsTop.setVisibility(View.GONE);
                })
                .start();
    }

    private void showMD3Menu(View v) {
        androidx.appcompat.widget.PopupMenu popup = new androidx.appcompat.widget.PopupMenu(this, v);

        popup.getMenu().add(0, 0, 0, "Save");
        popup.getMenu().add(0, 1, 1, "Share");
        popup.getMenu().add(0, 2, 2, "Delete");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    saveMedia();
                    return true;
                case 1:
                    shareMedia();
                    return true;
                case 2:
                    deleteMedia();
                    return true;
                default:
                    return false;
            }
        });

        popup.show();
    }

    private void deleteMedia() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete media?")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Delete", (d, w) -> {
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveMedia() {
        Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    private void shareMedia() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}