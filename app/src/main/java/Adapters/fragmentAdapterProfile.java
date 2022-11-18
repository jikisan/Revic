package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ApplicantsFragment;
import Fragments.HiredFragment;
import Fragments.PhotosFragment;
import Fragments.PostsFragment;
import Fragments.ProfileEventsFragment;
import Fragments.VideosFragment;

public class fragmentAdapterProfile extends FragmentStateAdapter {
    public fragmentAdapterProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new ApplicantsFragment();
            case 2:
                return new HiredFragment();
        }

        return new ProfileEventsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
