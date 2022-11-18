package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.photo_view_page;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Photos;
import Models.Posts;

public class AdapterPhotoItem extends RecyclerView.Adapter<AdapterPhotoItem.ItemViewHolder> {

    private List<Posts> arr;
    private OnItemClickListener onItemClickListener;
    private FirebaseUser user;
    private String userIdFromSearch, userID;


    public AdapterPhotoItem() {
    }

    public AdapterPhotoItem(List<Posts> arr, String userIdFromSearch) {
        this.arr = arr;
        this.userIdFromSearch = userIdFromSearch;
    }

    @NonNull
    @Override
    public AdapterPhotoItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AdapterPhotoItem.ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photos,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPhotoItem.ItemViewHolder holder, int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (userIdFromSearch != null) {

            userID = userIdFromSearch;
        }
        else
        {
            userID = user.getUid();
        }

        Posts posts = arr.get(position);

        String imageUrl = posts.getFileUrl();
        String postUserId = posts.getUserId();
        String fileType = posts.getFileType();

        Picasso.get()
                .load(imageUrl)
                .into(holder.iv_photoItem);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), photo_view_page.class);
                intent.putExtra("postUserId", postUserId);
                intent.putExtra("current position", position);
                intent.putExtra("fileType", fileType);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_photoItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_photoItem = itemView.findViewById(R.id.iv_photoItem);

            if(onItemClickListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    onItemClickListener.onItemClick(position);
                }
            }

        }
    }
}
