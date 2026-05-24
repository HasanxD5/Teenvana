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

import by.hasanxd5.teenvana.R;

public class ContactsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_contacts, container, false);
        applySystemInsets(view);

        view.findViewById(R.id.buttonInviteFriends).setOnClickListener(v ->
                Toast.makeText(getContext(), R.string.contacts_invite_friends, Toast.LENGTH_SHORT).show());

        return view;
    }
}