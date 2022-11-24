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

import java.util.List;

import Models.Ratings;
import Objects.TextModifier;

public class AdapterReviewsItem extends RecyclerView.Adapter<AdapterReviewsItem.ItemViewHolder> {

    private List<Ratings> arrRatings;
    private OnItemClickListener onItemClickListener;

    public AdapterReviewsItem() {
    }

    public AdapterReviewsItem(List<Ratings> arrRatings) {
        this.arrRatings = arrRatings;
    }

    @NonNull
    @Override
    public AdapterReviewsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratings,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterReviewsItem.ItemViewHolder holder, int position) {

        Ratings ratings = arrRatings.get(position);
        TextModifier textModifier = new TextModifier();

        double ratingsValue = ratings.getRatingValue();
        String userName = ratings.getRatedByName();
        String message = ratings.getRatingMessage();

        holder.rb_userRating.setRating((float)ratingsValue);
        holder.tv_userName.setText("by: " + userName);
        holder.tv_message.setText(message);

    }

    @Override
    public int getItemCount() {
        return arrRatings.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_userName, tv_message;
        RatingBar rb_userRating;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_message = itemView.findViewById(R.id.tv_message);

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
