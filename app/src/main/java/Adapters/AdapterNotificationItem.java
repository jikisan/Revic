package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;

import java.util.List;
import java.util.Locale;

import Models.Chat;
import Models.Notifications;

public class AdapterNotificationItem extends RecyclerView.Adapter<AdapterNotificationItem.ItemViewHolder> {

    private List<Notifications> arr;
    private OnItemClickListener onItemClickListener;

    public AdapterNotificationItem() {
    }

    public AdapterNotificationItem(List<Notifications> arr) {
        this.arr = arr;
    }

    @NonNull
    @Override
    public AdapterNotificationItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotificationItem.ItemViewHolder holder, int position) {

        Notifications notifications = arr.get(position);

        String notifType = notifications.getNotificationType().toLowerCase(Locale.ROOT);
        String date = notifications.getTransactionDate();
        String time = notifications.getTransactionTime();
        String notifMessage = notifications.getNotificationMessage();


        String dateTime = date + " " + time;

        holder.tv_notifDateTime.setText(dateTime);
        holder.tv_notifMessage.setText(notifMessage);

        if(notifType.equals("message"))
        {
            holder.tv_notifType.setText(notifType);
        }
        else if(notifType.equals("hired"))
        {
            holder.tv_notifType.setText("Application accepted");
        }
        else if(notifType.equals("payment"))
        {
            holder.tv_notifType.setText("Fund Transfer");
        }





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

        TextView tv_notifType, tv_notifDateTime, tv_notifMessage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_notifType = itemView.findViewById(R.id.tv_notifType);
            tv_notifDateTime = itemView.findViewById(R.id.tv_notifDateTime);
            tv_notifMessage = itemView.findViewById(R.id.tv_notifMessage);

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
