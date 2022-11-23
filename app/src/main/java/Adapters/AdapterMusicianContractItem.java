package Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.view_contract_musician;
import com.example.revic_capstone.view_contract_page;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import Models.Contracts;
import Models.Events;

public class AdapterMusicianContractItem extends RecyclerView.Adapter<AdapterMusicianContractItem.ItemViewHolder> {

    private List<Contracts> arrContracts;
    private List<String> arrContractId;
    private OnItemClickListener onItemClickListener;

    public AdapterMusicianContractItem() {
    }

    public AdapterMusicianContractItem(List<Contracts> arrContracts, List<String> arrContractId) {
        this.arrContracts = arrContracts;
        this.arrContractId = arrContractId;
    }

    @NonNull
    @Override
    public AdapterMusicianContractItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_musician_contract,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMusicianContractItem.ItemViewHolder holder, int position) {

        Contracts contract = arrContracts.get(position);
        String contractId = arrContractId.get(position);
        String eventId = contract.getEventId();
        String creatorId = contract.getCreatorUserId();

        String contractStatus = contract.getContractStatus();
        String eventName = contract.getEventName();
        String eventDate = contract.getEventDate();

        generateEventData(eventId, holder);

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

        holder.tv_eventName.setText(eventName);
        holder.tv_eventDate.setText(eventDate);

        holder.tv_viewContract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), view_contract_musician.class);
                intent.putExtra("contractId", contractId);
                intent.putExtra("eventId", eventId);
                intent.putExtra("creatorId", creatorId);
                view.getContext().startActivity(intent);

            }
        });

    }

    private void generateEventData(String eventId, ItemViewHolder holder) {

        DatabaseReference eventDatabase = FirebaseDatabase.getInstance().getReference("Events");

        eventDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Events events = snapshot.getValue(Events.class);

                    String eventsPhotoUrl = events.getImageUrl();

                    Picasso.get().load(eventsPhotoUrl).into(holder.iv_eventPhoto);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        ImageView iv_eventPhoto;
        TextView tv_eventName, tv_eventDate, tv_viewContract
                , tv_ongoing, tv_terminated, tv_done;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_eventPhoto = itemView.findViewById(R.id.iv_eventPhoto);

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
