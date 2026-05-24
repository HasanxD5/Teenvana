package by.hasanxd5.teenvana.chat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.UserProfileActivity;
import by.hasanxd5.teenvana.models.Message;
import by.hasanxd5.teenvana.models.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<Message> messages;
    private List<Message> messagesFull;
    private final String currentUserId;
    private OnMessageActionListener actionListener;
    private User otherUser;
    
    private boolean selectionMode = false;
    private final Set<String> selectedMessageIds = new HashSet<>();

    public interface OnMessageActionListener {
        void onMessageLongClick(Message message, int position);
        void onSelectionChanged(int count);
        void onShowSingleContextMenu(View view, Message message, int position);
    }

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.messagesFull = new ArrayList<>(messages);
        this.currentUserId = currentUserId;
    }

    public void setOtherUser(User otherUser) {
        this.otherUser = otherUser;
    }

    public void setOnMessageActionListener(OnMessageActionListener listener) {
        this.actionListener = listener;
    }

    public void updateList(List<Message> newList) {
        this.messagesFull = new ArrayList<>(newList);
        filter(""); // Reset filter
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        messages.clear();
        if (query.isEmpty()) {
            messages.addAll(messagesFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Message message : messagesFull) {
                if (message.getText() != null && message.getText().toLowerCase().contains(lowerCaseQuery)) {
                    messages.add(message);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectionMode(boolean enabled) {
        this.selectionMode = enabled;
        if (!enabled) {
            selectedMessageIds.clear();
        }
        notifyDataSetChanged();
    }

    public void toggleSelection(String messageId) {
        if (selectedMessageIds.contains(messageId)) {
            selectedMessageIds.remove(messageId);
        } else {
            selectedMessageIds.add(messageId);
        }
        notifyDataSetChanged();
        if (actionListener != null) {
            actionListener.onSelectionChanged(selectedMessageIds.size());
        }
    }

    public List<Message> getSelectedMessages() {
        List<Message> selected = new ArrayList<>();
        for (Message msg : messagesFull) {
            if (selectedMessageIds.contains(msg.getId())) {
                selected.add(msg);
            }
        }
        return selected;
    }

    @Override
    public int getItemViewType(int position) {
        if (Objects.equals(messages.get(position).getSenderId(), currentUserId)) {
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_SENT:
                View sentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(sentView);
            case TYPE_RECEIVED:
            default:
                View receivedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(receivedView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean isSelected = selectedMessageIds.contains(message.getId());
        
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message, isSelected, this, actionListener, position);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message, isSelected, this, actionListener, position, otherUser);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private static String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Helper for double-tap detection
    private static class DoubleTapHandler {
        private static final long DOUBLE_TAP_TIMEOUT = 300;
        private long lastTapTime = 0;
        private final Handler handler = new Handler(Looper.getMainLooper());
        private Runnable singleTapRunnable;

        public void handleTap(Runnable onSingleTap, Runnable onDoubleTap) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                handler.removeCallbacks(singleTapRunnable);
                onDoubleTap.run();
                lastTapTime = 0;
            } else {
                lastTapTime = currentTime;
                singleTapRunnable = onSingleTap;
                handler.postDelayed(singleTapRunnable, DOUBLE_TAP_TIMEOUT);
            }
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewTime;
        private final ImageView messageImageView;
        private final View gridLayoutCollage;
        private final ImageView[] collageImgs;
        private final View collageImg4Container;
        private final TextView textViewMore;
        private final View selectionOverlay;
        private final DoubleTapHandler doubleTapHandler = new DoubleTapHandler();

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            gridLayoutCollage = itemView.findViewById(R.id.gridLayoutCollage);
            collageImgs = new ImageView[]{
                    itemView.findViewById(R.id.collageImg1),
                    itemView.findViewById(R.id.collageImg2),
                    itemView.findViewById(R.id.collageImg3),
                    itemView.findViewById(R.id.collageImg4)
            };
            collageImg4Container = itemView.findViewById(R.id.collageImg4Container);
            textViewMore = itemView.findViewById(R.id.textViewMore);
            
            selectionOverlay = new View(itemView.getContext());
            selectionOverlay.setBackgroundColor(Color.parseColor("#402A86FF"));
            selectionOverlay.setVisibility(View.GONE);
            ((ViewGroup) itemView).addView(selectionOverlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        public void bind(Message message, boolean isSelected, MessageAdapter adapter, OnMessageActionListener listener, int position) {
            String type = message.getType();
            textViewMessage.setVisibility(View.GONE);
            messageImageView.setVisibility(View.GONE);
            gridLayoutCollage.setVisibility(View.GONE);
            selectionOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            if (Message.TYPE_IMAGE.equals(type) || Message.TYPE_VIDEO.equals(type) || Message.TYPE_GIF.equals(type)) {
                messageImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .centerCrop()
                        .into(messageImageView);

                messageImageView.setOnClickListener(v -> {
                    if (adapter.isSelectionMode()) {
                        adapter.toggleSelection(message.getId());
                    } else {
                        doubleTapHandler.handleTap(
                            () -> openMediaViewer(message.getUrl(), type, null, 0),
                            () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                        );
                    }
                });
            } else if (Message.TYPE_COLLAGE.equals(type) && message.getMediaUrls() != null && !message.getMediaUrls().isEmpty()) {
                gridLayoutCollage.setVisibility(View.VISIBLE);
                List<String> urls = message.getMediaUrls();
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    if (i < urls.size()) {
                        if (i == 3) {
                            collageImg4Container.setVisibility(View.VISIBLE);
                            Glide.with(itemView.getContext()).load(urls.get(i)).centerCrop().into(collageImgs[i]);
                            if (urls.size() > 4) {
                                textViewMore.setVisibility(View.VISIBLE);
                                textViewMore.setText("+" + (urls.size() - 3));
                            } else {
                                textViewMore.setVisibility(View.GONE);
                            }
                            collageImg4Container.setOnClickListener(v -> {
                                if (adapter.isSelectionMode()) {
                                    adapter.toggleSelection(message.getId());
                                } else {
                                    doubleTapHandler.handleTap(
                                        () -> openMediaViewer(null, Message.TYPE_COLLAGE, urls, index),
                                        () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                                    );
                                }
                            });
                        } else {
                            collageImgs[i].setVisibility(View.VISIBLE);
                            Glide.with(itemView.getContext()).load(urls.get(i)).centerCrop().into(collageImgs[i]);
                            collageImgs[i].setOnClickListener(v -> {
                                if (adapter.isSelectionMode()) {
                                    adapter.toggleSelection(message.getId());
                                } else {
                                    doubleTapHandler.handleTap(
                                        () -> openMediaViewer(null, Message.TYPE_COLLAGE, urls, index),
                                        () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                                    );
                                }
                            });
                        }
                    } else {
                        if (i == 3) collageImg4Container.setVisibility(View.GONE);
                        else collageImgs[i].setVisibility(View.GONE);
                    }
                }
            } else {
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(message.getText());
                textViewMessage.setOnClickListener(v -> {
                    if (adapter.isSelectionMode()) {
                        adapter.toggleSelection(message.getId());
                    } else {
                        doubleTapHandler.handleTap(
                            () -> {}, // No default action for single tap on text message here
                            () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                        );
                    }
                });
            }

            textViewTime.setText(formatTimestamp(message.getTimestamp()));

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onMessageLongClick(message, position);
                    return true;
                }
                return false;
            });
        }

        private void openMediaViewer(String path, String type, List<String> urls, int startPos) {
            Intent intent = new Intent(itemView.getContext(), MediaViewerActivity.class);
            if (urls != null) {
                intent.putStringArrayListExtra("MEDIA_URLS", new ArrayList<>(urls));
                intent.putExtra("START_POSITION", startPos);
            } else {
                intent.putExtra("MEDIA_PATH", path);
            }
            intent.putExtra("MEDIA_TYPE", type);
            itemView.getContext().startActivity(intent);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewTime;
        private final ImageView messageImageView;
        private final ImageView imageViewAvatar;
        private final View gridLayoutCollage;
        private final ImageView[] collageImgs;
        private final View collageImg4Container;
        private final TextView textViewMore;
        private final View selectionOverlay;
        private final DoubleTapHandler doubleTapHandler = new DoubleTapHandler();

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            messageImageView = itemView.findViewById(R.id.messageImageView);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
            gridLayoutCollage = itemView.findViewById(R.id.gridLayoutCollage);
            collageImgs = new ImageView[]{
                    itemView.findViewById(R.id.collageImg1),
                    itemView.findViewById(R.id.collageImg2),
                    itemView.findViewById(R.id.collageImg3),
                    itemView.findViewById(R.id.collageImg4)
            };
            collageImg4Container = itemView.findViewById(R.id.collageImg4Container);
            textViewMore = itemView.findViewById(R.id.textViewMore);
            
            selectionOverlay = new View(itemView.getContext());
            selectionOverlay.setBackgroundColor(Color.parseColor("#402A86FF"));
            selectionOverlay.setVisibility(View.GONE);
            ((ViewGroup) itemView).addView(selectionOverlay, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        public void bind(Message message, boolean isSelected, MessageAdapter adapter, OnMessageActionListener listener, int position, User otherUser) {
            String type = message.getType();
            textViewMessage.setVisibility(View.GONE);
            messageImageView.setVisibility(View.GONE);
            gridLayoutCollage.setVisibility(View.GONE);
            selectionOverlay.setVisibility(isSelected ? View.VISIBLE : View.GONE);

            if (Message.TYPE_IMAGE.equals(type) || Message.TYPE_VIDEO.equals(type) || Message.TYPE_GIF.equals(type)) {
                messageImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(message.getUrl())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .centerCrop()
                        .into(messageImageView);

                messageImageView.setOnClickListener(v -> {
                    if (adapter.isSelectionMode()) {
                        adapter.toggleSelection(message.getId());
                    } else {
                        doubleTapHandler.handleTap(
                            () -> openMediaViewer(message.getUrl(), type, null, 0),
                            () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                        );
                    }
                });
            } else if (Message.TYPE_COLLAGE.equals(type) && message.getMediaUrls() != null && !message.getMediaUrls().isEmpty()) {
                gridLayoutCollage.setVisibility(View.VISIBLE);
                List<String> urls = message.getMediaUrls();
                for (int i = 0; i < 4; i++) {
                    final int index = i;
                    if (i < urls.size()) {
                        if (i == 3) {
                            collageImg4Container.setVisibility(View.VISIBLE);
                            Glide.with(itemView.getContext()).load(urls.get(i)).centerCrop().into(collageImgs[i]);
                            if (urls.size() > 4) {
                                textViewMore.setVisibility(View.VISIBLE);
                                textViewMore.setText("+" + (urls.size() - 3));
                            } else {
                                textViewMore.setVisibility(View.GONE);
                            }
                            collageImg4Container.setOnClickListener(v -> {
                                if (adapter.isSelectionMode()) {
                                    adapter.toggleSelection(message.getId());
                                } else {
                                    doubleTapHandler.handleTap(
                                        () -> openMediaViewer(null, Message.TYPE_COLLAGE, urls, index),
                                        () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                                    );
                                }
                            });
                        } else {
                            collageImgs[i].setVisibility(View.VISIBLE);
                            Glide.with(itemView.getContext()).load(urls.get(i)).centerCrop().into(collageImgs[i]);
                            collageImgs[i].setOnClickListener(v -> {
                                if (adapter.isSelectionMode()) {
                                    adapter.toggleSelection(message.getId());
                                } else {
                                    doubleTapHandler.handleTap(
                                        () -> openMediaViewer(null, Message.TYPE_COLLAGE, urls, index),
                                        () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                                    );
                                }
                            });
                        }
                    } else {
                        if (i == 3) collageImg4Container.setVisibility(View.GONE);
                        else collageImgs[i].setVisibility(View.GONE);
                    }
                }
            } else {
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(message.getText());
                textViewMessage.setOnClickListener(v -> {
                    if (adapter.isSelectionMode()) {
                        adapter.toggleSelection(message.getId());
                    } else {
                        doubleTapHandler.handleTap(
                            () -> {},
                            () -> { if (listener != null) listener.onShowSingleContextMenu(v, message, position); }
                        );
                    }
                });
            }
            textViewTime.setText(formatTimestamp(message.getTimestamp()));

            if (otherUser != null) {
                if (otherUser.getAvatarUrl() != null) {
                    Glide.with(itemView.getContext()).load(otherUser.getAvatarUrl()).into(imageViewAvatar);
                }
                imageViewAvatar.setOnClickListener(v -> {
                    if (adapter.isSelectionMode()) {
                        adapter.toggleSelection(message.getId());
                    } else {
                        Intent intent = new Intent(v.getContext(), UserProfileActivity.class);
                        intent.putExtra("user", otherUser);
                        v.getContext().startActivity(intent);
                    }
                });
            }

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onMessageLongClick(message, position);
                    return true;
                }
                return false;
            });
        }

        private void openMediaViewer(String path, String type, List<String> urls, int startPos) {
            Intent intent = new Intent(itemView.getContext(), MediaViewerActivity.class);
            if (urls != null) {
                intent.putStringArrayListExtra("MEDIA_URLS", new ArrayList<>(urls));
                intent.putExtra("START_POSITION", startPos);
            } else {
                intent.putExtra("MEDIA_PATH", path);
            }
            intent.putExtra("MEDIA_TYPE", type);
            itemView.getContext().startActivity(intent);
        }
    }

}