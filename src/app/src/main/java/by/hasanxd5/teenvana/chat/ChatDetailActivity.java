package by.hasanxd5.teenvana.chat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

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
    private MediaHelper mediaHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        Window window = getWindow();
        int darkColor = ContextCompat.getColor(this, android.R.color.black);
        window.setStatusBarColor(darkColor);
        window.setNavigationBarColor(darkColor);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false); // false = белые иконки
        controller.setAppearanceLightNavigationBars(false); // false = белые иконки

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
            toolbarStatus.setText(R.string.status_online);
        }

        recyclerView = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        ImageButton buttonSend = findViewById(R.id.buttonSend);

        messages = new ArrayList<>();
        // Mock some messages
        messages.add(new Message("1", "other", "Hello!", System.currentTimeMillis(), "TEXT", null));
        messages.add(new Message("2", "me", "Hi there!", System.currentTimeMillis(), "TEXT", null));

        adapter = new MessageAdapter(messages, "me");
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());
        mediaHelper = new MediaHelper(getContentResolver());
        findViewById(R.id.buttonAttach).setOnClickListener(v -> showMediaPicker());
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

    private void showMediaPicker() {

        // --- Request permissions ---
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                }, 100);
                return; // Прерываем метод, пока пользователь не даст разрешение
            }
        } else {
            // Check for Android 12
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                return;
            }
        }

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_media_picker, null);

        TabLayout tabs = view.findViewById(R.id.mediaTabLayout);
        RecyclerView rv = view.findViewById(R.id.mediaRecyclerView);
        rv.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String selectedType = (tab.getPosition() == 1)
                        ? MediaHelper.TYPE_VIDEO
                        : MediaHelper.TYPE_IMAGE;

                // Передаем view, чтобы найти в нем кнопку
                updateList(rv, selectedType, dialog, view);
            }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });

        // Первичная загрузка
        updateList(rv, MediaHelper.TYPE_IMAGE, dialog, view);

        dialog.setContentView(view);
        dialog.show();
    }

    // Обновленный метод для ChatDetailActivity
    private void updateList(RecyclerView rv, String type, BottomSheetDialog dialog, View dialogView) {
        List<String> data = mediaHelper.fetchMedia(type);

        // Находим кнопку отправки в макете диалога
        android.widget.Button btnSend = dialogView.findViewById(R.id.buttonSendMedia);
        // Изначально скрываем кнопку, пока ничего не выбрано
        btnSend.setVisibility(View.GONE);

        @SuppressLint("SetTextI18n") MediaPickerAdapter adapter = new MediaPickerAdapter(data, selectedPaths -> {
            if (selectedPaths.isEmpty()) {
                btnSend.setVisibility(View.GONE);
            } else {
                btnSend.setVisibility(View.VISIBLE);
                btnSend.setText("Send (" + selectedPaths.size() + ")");
            }

            // Слушатель клика на кнопку "Отправить"
            btnSend.setOnClickListener(v -> {
                for (String path : selectedPaths) {
                    sendMediaMessage(path, type);
                }
                dialog.dismiss();
            });
        });

        rv.setAdapter(adapter);
    }

    private void sendMessage() {
        String text = editTextMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(
                    "" + System.currentTimeMillis(),
                    "me",
                    text, // переменная с текстом сообщения
                    System.currentTimeMillis(),
                    "TEXT",
                    null
            );
            messages.add(newMessage);
            adapter.notifyItemInserted(messages.size() - 1);
            recyclerView.scrollToPosition(messages.size() - 1);
            editTextMessage.setText("");
        }
    }

    private void sendMediaMessage(String filePath, String type) {
        // 1. Create a unique ID for the message (usually based on timestamp)
        String messageId = String.valueOf(System.currentTimeMillis());

        // 2. Get the current timestamp
        long currentTime = System.currentTimeMillis();

        // 3. Create the Message object
        // Assuming your Message constructor is: Message(id, senderId, text, timestamp, type, mediaUrl)
        Message mediaMessage = new Message(
                messageId,
                "me",           // Current user ID
                null,           // No text for media messages
                currentTime,
                type,           // "IMAGE", "VIDEO", or "GIF"
                filePath        // Local path to the file
        );

        // 4. Add to your local list
        messages.add(mediaMessage);

        // 5. Notify the adapter that a new item is inserted
        adapter.notifyItemInserted(messages.size() - 1);

        // 6. Scroll to the bottom to show the new message
        recyclerView.scrollToPosition(messages.size() - 1);

        // TODO: Later you will add Firebase Storage upload logic here
    }

}
