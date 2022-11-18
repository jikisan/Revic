package Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.revic_capstone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Models.Photos;
import Models.Posts;

public class AdapterPhotoView extends PagerAdapter {

    private Context context;
    private List<Posts> arrUrl = new ArrayList<Posts>();

    private LayoutInflater mLayoutInflater;

    public AdapterPhotoView() {
    }

    public AdapterPhotoView(Context context, List<Posts> arrUrl) {
        this.context = context;
        this.arrUrl = arrUrl;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.item_photo_fullscreen, container, false);

        // referencing the image view from the item.xml file
        ImageView iv_fullScreenPhoto = (ImageView) itemView.findViewById(R.id.iv_fullScreenPhoto);

        // setting the image in the imageView

        Posts posts = arrUrl.get(position);

        String imageUrl = posts.getFileUrl();

        Picasso.get()
                .load(imageUrl)
                .into(iv_fullScreenPhoto);


        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
