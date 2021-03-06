package com.example.survirun.activity.exercise;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.example.survirun.R;
import com.example.survirun.databinding.ActivityExerciseStoryBinding;
import com.example.survirun.databinding.ActivityMapBinding;

public class ExerciseStoryActivity extends AppCompatActivity {
    private ActivityExerciseStoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = ActivityExerciseStoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();
        String data = intent.getStringExtra("storyBody");
        String negativeBtnText = intent.getStringExtra("negative");
        String positiveBtnText = intent.getStringExtra("positive");

        if(data.equals("모자") || data.equals("너클") || data.equals("야구빠따") || data.equals("화염병") || data.equals("칼")) {
            binding.imgItem.setVisibility(View.VISIBLE);
            switch(data) {
                case "모자":
                    binding.imgItem.setImageResource(R.drawable.cap);
                    //binding.imgItem.setImageDrawable(getDrawable(R.drawable.knuckle));
                    break;
                case "너클":
                    binding.imgItem.setImageResource(R.drawable.knuckle);
                    break;
                case "야구빠따":
                    binding.imgItem.setImageResource(R.drawable.bat);
                    break;
                case "화염병":
                    binding.imgItem.setImageResource(R.drawable.alcohol);
                    break;
                case "칼":
                    binding.imgItem.setImageResource(R.drawable.knife);
                    break;
                default:
                    break;
            }
            data = data + " 을(를) 획득하였습니다.";
            binding.storyTitle.setText("아이템 획득");
        }



        binding.storyBody.setText(data);
        binding.negativeButton.setText(negativeBtnText);
        binding.positiveButton.setText(positiveBtnText);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

    public void negativeBtnPressed(View v) {
        Intent i = new Intent();
        i.putExtra("result", 0);
        setResult(RESULT_OK, i);
        finish();
    }

    public void positiveBtnPressed(View v) {
        Intent i = new Intent();
        i.putExtra("result", 1);
        setResult(RESULT_OK, i);
        finish();
    }

}