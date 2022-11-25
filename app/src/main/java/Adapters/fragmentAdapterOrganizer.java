package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ApplicantsFragment;
import Fragments.HiredFragment;
import Fragments.ProfileEventsFragment;

public class fragmentAdapterOrganizer extends FragmentStateAdapter {
    public fragmentAdapterOrganizer(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return new ProfileEventsFragment();
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
