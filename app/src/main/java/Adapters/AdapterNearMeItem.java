package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.view_event_page;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import Models.Events;
import Models.MostRated;

public class AdapterNearMeItem extends RecyclerView.Adapter<AdapterNearMeItem.ItemViewHolder> {

    private List<MostRated> arrMostRated;
    private List<Events> arrEvents;
    private OnItemClickListener onItemClickListener;

    public AdapterNearMeItem() {
    }

    public AdapterNearMeItem(List<MostRated> arrMostRated, List<Events> arrEvents) {
        this.arrMostRated = arrMostRated;
        this.arrEvents = arrEvents;
    }

    @NonNull
    @Override
    public AdapterNearMeItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_near_me,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNearMeItem.ItemViewHolder holder, int position) {

        MostRated mostRated = arrMostRated.get(position);

        String eventName = mostRated.getEventsName();
        String eventAddress = mostRated.getEventAddress();
        double distance = mostRated.getDistance();
        String eventsUrl = mostRated.getEventsUrl();
        String eventsId = mostRated.getEventsId();

        holder.tv_eventName.setText(eventName);
        holder.tv_eventAddress.setText(eventAddress);

        DecimalFormat df = new DecimalFormat("#.00");
        df.format(distance);

        if (distance > 1000) {
            double kilometers = distance / 1000;
            holder.tv_distance.setText(df.format(kilometers) + " Km Away");
        } else {
            holder.tv_distance.setText(df.format(distance) + " m Away");

        }

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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_eventPhoto;
        TextView tv_eventName, tv_eventAddress, tv_distance;
        Button btn_viewEvent;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_eventPhoto = itemView.findViewById(R.id.iv_eventPhoto);

            tv_eventName = itemView.findViewById(R.id.tv_eventName);
            tv_eventAddress = itemView.findViewById(R.id.tv_eventAddress);
            tv_distance = itemView.findViewById(R.id.tv_distance);

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
