package com.example.survirun.Fragmnet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.survirun.R;
import com.example.survirun.Fragmnet.inFragment.ViewPagerAdapter;
import com.example.survirun.databinding.FragmentStatisticsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class StatisticsFragment extends Fragment {
    FragmentStatisticsBinding binding;
    private final List<String> titles = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentStatisticsBinding.inflate(inflater,container,false);
        View view=binding.getRoot();
        titles.add(getString(R.string.total_exercise));
        titles.add(getString(R.string.goal));
        titles.add(getString(R.string.route));
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);

        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();

        return view;
    }
}