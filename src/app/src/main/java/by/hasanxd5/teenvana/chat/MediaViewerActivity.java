package by.hasanxd5.teenvana.chat;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.VideoView;
import android.widget.MediaController;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import by.hasanxd5.teenvana.R;

public class MediaViewerActivity extends AppCompatActivity {

    private VideoView videoView;
    private ImageView imageView;
    private ImageButton playButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Делаем экран на весь дисплей
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_media_viewer);

        imageView = findViewById(R.id.full_image_view);
        videoView = findViewById(R.id.full_video_view);
        playButton = findViewById(R.id.play_button);
        ImageButton backButton = findViewById(R.id.btn_back);

        String mediaPath = getIntent().getStringExtra("MEDIA_PATH");
        String mediaType = getIntent().getStringExtra("MEDIA_TYPE");

        backButton.setOnClickListener(v -> finish());

        if ("VIDEO".equals(mediaType)) {
            setupVideo(mediaPath);
        } else {
            setupImage(mediaPath);
        }
    }

    private void setupImage(String path) {
        imageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);

        Glide.with(this)
                .load(Uri.parse(path))
                .into(imageView);
    }

    private void setupVideo(String path) {
        imageView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        playButton.setVisibility(View.VISIBLE);

        videoView.setVideoURI(Uri.parse(path));

        // Создаем контроллер (Play, Pause, Таймлайн)
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        playButton.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                playButton.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                playButton.setVisibility(View.GONE);
            }
        });

        videoView.setOnCompletionListener(mp -> {
            playButton.setImageResource(android.R.drawable.ic_media_play);
            playButton.setVisibility(View.VISIBLE);
        });
    }
}