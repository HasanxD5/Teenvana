package by.hasanxd5.teenvana;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
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
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_main_chats, container, false);
            applySystemInsets(view);

            RecyclerView recyclerView = view.findViewById(R.id.recyclerViewChats);
            List<Chat> chats = new ArrayList<>();
            // Mock some chats
            User user1 = new User("1", "John Doe", null);
            Message lastMsg1 = new Message("1", "1", "Hello there!", System.currentTimeMillis(), "TEXT", null);
            chats.add(new Chat("1", user1, lastMsg1, 2));

            User user2 = new User("2", "Jane Smith", null);
            Message lastMsg2 = new Message("2", "me", "See you tommorow", System.currentTimeMillis(), "TEXT", null);
            chats.add(new Chat("2", user2, lastMsg2, 0));

            ChatAdapter adapter = new ChatAdapter(chats, chat -> {
                Intent intent = new Intent(getActivity(), ChatDetailActivity.class);
                intent.putExtra("chat", chat);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);

            return view;
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