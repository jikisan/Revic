package Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.revic_capstone.R;

import Fragments.EventsFragment;
import Fragments.MostAppliedFragment;
import Fragments.MostConnectedFragment;
import Fragments.MostRatedFragment;
import Fragments.MyApplicationFragment;
import Fragments.NearMeFragment;
import Fragments.PostsFragment;
import Fragments.VideosFragment;

public class fragmentAdapsterHomepage extends FragmentStateAdapter {
    public fragmentAdapsterHomepage(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position){
            case 1:
                return new EventsFragment();
            case 2:
                return new MostConnectedFragment();

            case 3:
                return new MostAppliedFragment();

            case 4:
                return new MostRatedFragment();

            case 5:
                return new NearMeFragment();


        }

        return new PostsFragment();
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
