package com.example.survirun.fragmnet.inFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.survirun.fragmnet.StatisticsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> list = new ArrayList<>();

    public ViewPagerAdapter(@NonNull @NotNull StatisticsFragment fragmentActivity) {
        super(fragmentActivity);
        list.add(new Fragment1());
        list.add(new Fragment2());
        list.add(new Fragment3());
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return list.get(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
