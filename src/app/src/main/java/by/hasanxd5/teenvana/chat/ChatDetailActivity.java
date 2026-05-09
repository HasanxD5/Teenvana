package by.hasanxd5.teenvana.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.models.Chat;
import by.hasanxd5.teenvana.models.Message;
import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    private List<Message> messages;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private EditText editTextMessage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        Chat chat = (Chat) getIntent().getSerializableExtra("chat");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView toolbarTitle = findViewById(R.id.toolbarTitle);
        TextView toolbarStatus = findViewById(R.id.toolbarStatus);
        if (chat != null) {
            toolbarTitle.setText(chat.getOtherUser().getName());
            toolbarStatus.setText("online");
        }

        recyclerView = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        ImageButton buttonEmoji = findViewById(R.id.buttonEmoji);
        ImageButton buttonAttach = findViewById(R.id.buttonAttach);
        ImageButton buttonSend = findViewById(R.id.buttonSend);

        messages = new ArrayList<>();
        // Mock some messages
        messages.add(new Message("1", "other", "Hello!", System.currentTimeMillis()));
        messages.add(new Message("2", "me", "Hi there!", System.currentTimeMillis()));

        adapter = new MessageAdapter(messages, "me");
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());
        buttonEmoji.setOnClickListener(v -> Toast.makeText(this, "Emoji picker opened", Toast.LENGTH_SHORT).show());
        buttonAttach.setOnClickListener(v -> Toast.makeText(this, "Attachment picker opened", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_mute_1h) {
            Toast.makeText(this, "Muted for 1 hour", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_mute_8h) {
            Toast.makeText(this, "Muted for 8 hours", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_mute_2d) {
            Toast.makeText(this, "Muted for 2 days", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_mute_forever) {
            Toast.makeText(this, "Muted forever", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_unmute) {
            Toast.makeText(this, "Unmuted", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_clear_history) {
            Toast.makeText(this, "Clear history clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete_chat) {
            Toast.makeText(this, "Delete chat clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        String text = editTextMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(
                    "" + System.currentTimeMillis(),
                    "me",
                    text,
                    System.currentTimeMillis()
            );
            messages.add(newMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
            editTextMessage.setText("");
        }
    }
}
