package by.hasanxd5.teenvana;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import by.hasanxd5.teenvana.models.User;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
            collapsingToolbar.setTitle(user.getName());

            ImageView imageViewProfile = findViewById(R.id.imageViewProfile);
            if (user.getAvatarUrl() != null) {
                Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.ic_pearson).into(imageViewProfile);
            }

            TextView textViewBio = findViewById(R.id.textViewBio);
            textViewBio.setText(user.getBio());

            TextView textViewStatus = findViewById(R.id.textViewStatus);
            textViewStatus.setText(user.getStatus());

            TextView textViewPhone = findViewById(R.id.textViewPhone);
            textViewPhone.setText(user.getPhoneNumber());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}