package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.MyApplicationFragment;
import Fragments.OngoingContractMusician;
import Fragments.PhotosFragment;
import Fragments.PostsFragmentInMyProfile;
import Fragments.VideosFragment;

public class fragmentAdapterOtherUsers extends FragmentStateAdapter {
    public fragmentAdapterOtherUsers(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new PhotosFragment();
            case 2:
                return new VideosFragment();
        }

        return new PostsFragmentInMyProfile();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
