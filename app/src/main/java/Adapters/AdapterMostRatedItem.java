package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.view_event_page;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Events;
import Models.MostRated;
import Models.Users;

public class AdapterMostRatedItem extends RecyclerView.Adapter<AdapterMostRatedItem.ItemViewHolder> {

    private List<MostRated> arrMostRated;
    private OnItemClickListener onItemClickListener;

    public AdapterMostRatedItem() {
    }

    public AdapterMostRatedItem(List<MostRated> arrMostRated) {
        this.arrMostRated = arrMostRated;
    }

    @NonNull
    @Override
    public AdapterMostRatedItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_most_rated,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMostRatedItem.ItemViewHolder holder, int position) {

        MostRated mostRated = arrMostRated.get(position);

        String eventName = mostRated.getEventsName();
        long ratingsCount = mostRated.getRatingsCount();
        String eventsUrl = mostRated.getEventsUrl();
        String eventsId = mostRated.getEventsId();

        holder.tv_eventName.setText(eventName);
        holder.tv_userRatingCount.setText("("+ratingsCount+")");
        holder.rb_userRating.setRating(ratingsCount);

        Picasso.get()
                .load(eventsUrl)
                .fit()
                .centerCrop()
                .into(holder.iv_eventPhoto);

        holder.btn_viewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), view_event_page.class);
                intent.putExtra("eventId", eventsId);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrMostRated.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_eventPhoto;
        TextView tv_eventName, tv_userRatingCount;
        RatingBar rb_userRating;
        Button btn_viewEvent;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_eventPhoto = itemView.findViewById(R.id.iv_eventPhoto);

            tv_eventName = itemView.findViewById(R.id.tv_eventName);
            tv_userRatingCount = itemView.findViewById(R.id.tv_userRatingCount);

            rb_userRating = itemView.findViewById(R.id.rb_userRating);

            btn_viewEvent = itemView.findViewById(R.id.btn_viewEvent);

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
