package by.hasanxd5.teenvana.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.models.Chat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Chat> chats;
    private final OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public ChatAdapter(List<Chat> chats, OnChatClickListener listener) {
        this.chats = chats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.bind(chat, listener);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final TextView textViewLastMessage;
        private final TextView textViewTime;
        private final TextView textViewUnread;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewLastMessage = itemView.findViewById(R.id.textViewLastMessage);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewUnread = itemView.findViewById(R.id.textViewUnread);
        }

        public void bind(Chat chat, OnChatClickListener listener) {
            textViewName.setText(chat.getOtherUser().getName());
            if (chat.getLastMessage() != null) {
                textViewLastMessage.setText(chat.getLastMessage().getText());
                textViewTime.setText(formatTimestamp(chat.getLastMessage().getTimestamp()));
            } else {
                textViewLastMessage.setText("");
                textViewTime.setText("");
            }

            if (chat.getUnreadCount() > 0) {
                textViewUnread.setVisibility(View.VISIBLE);
                textViewUnread.setText(String.valueOf(chat.getUnreadCount()));
            } else {
                textViewUnread.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> listener.onChatClick(chat));
        }

        private String formatTimestamp(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            return sdf.format(new Date(timestamp));
        }
    }
}
