package edu.illinois.cs465.lockedin.adapters;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.illinois.cs465.lockedin.fragments.HomeFragment;
import edu.illinois.cs465.lockedin.fragments.UpcomingFragment;
import edu.illinois.cs465.lockedin.fragments.CompletedFragment;

public class ViewPagerAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull HomeFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UpcomingFragment();
            case 1:
                return new CompletedFragment();
            default:
                return new UpcomingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
