package com.example.survirun;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.survirun.activity.MainActivity;

public class WelcomeFragment extends Fragment {

    public static WelcomeFragment newInstance(int page) {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        Bundle args = new Bundle();
        args.putInt("welcome_page", page);
        welcomeFragment.setArguments(args);
        return welcomeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int page = this.getArguments().getInt("welcome_page");
        View view = inflater.inflate(page, container, false);

        if (page == R.layout.welcome_3) {
            view.findViewById(R.id.start_button).setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            });
        }
        return view;
    }
}