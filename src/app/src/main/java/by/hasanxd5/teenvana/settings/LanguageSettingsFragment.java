package by.hasanxd5.teenvana.settings;

import static by.hasanxd5.teenvana.utils.UIUtils.applySystemInsets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import by.hasanxd5.teenvana.R;

public class LanguageSettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_settings, container, false);
        applySystemInsets(view);

        View toolbar = view.findViewById(R.id.toolbar);
        if (toolbar instanceof androidx.appcompat.widget.Toolbar) {
            ((androidx.appcompat.widget.Toolbar) toolbar).setNavigationOnClickListener(v -> 
                Navigation.findNavController(v).navigateUp());
        }

        return view;
    }
}