package by.hasanxd5.teenvana;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import by.hasanxd5.teenvana.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {

    private Button buttonSubmit;
    private TextView loginPrompt, passwordPrompt, linkSignUp, linkForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Standard Edge-to-Edge padding setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        loginPrompt = findViewById(R.id.loginPrompt);
        passwordPrompt = findViewById(R.id.passwordPrompt);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        linkSignUp = findViewById(R.id.textViewSignUp);
        linkForgotPassword = findViewById(R.id.textViewForgotPassword);
    }

    private void setupClickListeners() {
        // English comment: Navigate to RegisterActivity when "Sign Up" is clicked
        linkSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // English comment: Handle "Forgot Password" click
        linkForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        buttonSubmit.setOnClickListener(v -> {

            String login = loginPrompt.getText().toString().trim();
            String password = passwordPrompt.getText().toString().trim();

            if (login.equals("admin") && password.equals("admin")) {
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter login and password", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
            }

        });
    }
}