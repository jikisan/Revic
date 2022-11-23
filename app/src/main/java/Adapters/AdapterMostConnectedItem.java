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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Ratings;
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
        int connectionCount = users.getConnections();
        String imageUrl = users.getImageUrl();
        String userId = users.getUsersId();

        generateRatingAverage(userId, holder);

        holder.tv_userName.setText(fullName);
        holder.tv_category.setText(category);

        holder.tv_connectionsCount.setText(connectionCount + "");

        Picasso.get().load(imageUrl).into(holder.iv_userPhoto);

    }

    private void generateRatingAverage(String userId, ItemViewHolder holder) {

        DatabaseReference ratingDatabase = FirebaseDatabase.getInstance().getReference("Ratings");
        Query query = ratingDatabase.orderByChild("ratingOfId").equalTo(userId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = 0;
                double totalRating = 0, tempRatingValue = 0, averageRating = 0;

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Ratings ratings = dataSnapshot.getValue(Ratings.class);
                        tempRatingValue = ratings.getRatingValue();
                        totalRating = totalRating + tempRatingValue;
                        counter++;
                    }

                    averageRating = totalRating / counter;
                    String ratingCounter = "(" + String.valueOf(averageRating) + ")";

                    holder.tv_userRatingCount.setText(ratingCounter);
                    holder.rb_userRating.setRating((float) averageRating);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
