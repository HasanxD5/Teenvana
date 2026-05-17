package by.hasanxd5.teenvana;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import by.hasanxd5.teenvana.chat.ChatAdapter;
import by.hasanxd5.teenvana.chat.ChatDetailActivity;
import by.hasanxd5.teenvana.models.Chat;
import by.hasanxd5.teenvana.models.Message;
import by.hasanxd5.teenvana.models.User;

import java.util.ArrayList;
import java.util.List;

public class MainFragments {

    /**
     * Fragment for displaying user chats.
     */
    public static class ChatsFragment extends Fragment {

        // Class-level field to persist the file URI across activity lifecycles
        private Uri capturedMediaUri;
        // Track whether we requested a video or a photo
        private boolean isVideoRequest = false;

        // Modern Activity Result Launcher handling camera callback events
        private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (capturedMediaUri != null) {
                            // Success: Determine what type of media was captured
                            String mediaType = isVideoRequest ? "VIDEO" : "IMAGE";
                            Toast.makeText(getContext(), mediaType + " captured successfully!", Toast.LENGTH_SHORT).show();

                            // TODO: Pass 'capturedMediaUri' and 'mediaType' to your target chat screen or message sender
                            // Example: sendMediaToChat(capturedMediaUri, mediaType);
                        }
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        // User backed out without capturing anything
                        Toast.makeText(getContext(), "Camera capture canceled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_main_chats, container, false);
            applySystemInsets(view);

            // Set up the RecyclerView for active chat conversations
            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChats);
            List<Chat> chats = new ArrayList<>();

            // Mock sample chat data for development/UI testing
            User user1 = new User("1", "John Doe", null);
            Message lastMsg1 = new Message("1", "1", "Hello there!", System.currentTimeMillis(), "TEXT", null);
            chats.add(new Chat("1", user1, lastMsg1, 2));

            User user2 = new User("2", "Jane Smith", null);
            Message lastMsg2 = new Message("2", "me", "See you tomorrow", System.currentTimeMillis(), "TEXT", null);
            chats.add(new Chat("2", user2, lastMsg2, 0));

            // Initialize the adapter and attach the chat row item click handler
            ChatAdapter adapter = new ChatAdapter(chats, chat -> {
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("chat", chat);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            // Initialize Floating Action Buttons (FABs)
            FloatingActionButton fabNewChat = view.findViewById(R.id.fabNewChat);
            FloatingActionButton fabCamera = view.findViewById(R.id.fabCamera);

            // Click listener to navigate to the contact selection or new conversation screen
            fabNewChat.setOnClickListener(v -> Toast.makeText(getContext(), "Open Create New Chat Screen", Toast.LENGTH_SHORT).show());

            // Click listener to launch the system camera capture app
            fabCamera.setOnClickListener(v -> openSystemCamera());

            return view;
        }

        /**
         * Prepares media storage configurations and launches the system camera app.
         */
        @SuppressLint("QueryPermissionsNeeded")
        private void openSystemCamera() {
            if (getActivity() == null) return;

            // Options for the user selection dialog
            CharSequence[] options = {"Take Photo", "Record Video"};

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Choose Camera Mode");
            builder.setItems(options, (dialog, item) -> {
                ContentValues values = new ContentValues();
                Intent cameraIntent;

                if (item == 0) {
                    // Photo Mode selected
                    isVideoRequest = false;
                    values.put(MediaStore.Images.Media.TITLE, "Photo_" + System.currentTimeMillis());
                    capturedMediaUri = getActivity().getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                    );
                    cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                } else {
                    // Video Mode selected
                    isVideoRequest = true;
                    values.put(MediaStore.Video.Media.TITLE, "Video_" + System.currentTimeMillis());
                    capturedMediaUri = getActivity().getContentResolver().insert(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values
                    );
                    cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    // Optional: Limit video duration to 30 seconds for chat optimization
                    cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
                }

                // Pass the target destination URI to the camera intent
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedMediaUri);

                // Verify that a system camera handler app exists before executing the intent
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    cameraLauncher.launch(cameraIntent);
                } else {
                    Toast.makeText(getContext(), "Camera application not found", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        }
    }

    /**
     * Fragment for displaying the contact list.
     */
    public static class ContactsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_main_contacts, container, false);
            applySystemInsets(view);
            return view;
        }
    }

    /**
     * Fragment for user settings and preferences.
     */
    public static class SettingsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_main_settings, container, false);
            applySystemInsets(view);
            return view;
        }
    }

    /**
     * Helper method to apply window insets for Edge-to-Edge support.
     * This handles padding for the title (top) and scrollable content (bottom).
     */
    private static void applySystemInsets(View view) {
        View titleView = view.findViewById(R.id.textViewTitle);
        View scrollContent = view.findViewById(R.id.nestedScrollView);

        if (titleView == null) return;

        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Adjust title padding to account for the status bar
            titleView.setPadding(
                    titleView.getPaddingLeft(),
                    systemBars.top + (int) (16 * v.getResources().getDisplayMetrics().density),
                    titleView.getPaddingRight(),
                    titleView.getPaddingBottom()
            );

            // Adjust scroll content padding to account for the navigation bar
            if (scrollContent != null) {
                scrollContent.setPadding(
                        scrollContent.getPaddingLeft(),
                        scrollContent.getPaddingTop(),
                        scrollContent.getPaddingRight(),
                        systemBars.bottom
                );
            }

            return WindowInsetsCompat.CONSUMED;
        });
    }
}