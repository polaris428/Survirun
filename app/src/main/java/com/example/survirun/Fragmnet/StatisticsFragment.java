package com.example.survirun.Fragmnet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.R;
import com.example.survirun.ViewPagerAdapter;
import com.example.survirun.databinding.FragmentStatisticsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class StatisticsFragment extends Fragment {
    FragmentStatisticsBinding binding;
    private String[] titles = new String[]{"운동량", "목표량", "루트"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentStatisticsBinding.inflate(inflater,container,false);
        View view=binding.getRoot();

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles[position])).attach();

        return view;
    }
}