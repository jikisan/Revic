package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.view_event_page;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import Models.Applications;
import Models.Events;
import Models.MostRated;

public class AdapterMyApplicationsItem extends RecyclerView.Adapter<AdapterMyApplicationsItem.ItemViewHolder> {

    private List<Events> arrEvents;
    private List<String> arrEventId;
    private List<String> arrStatus;

    private OnItemClickListener onItemClickListener;

    public AdapterMyApplicationsItem() {
    }

    public AdapterMyApplicationsItem(List<Events> arrEvents, List<String> arrEventId, List<String> arrStatus) {
        this.arrEvents = arrEvents;
        this.arrEventId = arrEventId;
        this.arrStatus = arrStatus;
    }

    @NonNull
    @Override
    public AdapterMyApplicationsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_applications,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMyApplicationsItem.ItemViewHolder holder, int position) {

        Events events = arrEvents.get(position);
        String eventsId = arrEventId.get(position);
        String status = arrStatus.get(position);

        String eventName = events.getEventName();
        String eventsUrl = events.getImageUrl();

        holder.tv_eventName.setText(eventName);

        if(status.toLowerCase(Locale.ROOT).equals("pending"))
        {
            holder.tv_pending.setVisibility(View.VISIBLE);

        } else if(status.toLowerCase(Locale.ROOT).equals("accepted"))
        {
            holder.tv_accepted.setVisibility(View.VISIBLE);
        }
        else if(status.toLowerCase(Locale.ROOT).equals("declined"))
        {
            holder.tv_declined.setVisibility(View.VISIBLE);
        }
        else if(status.toLowerCase(Locale.ROOT).equals("ongoing"))
        {
            holder.tv_ongoing.setVisibility(View.VISIBLE);
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
        return arrEvents.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_eventPhoto;
        TextView tv_eventName, tv_pending, tv_accepted, tv_declined, tv_ongoing;
        Button btn_viewEvent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_eventPhoto = itemView.findViewById(R.id.iv_eventPhoto);

            tv_eventName = itemView.findViewById(R.id.tv_eventName);
            tv_pending = itemView.findViewById(R.id.tv_pending);
            tv_accepted = itemView.findViewById(R.id.tv_accepted);
            tv_declined = itemView.findViewById(R.id.tv_declined);
            tv_ongoing = itemView.findViewById(R.id.tv_ongoing);

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
