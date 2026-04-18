package by.hasanxd5.teenvana.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import by.hasanxd5.teenvana.LoginActivity;
import by.hasanxd5.teenvana.R;

public class RegisterUsersFragments {

    public static class Step3Fragment extends Fragment {

        private TextInputEditText editBirthDate;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_register_user_step3, container, false);

            // Input Fields
            TextInputEditText editFirstName = view.findViewById(R.id.editFirstName);
            TextInputEditText editLastName = view.findViewById(R.id.editLastName);
            TextInputEditText editUsername = view.findViewById(R.id.editUsername);
            TextInputEditText editEmail = view.findViewById(R.id.editEmail);
            editBirthDate = view.findViewById(R.id.editBirthDate);

            // Layouts (Used for displaying error messages)
            // Note: Using getParent().getParent() is a bit fragile.
            // If you add IDs to your TextInputLayouts in XML, use view.findViewById(R.id.your_id) instead.
            TextInputLayout layoutFirstName = (TextInputLayout) editFirstName.getParent().getParent();
            TextInputLayout layoutLastName = (TextInputLayout) editLastName.getParent().getParent();
            TextInputLayout layoutUsername = (TextInputLayout) editUsername.getParent().getParent();
            TextInputLayout layoutEmail = (TextInputLayout) editEmail.getParent().getParent();
            TextInputLayout layoutBirthDate = (TextInputLayout) editBirthDate.getParent().getParent();

            // Date Picker Trigger
            editBirthDate.setOnClickListener(v -> showDatePicker(layoutBirthDate));

            // Next Button Logic
            view.findViewById(R.id.buttonNextUser).setOnClickListener(v -> {
                boolean isValid = validateInputs(
                        editFirstName, editLastName, editUsername, editEmail,
                        layoutFirstName, layoutLastName, layoutUsername, layoutEmail, layoutBirthDate
                );

                if (isValid) {
                    Navigation.findNavController(view).navigate(R.id.action_step3Fragment_to_step4Fragment);
                }
            });

            // Back Button Logic
            view.findViewById(R.id.buttonBackUser).setOnClickListener(v ->
                    Navigation.findNavController(view).popBackStack()
            );

            return view;
        }

        private boolean validateInputs(TextInputEditText etFirstName, TextInputEditText etLastName,
                                       TextInputEditText etUser, TextInputEditText etEmail,
                                       TextInputLayout lFirstName, TextInputLayout lLastName,
                                       TextInputLayout lUser, TextInputLayout lEmail, TextInputLayout lDate) {
            boolean isValid = true;

            // 1. First Name
            if (Objects.requireNonNull(etFirstName.getText()).toString().trim().isEmpty()) {
                lFirstName.setError("First name is required");
                isValid = false;
            } else {
                lFirstName.setError(null);
            }

            // 2. Last Name
            if (Objects.requireNonNull(etLastName.getText()).toString().trim().isEmpty()) {
                lLastName.setError("Last name is required");
                isValid = false;
            } else {
                lLastName.setError(null);
            }

            // 3. Username
            if (Objects.requireNonNull(etUser.getText()).toString().trim().isEmpty()) {
                lUser.setError("Username is required");
                isValid = false;
            } else {
                lUser.setError(null);
            }

            // 4. Email with Format Validation
            String emailText = Objects.requireNonNull(etEmail.getText()).toString().trim();
            if (emailText.isEmpty()) {
                lEmail.setError("Email is required");
                isValid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                lEmail.setError("Please enter a valid email address");
                isValid = false;
            } else {
                lEmail.setError(null);
            }

            // 5. Date of Birth
            if (Objects.requireNonNull(editBirthDate.getText()).toString().isEmpty()) {
                lDate.setError("Please select your date of birth");
                isValid = false;
            } else {
                lDate.setError(null);
            }

            return isValid;
        }

        private void showDatePicker(TextInputLayout layout) {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date of birth")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Using US locale and MM/dd/yyyy format for English app standards
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                editBirthDate.setText(sdf.format(new Date(selection)));
                layout.setError(null);
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        }
    }

    public static class Step4Fragment extends Fragment {

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            View view = inflater.inflate(R.layout.fragment_register_user_step4, container, false);

            // Initialize Input Fields
            TextInputEditText editPassword = view.findViewById(R.id.editPassword);
            TextInputEditText editPasswordConfirm = view.findViewById(R.id.editPasswordConfirm);

            // BACK BUTTON LOGIC: Returns the user to Step 3
            view.findViewById(R.id.buttonBackStep4).setOnClickListener(v -> Navigation.findNavController(view).popBackStack());

            // FINISH BUTTON LOGIC: Shows final confirmation dialog
            view.findViewById(R.id.buttonFinish).setOnClickListener(v -> {
                // It is recommended to perform password validation here before showing the dialog
                showConfirmationDialog();
            });

            return view;
        }

        /**
         * Displays a Material alert dialog to confirm the final registration step.
         */
        private void showConfirmationDialog() {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Complete Registration")
                    .setMessage("Are you sure you want to submit? You won't be able to change your previous details after this step.")
                    .setCancelable(false) // Prevents closing the dialog by clicking outside
                    .setPositiveButton("Finish", (dialog, which) -> {

                        // Show success message
                        Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to LoginActivity and clear the activity task stack
                        // This prevents the user from going back to registration screens via the back button
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // Close the current registration host activity
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton("Review", (dialog, which) -> {
                        // Just dismiss the dialog so the user can edit fields
                        dialog.dismiss();
                    })
                    .show();
        }
    }
}