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

import Models.Users;
import Objects.TextModifier;

public class AdapterUsersItem extends RecyclerView.Adapter<AdapterUsersItem.ItemViewHolder> {

    private List<Users> arrUsers;
    private OnItemClickListener onItemClickListener;

    public AdapterUsersItem() {
    }

    public AdapterUsersItem(List<Users> arrUsers) {
        this.arrUsers = arrUsers;
    }


    @NonNull
    @Override
    public AdapterUsersItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_users, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterUsersItem.ItemViewHolder holder, int position) {

        TextModifier textModifier = new TextModifier();

        Users users = arrUsers.get(position);

        String imageUrl = users.getImageUrl();

        textModifier.setSentenceCase(users.getFname());
        String fName = textModifier.getSentenceCase();

        textModifier.setSentenceCase(users.getLname());
        String lName = textModifier.getSentenceCase();

        String category = users.getCategory();

        holder.tv_userFullName.setText(fName + " " + lName);
        holder.tv_userCategory.setText(category );

        if(!imageUrl.isEmpty())
        {
            Picasso.get()
                    .load(imageUrl)
                    .into(holder.iv_userProfile);
        }

    }

    @Override
    public int getItemCount() {
        return arrUsers.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView iv_userProfile;
        private TextView tv_userFullName, tv_userCategory;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userProfile = itemView.findViewById(R.id.iv_userProfile);
            tv_userFullName = itemView.findViewById(R.id.tv_userFullName);
            tv_userCategory = itemView.findViewById(R.id.tv_userCategory);

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
