package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Events;
import Models.Users;

public class AdapterMostConnectedItem extends RecyclerView.Adapter<AdapterMostConnectedItem.ItemViewHolder> {

    private List<Users> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterMostConnectedItem() {
    }

    public AdapterMostConnectedItem(List<Users> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public AdapterMostConnectedItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_most_connected,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMostConnectedItem.ItemViewHolder holder, int position) {

        Users users = arr.get(position);

        String fname = users.getFname();
        String lname = users.getLname();

        String fullName = fname + " " + lname;
        String category = users.getCategory();
        long ratingsCount = users.getRating();
        int connectionCount = users.getConnections();
        String imageUrl = users.getImageUrl();

        holder.tv_userName.setText(fullName);
        holder.tv_category.setText(category);
        holder.tv_userRatingCount.setText("("+ratingsCount+")");
        holder.rb_userRating.setRating(ratingsCount);
        holder.tv_connectionsCount.setText(connectionCount + "");

        Picasso.get().load(imageUrl).into(holder.iv_userPhoto);

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

        ImageView iv_userPhoto;
        TextView tv_userName, tv_category, tv_connectionsCount, tv_userRatingCount;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);

            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_category = itemView.findViewById(R.id.tv_category);
            tv_connectionsCount = itemView.findViewById(R.id.tv_connectionsCount);
            tv_userRatingCount = itemView.findViewById(R.id.tv_userRatingCount);

            rb_userRating = itemView.findViewById(R.id.rb_userRating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(onItemClickListener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            onItemClickListener.onItemClick(position);
                        }
                    }

                }
            });
        }
    }
}
