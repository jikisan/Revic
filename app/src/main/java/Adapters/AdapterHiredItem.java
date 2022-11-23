package Adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.homepage;
import com.example.revic_capstone.view_contract_page;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import Models.Contracts;
import Models.Events;

public class AdapterHiredItem extends RecyclerView.Adapter<AdapterHiredItem.ItemViewHolder> {

    private List<Contracts> arrContracts;
    private List<String> arrContractId;
    private OnItemClickListener onItemClickListener;

    public AdapterHiredItem() {
    }

    public AdapterHiredItem(List<Contracts> arrContracts, List<String> arrContractId) {
        this.arrContracts = arrContracts;
        this.arrContractId = arrContractId;
    }

    @NonNull
    @Override
    public AdapterHiredItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hired,parent, false));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterHiredItem.ItemViewHolder holder, int position) {

        Contracts contract = arrContracts.get(position);
        String contractId = arrContractId.get(position);
        String eventId = contract.getEventId();
        String employeeId = contract.getEmployeeId();

        String imageUrl = contract.getImageUrl();
        String userName = contract.getUserName();
        String contractStatus = contract.getContractStatus();
        String eventName = contract.getEventName();
        String eventDate = contract.getEventDate();

        if(contractStatus.toLowerCase(Locale.ROOT).equals("ongoing"))
        {
            holder.tv_ongoing.setVisibility(View.VISIBLE);
        } else if(contractStatus.toLowerCase(Locale.ROOT).equals("terminated"))
        {
            holder.tv_terminated.setVisibility(View.VISIBLE);

        }else if(contractStatus.toLowerCase(Locale.ROOT).equals("done"))
        {
            holder.tv_done.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(imageUrl).into(holder.iv_userPhoto);
        holder.tv_userName.setText(userName);
        holder.tv_eventName.setText(eventName);
        holder.tv_eventDate.setText(eventDate);

        holder.tv_viewContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), view_contract_page.class);
                intent.putExtra("contractId", contractId);
                intent.putExtra("eventId", eventId);
                intent.putExtra("employeeId", employeeId);
                view.getContext().startActivity(intent);

            }
        });


    }

    @Override
    public int getItemCount() {
        return arrContracts.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userPhoto;
        TextView tv_userName, tv_contractStatus, tv_eventName, tv_eventDate, tv_viewContract
                , tv_ongoing, tv_terminated, tv_done;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);

            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_ongoing = itemView.findViewById(R.id.tv_ongoing);
            tv_terminated = itemView.findViewById(R.id.tv_terminated);
            tv_done = itemView.findViewById(R.id.tv_done);
            tv_eventName = itemView.findViewById(R.id.tv_eventName);
            tv_eventDate = itemView.findViewById(R.id.tv_eventDate);
            tv_viewContract = itemView.findViewById(R.id.tv_viewContract);

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
