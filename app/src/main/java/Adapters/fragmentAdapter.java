package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.EventsFragment;
import Fragments.MostConnectedFragment;
import Fragments.MyApplicationFragment;
import Fragments.OngoingContractMusician;
import Fragments.PhotosFragment;
import Fragments.PostsFragment;
import Fragments.PostsFragmentInMyProfile;
import Fragments.VideosFragment;

public class fragmentAdapter extends FragmentStateAdapter {
    public fragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
            case 3:
                return new MyApplicationFragment();
            case 4:
                return new OngoingContractMusician();
        }

        return new PostsFragmentInMyProfile();
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
