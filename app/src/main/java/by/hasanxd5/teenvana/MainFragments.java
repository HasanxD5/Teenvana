package by.hasanxd5.teenvana;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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