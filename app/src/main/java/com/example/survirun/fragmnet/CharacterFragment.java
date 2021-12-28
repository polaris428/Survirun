package com.example.survirun.fragmnet;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.survirun.CharacterChangeActivity;
import com.example.survirun.R;
import com.example.survirun.activity.MainActivity;

public class CharacterFragment extends Fragment {
    Dialog dialog;
    SharedPreferences.Editor editor;

    public static CharacterFragment newInstance(int page) {
        CharacterFragment characterFragment = new CharacterFragment();
        Bundle args = new Bundle();
        args.putInt("character_page", page);
        characterFragment.setArguments(args);
        return characterFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int page = this.getArguments().getInt("character_page");
        View view = inflater.inflate(page, container, false);
        SlidingDrawer slidingDrawer = view.findViewById(R.id.sliding_drawer);
        Button button = view.findViewById(R.id.handle);
        ImageButton leftImageButton = getActivity().findViewById(R.id.left_button);
        ImageButton rightImageButton = getActivity().findViewById(R.id.right_button);
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog);

        slidingDrawer.setOnDrawerOpenListener(() -> {
            button.setText(R.string.closing_status);
        });
        slidingDrawer.setOnDrawerCloseListener(() -> {
            button.setText(R.string.check_status);
        });
        if (page == R.layout.fragment_character1) {
            view.findViewById(R.id.character1).setOnClickListener(v -> {
                showDialog(getString(R.string.ahn), 1);
            });
        }
        if (page == R.layout.fragment_character2) {
            view.findViewById(R.id.character2).setOnClickListener(v -> {
                showDialog(getString(R.string.gang), 2);
            });
        }
        if (page == R.layout.fragment_character3) {
            view.findViewById(R.id.character3).setOnClickListener(v -> {
                showDialog(getString(R.string.kim), 3);
            });
        }
        if (page == R.layout.fragment_character4) {
            view.findViewById(R.id.character4).setOnClickListener(v -> {
                showDialog(getString(R.string.rex), 4);
            });
        }
        return view;
    }

    public void showDialog(String name, int num) {
        SharedPreferences sf = getActivity().getSharedPreferences("character", 0);
        editor = sf.edit();
        editor.putInt("num", num);
        editor.commit();
        Button yesButton = dialog.findViewById(R.id.yes_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);
        TextView textView = dialog.findViewById(R.id.explain_textView);
        textView.setText(String.format(getString(R.string.choose_ch), name));
        dialog.show();
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        yesButton.setOnClickListener(v -> {
            ///여기 캐릭터 변경 서버로 보내는 코드
            getActivity().finish();
        });
    }

}