package by.hasanxd5.teenvana.fragments;

import static by.hasanxd5.teenvana.utils.UIUtils.applySystemInsets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import by.hasanxd5.teenvana.R;

/**
 * Fragment for application settings.
 */
public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_settings, container, false);
        applySystemInsets(view);

        view.findViewById(R.id.itemAccount).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_account));
        view.findViewById(R.id.itemNotifications).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_notifications));
        view.findViewById(R.id.itemChatSettings).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_chats));
        view.findViewById(R.id.itemPrivacy).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_privacy));
        view.findViewById(R.id.itemLanguage).setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settings_to_language));
        view.findViewById(R.id.itemHelp).setOnClickListener(v -> Toast.makeText(getContext(), R.string.settings_help, Toast.LENGTH_SHORT).show());
        view.findViewById(R.id.itemLogout).setOnClickListener(v -> {
            Toast.makeText(getContext(), R.string.toast_logged_out, Toast.LENGTH_SHORT).show();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }
}