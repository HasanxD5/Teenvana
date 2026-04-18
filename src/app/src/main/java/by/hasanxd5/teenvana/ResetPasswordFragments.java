package by.hasanxd5.teenvana;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AlertDialog;
import android.widget.FrameLayout;
import android.view.Gravity;
import com.google.android.material.progressindicator.CircularProgressIndicator;

public class ResetPasswordFragments {

    public static class Step1Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_reset_password_step1, container, false);

            view.findViewById(R.id.btnNext).setOnClickListener(v -> {
                TextView emailPrompt = view.findViewById(R.id.emailEditText);
                String email = emailPrompt.getText().toString();
                if (email.isEmpty()) {
                    emailPrompt.setError("Email is required");
                } else {
                    emailPrompt.setError(null);
                    showLoadingDialog(view);
                }
            });

            return view;
        }

        private void showLoadingDialog(View view) {
            CircularProgressIndicator progressIndicator = new CircularProgressIndicator(requireContext());
            progressIndicator.setIndeterminate(true);

            // Create a container to add padding around the progress indicator
            FrameLayout container = new FrameLayout(requireContext());
            int padding = (int) (24 * getResources().getDisplayMetrics().density);
            container.setPadding(padding, padding, padding, padding);
            container.addView(progressIndicator, new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER));

            AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.reset_step2_header)
                    .setMessage(R.string.reset_step2_description)
                    .setView(container)
                    .setCancelable(false)
                    .create();

            dialog.show();

            // Simulate sending email delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (isAdded()) {
                    dialog.dismiss();
                    Navigation.findNavController(view).navigate(R.id.action_resetPasswordStep1Fragment_to_resetPasswordStep2Fragment);
                }
            }, 2000); // 2 seconds delay
        }
    }

    public static class Step2Fragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_reset_password_step2, container, false);

            view.findViewById(R.id.btnResetPassword).setOnClickListener(v -> {
                Toast.makeText(getContext(), "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });

            return view;
        }
    }
}