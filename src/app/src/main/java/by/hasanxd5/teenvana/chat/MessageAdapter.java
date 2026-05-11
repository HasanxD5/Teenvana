package by.hasanxd5.teenvana.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.models.Message;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    private final List<Message> messages;
    private final String currentUserId;

    public MessageAdapter(List<Message> messages, String currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
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
        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
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

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewTime;
        private final ImageView messageImageView;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }

        public void bind(Message message) {
            String type = message.getType();

            if ("IMAGE".equals(type) || "VIDEO".equals(type)) {
                messageImageView.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);

                Glide.with(itemView.getContext())
                        .load(message.getUrl())
                        .placeholder(R.drawable.loading_placeholder)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(messageImageView);
            } else {
                messageImageView.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(message.getText());
            }

            textViewTime.setText(formatTimestamp(message.getTimestamp()));

            itemView.setOnClickListener(v -> {
                if ("IMAGE".equals(type) || "VIDEO".equals(type)) {
                    android.content.Intent intent = new android.content.Intent(v.getContext(), MediaViewerActivity.class);
                    intent.putExtra("MEDIA_PATH", message.getUrl());
                    intent.putExtra("MEDIA_TYPE", type);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewMessage;
        private final TextView textViewTime;
        private final ImageView messageImageView;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            messageImageView = itemView.findViewById(R.id.messageImageView);
        }

        public void bind(Message message) {
            if ("IMAGE".equals(message.getType()) || "GIF".equals(message.getType())) {
                messageImageView.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);

                Glide.with(itemView.getContext())
                        .load(message.getUrl())
                        .placeholder(R.drawable.loading_placeholder)
                        .error(R.drawable.error_image)
                        .centerCrop()
                        .into(messageImageView);
            } else {
                messageImageView.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewMessage.setText(message.getText());
            }
            textViewTime.setText(formatTimestamp(message.getTimestamp()));
        }
    }

}
