package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.profile_page;
import com.example.revic_capstone.view_event_page;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import Models.Chat;
import Models.Events;

public class AdapterEventsItem extends RecyclerView.Adapter<AdapterEventsItem.ItemViewHolder> {

    private List<Events> arr;
    private List<String> arrEventsId;
    private OnItemClickListener onItemClickListener;
    private String eventId;

    public AdapterEventsItem() {
    }

    public AdapterEventsItem(List<Events> arr, List<String> arrEventsId) {
        this.arr = arr;
        this.arrEventsId = arrEventsId;
    }

    @NonNull
    @Override
    public AdapterEventsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_events,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterEventsItem.ItemViewHolder holder, int position) {

        Events events = arr.get(position);
        String eventsId = arrEventsId.get(position);

        String dateCreated = events.getEventDateSched();
        String splitDate[] = dateCreated.split("-");

        String Month = splitDate[0];
        String Day = splitDate[1];

        String eventName = events.getEventName();
        String eventPriceInString = NumberFormat.getNumberInstance(Locale.US).format(events.getEventPrice());

        String eventsUrl = events.getImageUrl();

        holder.tv_month.setText(Month);
        holder.tv_date.setText(Day);
        holder.tv_eventName.setText(eventName);
        holder.tv_eventAddress.setText("₱ " + eventPriceInString + " / event");

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
        return arr.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_eventPhoto;
        TextView tv_date, tv_month, tv_eventName, tv_eventAddress;
        Button btn_viewEvent;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_eventPhoto = itemView.findViewById(R.id.iv_eventPhoto);

            tv_date = itemView.findViewById(R.id.tv_date);
            tv_month = itemView.findViewById(R.id.tv_month);
            tv_eventName = itemView.findViewById(R.id.tv_eventName);
            tv_eventAddress = itemView.findViewById(R.id.tv_eventAddress);

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
