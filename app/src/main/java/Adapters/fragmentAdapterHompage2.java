package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import Fragments.ApplicantsFragment;
import Fragments.HiredFragment;
import Fragments.MostConnectedFragment;
import Fragments.MostRatedMusicians;
import Fragments.PostsFragment;

public class fragmentAdapterHompage2 extends FragmentStateAdapter {

    public fragmentAdapterHompage2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 1:
                return new MostConnectedFragment();
            case 2:
                return new MostRatedMusicians();
        }

        return new PostsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
