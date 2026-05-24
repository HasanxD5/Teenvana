package by.hasanxd5.teenvana.fragments;

import static by.hasanxd5.teenvana.utils.UIUtils.applySystemInsets;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import by.hasanxd5.teenvana.R;
import by.hasanxd5.teenvana.chat.ChatAdapter;
import by.hasanxd5.teenvana.chat.ChatDetailActivity;
import by.hasanxd5.teenvana.models.Chat;
import by.hasanxd5.teenvana.models.Message;
import by.hasanxd5.teenvana.models.User;

/**
 * Fragment for displaying user chats.
 */
public class ChatsFragment extends Fragment {

    private Uri capturedMediaUri;
    private boolean isVideoRequest = false;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    String mediaType = isVideoRequest ? getString(R.string.media_tab_videos) : getString(R.string.media_tab_images);
                    Toast.makeText(getContext(), mediaType + " captured!", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_chats, container, false);
        applySystemInsets(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChats);
        List<Chat> mockChats = new ArrayList<>();
        mockChats.add(new Chat("1", new User("1", "John Doe", null), new Message("1", "1", "Hello!", System.currentTimeMillis(), Message.TYPE_TEXT, null), 2));
        mockChats.add(new Chat("2", new User("2", "Jane Smith", null), new Message("2", "2", "How are you?", System.currentTimeMillis() - 100000, Message.TYPE_TEXT, null), 0));

        ChatAdapter adapter = new ChatAdapter(mockChats, chat -> {
            Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
            intent.putExtra("chat", chat);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabNewChat = view.findViewById(R.id.fabNewChat);
        fabNewChat.setOnClickListener(v -> Toast.makeText(getContext(), R.string.desc_new_chat, Toast.LENGTH_SHORT).show());

        FloatingActionButton fabCamera = view.findViewById(R.id.fabCamera);
        fabCamera.setOnClickListener(v -> openSystemCamera());

        return view;
    }

    private void openSystemCamera() {
        String[] options = {getString(R.string.dialog_photo), getString(R.string.dialog_video)};
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_camera_mode)
                .setItems(options, (dialog, which) -> {
                    isVideoRequest = (which == 1);
                    Intent intent;
                    if (isVideoRequest) {
                        intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    } else {
                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    }

                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        ContentValues values = new ContentValues();
                        values.put(MediaStore.Images.Media.TITLE, "New Media");
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");

                        Uri collection = isVideoRequest
                                ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                        capturedMediaUri = requireContext().getContentResolver().insert(collection, values);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedMediaUri);
                        cameraLauncher.launch(intent);
                    }
                })
                .show();
    }
}