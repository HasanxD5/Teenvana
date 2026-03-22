package by.hasanxd5.teenvana.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import by.hasanxd5.teenvana.R;

public class RegisterPhoneFragments {

    public static class Step1Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_register_phone_step1, container, false);

            view.findViewById(R.id.buttonBackReg).setOnClickListener(v -> {
                requireActivity().finish();
            });

            TextView phonePrompt = view.findViewById(R.id.phonePromptReg);
            view.findViewById(R.id.buttonNextReg).setOnClickListener(v -> {
                if (phonePrompt != null) {
                    // Получаем текст именно в момент клика!
                    String phoneStr = phonePrompt.getText().toString().trim();

                    if (phoneStr.isEmpty()) {
                        phonePrompt.setError("Enter phone number");
                    } else {
                        Navigation.findNavController(view).navigate(R.id.action_step1Fragment_to_step2Fragment);
                    }
                }
            });

            return view;
        }
    }

    public static class Step2Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_register_phone_step2, container, false);

            view.findViewById(R.id.buttonVerifySms).setOnClickListener(v -> {
                // Navigate to Step 3 using the graph action
                Navigation.findNavController(view).navigate(R.id.action_step2Fragment_to_step3Fragment);
            });

            view.findViewById(R.id.buttonBackSms).setOnClickListener(v -> {
                // Proper way to go back in Navigation Component
                Navigation.findNavController(view).popBackStack();
            });

            return view;
        }
    }
}