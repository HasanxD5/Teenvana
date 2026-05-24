package by.hasanxd5.teenvana.chat;

import android.content.ContentResolver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import by.hasanxd5.teenvana.R;
import io.getstream.photoview.PhotoView;
import java.io.File;
import java.util.List;

public class MediaPagerAdapter extends RecyclerView.Adapter<MediaPagerAdapter.ViewHolder> {

    private final List<String> mediaUrls;
    private final String mediaType;
    private final OnMediaClickListener listener;

    public interface OnMediaClickListener {
        void onMediaClick();
    }

    public MediaPagerAdapter(List<String> mediaUrls, String mediaType, OnMediaClickListener listener) {
        this.mediaUrls = mediaUrls;
        this.mediaType = mediaType;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_page, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mediaUrls.get(position));
    }

    @Override
    public int getItemCount() {
        return mediaUrls.size();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.resumePlayer();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.pausePlayer();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.releasePlayer();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final PhotoView imageView;
        private final PlayerView playerView;
        private final View loadingProgress;
        private ExoPlayer player;
        private String currentUrl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.full_image_view);
            playerView = itemView.findViewById(R.id.player_view);
            loadingProgress = itemView.findViewById(R.id.loading_progress);
        }

        public void bind(String url) {
            if (url.equals(currentUrl) && player != null) {
                return;
            }
            currentUrl = url;
            releasePlayer();
            imageView.setVisibility(View.GONE);
            playerView.setVisibility(View.GONE);
            loadingProgress.setVisibility(View.VISIBLE);

            boolean isVideo = isActuallyVideo(url);
            Log.d("MEDIA_PAGER", "Binding URL: " + url + " | isVideo: " + isVideo + " | globalType: " + mediaType);

            if (isVideo) {
                setupVideo(url);
            } else {
                setupImage(url);
            }
        }

        private boolean isActuallyVideo(String url) {
            if (url == null) return false;
            
            // 1. Check by extension
            String lower = url.toLowerCase();
            if (lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".webm") || 
                lower.endsWith(".3gp") || lower.endsWith(".mov") || lower.endsWith(".avi")) {
                return true;
            }

            // 2. Check by MIME type via ContentResolver (best for content:// URIs)
            if (url.startsWith("content://")) {
                try {
                    ContentResolver cr = itemView.getContext().getContentResolver();
                    String mime = cr.getType(Uri.parse(url));
                    if (mime != null && mime.startsWith("video/")) {
                        return true;
                    }
                } catch (Exception e) {
                    Log.e("MEDIA_PAGER", "MIME check error: " + e.getMessage());
                }
            }

            // 3. Fallback to global mediaType
            return "VIDEO".equals(mediaType);
        }

        private void setupImage(String url) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(itemView.getContext())
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            loadingProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loadingProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);

            imageView.setOnPhotoTapListener((view, x, y) -> {
                if (listener != null) listener.onMediaClick();
            });
        }

        private void setupVideo(String url) {
            playerView.setVisibility(View.VISIBLE);
            playerView.setUseController(true);
            
            player = new ExoPlayer.Builder(itemView.getContext()).build();
            playerView.setPlayer(player);
            
            Uri uri;
            if (url.startsWith("/") || url.startsWith("content://") || url.startsWith("file://")) {
                uri = url.startsWith("/") ? Uri.fromFile(new File(url)) : Uri.parse(url);
            } else {
                uri = Uri.parse(url);
            }

            player.setMediaItem(MediaItem.fromUri(uri));
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_READY) {
                        loadingProgress.setVisibility(View.GONE);
                    } else if (state == Player.STATE_BUFFERING) {
                        loadingProgress.setVisibility(View.VISIBLE);
                    }
                }
            });
            player.prepare();
            player.setPlayWhenReady(true);

            // Forward clicks to toggle UI
            playerView.setOnClickListener(v -> {
                if (listener != null) listener.onMediaClick();
            });
        }

        public void pausePlayer() {
            if (player != null) {
                player.setPlayWhenReady(false);
            }
        }

        public void resumePlayer() {
            if (player != null) {
                player.setPlayWhenReady(true);
            }
        }

        public void releasePlayer() {
            if (player != null) {
                player.release();
                player = null;
                playerView.setPlayer(null);
            }
        }
    }
}
