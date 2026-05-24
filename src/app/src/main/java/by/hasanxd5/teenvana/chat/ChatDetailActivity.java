package by.hasanxd5.teenvana.chat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiTheming;
import com.vanniktech.emoji.EmojiView;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.models.Chat;
import by.hasanxd5.teenvana.models.Message;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatDetailActivity extends AppCompatActivity {

    private List<Message> messages;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private EmojiEditText editTextMessage;
    private MediaHelper mediaHelper;
    private ActionMode actionMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EmojiManager.install(new GoogleEmojiProvider());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        Window window = getWindow();
        int darkColor = ContextCompat.getColor(this, android.R.color.black);
        window.setStatusBarColor(darkColor);
        window.setNavigationBarColor(darkColor);

        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

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
        ImageView toolbarAvatar = findViewById(R.id.toolbarAvatar);

        if (chat != null) {
            toolbarTitle.setText(chat.getOtherUser().getName());
            toolbarStatus.setText(chat.getOtherUser().getStatus());
            if (chat.getOtherUser().getAvatarUrl() != null) {
                Glide.with(this).load(chat.getOtherUser().getAvatarUrl()).placeholder(R.drawable.ic_launcher_foreground).into(toolbarAvatar);
            }

            View.OnClickListener profileClick = v -> {
                Intent intent = new Intent(this, by.hasanxd5.teenvana.UserProfileActivity.class);
                intent.putExtra("user", chat.getOtherUser());
                startActivity(intent);
            };

            toolbarAvatar.setOnClickListener(profileClick);
            toolbarTitle.setOnClickListener(profileClick);
            findViewById(R.id.toolbarStatus).setOnClickListener(profileClick);
        }

        recyclerView = findViewById(R.id.recyclerViewMessages);
        editTextMessage = findViewById(R.id.editTextMessage);
        ImageButton buttonSend = findViewById(R.id.buttonSend);
        ImageButton buttonEmoji = findViewById(R.id.buttonEmoji);

        buttonSend.setEnabled(false);
        buttonSend.setAlpha(0.5f);

        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = !s.toString().trim().isEmpty();
                buttonSend.setEnabled(hasText);
                buttonSend.setAlpha(hasText ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        messages = new ArrayList<>();
        messages.add(new Message("1", "other", "Hello!", System.currentTimeMillis(), Message.TYPE_TEXT, null));
        messages.add(new Message("2", "me", "Hi there!", System.currentTimeMillis(), Message.TYPE_TEXT, null));

        adapter = new MessageAdapter(messages, "me");
        if (chat != null) {
            adapter.setOtherUser(chat.getOtherUser());
        }
        adapter.setOnMessageActionListener(new MessageAdapter.OnMessageActionListener() {
            @Override
            public void onMessageLongClick(Message message, int position) {
                if (actionMode == null) {
                    startSupportActionMode(new MessageActionModeCallback());
                }
                adapter.toggleSelection(message.getId());
            }

            @Override
            public void onSelectionChanged(int count) {
                if (actionMode != null) {
                    if (count == 0) {
                        actionMode.finish();
                    } else {
                        actionMode.setTitle(String.valueOf(count));
                    }
                }
            }

            @Override
            public void onShowSingleContextMenu(View view, Message message, int position) {
                showSingleContextMenu(view, message, position);
            }
        });
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());

        mediaHelper = new MediaHelper(getContentResolver());
        findViewById(R.id.buttonAttach).setOnClickListener(v -> showMediaPicker());

        buttonEmoji.setOnClickListener(v -> showExtrasPicker());
    }

    private void showExtrasPicker() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_chat_extras, null);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutExtras);
        FrameLayout container = view.findViewById(R.id.containerExtras);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                container.removeAllViews();
                switch (tab.getPosition()) {
                    case 0: // Emoji
                        EmojiView emojiView = new EmojiView(ChatDetailActivity.this);
                        emojiView.setUp(
                            findViewById(android.R.id.content),
                            emoji -> editTextMessage.append(emoji.getUnicode()),
                            () -> editTextMessage.dispatchKeyEvent(new android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_DEL)),
                            editTextMessage,
                            EmojiTheming.Companion.from(ChatDetailActivity.this),
                            new com.vanniktech.emoji.recent.RecentEmojiManager(ChatDetailActivity.this),
                            new com.vanniktech.emoji.search.SearchEmojiManager(),
                            new com.vanniktech.emoji.variant.VariantEmojiManager(ChatDetailActivity.this),
                            null
                        );
                        container.addView(emojiView);
                        break;
                    case 1: // GIF
                        @SuppressLint("InflateParams")
                        View gifView = getLayoutInflater().inflate(R.layout.dialog_gif_picker, null);
                        RecyclerView rvGifs = gifView.findViewById(R.id.rvGifs);
                        List<String> mockGifs = new ArrayList<>();
                        mockGifs.add("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJndGZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3o7TKDkDbIDJieKbVm/giphy.gif");
                        mockGifs.add("https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNHJndGZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZWZ4ZSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/3o7TKVUn7iM8FMEU24/giphy.gif");
                        MediaPickerAdapter gifAdapter = new MediaPickerAdapter(mockGifs, selectedPaths -> {
                            if (!selectedPaths.isEmpty()) {
                                sendMediaMessage(selectedPaths.get(0), Message.TYPE_GIF);
                                dialog.dismiss();
                            }
                        });
                        rvGifs.setAdapter(gifAdapter);
                        container.addView(gifView);
                        break;
                    case 2: // Stickers
                        @SuppressLint("InflateParams")
                        View stickerView = getLayoutInflater().inflate(R.layout.dialog_sticker_picker, null);
                        RecyclerView rvStickers = stickerView.findViewById(R.id.rvStickers);
                        List<String> mockStickers = new ArrayList<>();
                        mockStickers.add("https://cdn-icons-png.flaticon.com/512/3536/3536394.png");
                        mockStickers.add("https://cdn-icons-png.flaticon.com/512/3536/3536444.png");
                        MediaPickerAdapter stickerAdapter = new MediaPickerAdapter(mockStickers, selectedPaths -> {
                            if (!selectedPaths.isEmpty()) {
                                sendMediaMessage(selectedPaths.get(0), Message.TYPE_IMAGE);
                                dialog.dismiss();
                            }
                        });
                        rvStickers.setAdapter(stickerAdapter);
                        container.addView(stickerView);
                        break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });

        TabLayout.Tab tab = tabLayout.getTabAt(0);
        if (tab != null) tab.select();
        
        dialog.setContentView(view);
        dialog.show();
    }

    private void showSingleContextMenu(View view, Message message, int position) {
        PopupMenu popup = new PopupMenu(this, view);
        boolean isMedia = !Message.TYPE_TEXT.equals(message.getType());

        if (isMedia) {
            popup.getMenu().add(0, 0, 0, "Save to Gallery");
            popup.getMenu().add(0, 1, 1, "Share");
        } else {
            popup.getMenu().add(0, 3, 0, "Copy Text");
            popup.getMenu().add(0, 4, 1, "Edit Message");
        }

        popup.getMenu().add(0, 5, 2, "Forward");
        popup.getMenu().add(0, 6, 3, "Send to another chat");
        popup.getMenu().add(0, 7, 4, "Select");
        popup.getMenu().add(0, 2, 5, "Delete for me");

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    saveMedia(message.getUrl());
                    return true;
                case 1:
                    shareMedia(message.getUrl());
                    return true;
                case 2:
                    deleteMessage(position);
                    return true;
                case 3:
                    copyToClipboard(message.getText());
                    return true;
                case 4:
                    showEditMessageDialog(message, position);
                    return true;
                case 5:
                case 6:
                    Toast.makeText(this, "Forwarded", Toast.LENGTH_SHORT).show();
                    return true;
                case 7:
                    if (actionMode == null) {
                        startSupportActionMode(new MessageActionModeCallback());
                    }
                    adapter.toggleSelection(message.getId());
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private class MessageActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            adapter.setSelectionMode(true);
            mode.getMenuInflater().inflate(R.menu.menu_message_selection, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Message> selected = adapter.getSelectedMessages();
            int id = item.getItemId();
            if (id == R.id.action_delete_selected) {
                messages.removeAll(selected);
                adapter.updateList(messages);
                mode.finish();
                return true;
            } else if (id == R.id.action_forward_selected || id == R.id.action_send_another_selected) {
                Toast.makeText(ChatDetailActivity.this, "Forwarded " + selected.size() + " messages", Toast.LENGTH_SHORT).show();
                mode.finish();
                return true;
            } else if (id == R.id.action_copy_selected) {
                StringBuilder sb = new StringBuilder();
                for (Message m : selected) {
                    if (m.getText() != null) sb.append(m.getText()).append("\n");
                }
                copyToClipboard(sb.toString());
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.setSelectionMode(false);
            actionMode = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean allGranted = true;
            for (int res : grantResults) {
                if (res != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                showMediaPicker();
            } else {
                Toast.makeText(this, "Permissions required to select media", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_detail, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        View customToolbarContent = findViewById(R.id.customToolbarContent);

        if (searchItem != null) {
            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    if (customToolbarContent != null) customToolbarContent.setVisibility(View.GONE);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (customToolbarContent != null) customToolbarContent.setVisibility(View.VISIBLE);
                    return true;
                }
            });

            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Search messages...");
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        adapter.filter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
                        return true;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_mute_1h) {
            muteChat(1);
            return true;
        } else if (id == R.id.action_mute_8h) {
            muteChat(8);
            return true;
        } else if (id == R.id.action_mute_24h) {
            muteChat(24);
            return true;
        } else if (id == R.id.action_mute_forever) {
            muteChat(-1);
            return true;
        } else if (id == R.id.action_unmute) {
            unmuteChat();
            return true;
        } else if (id == R.id.action_clear_history) {
            showClearHistoryDialog();
            return true;
        } else if (id == R.id.action_delete_chat) {
            showDeleteChatDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void muteChat(int hours) {
        String message = hours == -1 ? "Muted forever" : "Muted for " + hours + " hours";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void unmuteChat() {
        Toast.makeText(this, "Unmuted", Toast.LENGTH_SHORT).show();
    }

    private void showClearHistoryDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Clear History")
                .setMessage("Are you sure you want to delete all messages in this chat?")
                .setPositiveButton("Clear", (dialog, which) -> {
                    messages.clear();
                    adapter.updateList(messages);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteChatDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Chat")
                .setMessage("Are you sure you want to delete this chat? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMediaPicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != android.content.pm.PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) != android.content.pm.PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        android.Manifest.permission.READ_MEDIA_IMAGES,
                        android.Manifest.permission.READ_MEDIA_VIDEO
                }, 100);
                return;
            }
        } else {
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

                updateList(rv, selectedType, dialog, view);
            }
            @Override public void onTabUnselected(TabLayout.Tab t) {}
            @Override public void onTabReselected(TabLayout.Tab t) {}
        });

        updateList(rv, MediaHelper.TYPE_IMAGE, dialog, view);

        dialog.setContentView(view);
        dialog.show();
    }

    private void updateList(RecyclerView rv, String type, BottomSheetDialog dialog, View dialogView) {
        List<String> data = mediaHelper.fetchMedia(type);
        renderMediaList(rv, data, type, dialog, dialogView);
    }

    private void renderMediaList(RecyclerView rv, List<String> data, String type, BottomSheetDialog dialog, View dialogView) {
        android.widget.Button btnSend = dialogView.findViewById(R.id.buttonSendMedia);
        btnSend.setVisibility(View.GONE);

        MediaPickerAdapter adapter = new MediaPickerAdapter(data, selectedPaths -> {
            if (selectedPaths.isEmpty()) {
                btnSend.setVisibility(View.GONE);
            } else {
                btnSend.setVisibility(View.VISIBLE);
                btnSend.setText(getString(R.string.media_send_button, selectedPaths.size()));
            }

            btnSend.setOnClickListener(v -> {
                if (selectedPaths.size() > 1) {
                    sendCollageMessage(selectedPaths);
                } else {
                    sendMediaMessage(selectedPaths.get(0), type);
                }
                dialog.dismiss();
            });
        });

        rv.setAdapter(adapter);
    }

    private void sendMessage() {
        if (editTextMessage.getText() == null) return;
        String text = editTextMessage.getText().toString().trim();
        if (!text.isEmpty()) {
            Message newMessage = new Message(
                    String.valueOf(System.currentTimeMillis()),
                    "me",
                    text,
                    System.currentTimeMillis(),
                    Message.TYPE_TEXT,
                    null
            );
            addMessage(newMessage);
            editTextMessage.setText("");
        }
    }

    private void addMessage(Message message) {
        messages.add(message);
        adapter.updateList(messages);
        scrollToBottom();
    }

    private void showEditMessageDialog(Message message, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_message, null);
        TextInputEditText editInput = dialogView.findViewById(R.id.editTextEditMessage);
        editInput.setText(message.getText());

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newText = Objects.requireNonNull(editInput.getText()).toString().trim();
                    if (!newText.isEmpty()) {
                        message.setText(newText);
                        adapter.notifyItemChanged(position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveMedia(String path) {
        if (path == null) return;

        // Correctly handle local paths vs URIs
        Uri sourceUri = path.startsWith("/") ? Uri.fromFile(new File(path)) : Uri.parse(path);

        String fileName = "Teenvana_" + System.currentTimeMillis();
        String mimeType = URLConnection.guessContentTypeFromName(path);
        if (mimeType == null) mimeType = "image/jpeg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);

        // Add relative path for Scoped Storage (Android 10+)
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Teenvana");
        values.put(MediaStore.MediaColumns.IS_PENDING, 1);

        Uri collection = (mimeType.startsWith("video"))
                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        Uri resultUri = getContentResolver().insert(collection, values);

        if (resultUri != null) {
            try (InputStream is = getContentResolver().openInputStream(sourceUri);
                 OutputStream os = getContentResolver().openOutputStream(resultUri)) {

                if (is != null && os != null) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                    }
                }

                values.clear();
                values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                getContentResolver().update(resultUri, values, null, null);

                Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void shareMedia(String path) {
        if (path == null) return;

        Uri uri;
        if (path.startsWith("/")) {
            uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", new File(path));
        } else {
            uri = Uri.parse(path);
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String mimeType = URLConnection.guessContentTypeFromName(path);
        shareIntent.setType(mimeType != null ? mimeType : "image/*");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share Media"));
    }

    private void deleteMessage(int position) {
        if (position >= 0 && position < messages.size()) {
            messages.remove(position);
            adapter.updateList(messages);
        }
    }

    private void copyToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            android.content.ClipData clip = android.content.ClipData.newPlainText("Chat Message", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollToBottom() {
        if (!messages.isEmpty()) {
            recyclerView.scrollToPosition(messages.size() - 1);
        }
    }

    private void sendMediaMessage(String filePath, String type) {
        String messageId = String.valueOf(System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();

        Message mediaMessage = new Message(
                messageId,
                "me",
                null,
                currentTime,
                type,
                filePath
        );

        addMessage(mediaMessage);
    }

    private void sendCollageMessage(List<String> filePaths) {
        String messageId = String.valueOf(System.currentTimeMillis());
        long currentTime = System.currentTimeMillis();

        Message collageMessage = new Message(
                messageId,
                "me",
                null,
                currentTime,
                Message.TYPE_COLLAGE,
                null,
                filePaths
        );

        addMessage(collageMessage);
    }
}