package Adapters;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.profile_page;
import com.example.revic_capstone.view_event_page;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.Applications;
import Models.Events;

public class AdapterApplicationsItem extends RecyclerView.Adapter<AdapterApplicationsItem.ItemViewHolder> {

    private List<Applications> arrApplications = new ArrayList<>();
    private List<String> arrApplicationId = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public AdapterApplicationsItem() {
    }

    public AdapterApplicationsItem(List<Applications> arrApplications, List<String> arrApplicationId) {
        this.arrApplications = arrApplications;
        this.arrApplicationId = arrApplicationId;
    }

    @NonNull
    @Override
    public AdapterApplicationsItem.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder
                (LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applicants,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApplicationsItem.ItemViewHolder holder, int position) {

        Applications applications = arrApplications.get(position);

        String userPhotoUrl = applications.getEventImageUrl();
        String applicantsName = applications.getApplicantName();
        String eventName = applications.getEventName();
        String applicantUserId = applications.getApplicantUsersId();

        Picasso.get().load(userPhotoUrl).into(holder.iv_userPhoto);

        String middleMessage = " is applying for the event ";

        String finalMessage = "<b>" + applicantsName + "</b>" + middleMessage + "<b>" + eventName + "</b>";
        holder.tv_message.setText(Html.fromHtml(finalMessage));

        holder.tv_viewProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), profile_page.class);
                intent.putExtra("myPosts", "2");
                intent.putExtra("userID", applicantUserId);
                view.getContext().startActivity(intent);
            }
        });

        holder.tv_acceptApplicant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference applicationDatabase = FirebaseDatabase.getInstance().getReference("Applications");

                String newValue = "ongoing";
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("status", newValue);
                applicationDatabase.child(arrApplicationId.get(position)).updateChildren(hashMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrApplications.size();
    }

    public interface  OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_userPhoto;
        TextView tv_message, tv_viewProfileBtn, tv_acceptApplicant;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_acceptApplicant = itemView.findViewById(R.id.tv_acceptApplicant);
            tv_viewProfileBtn = itemView.findViewById(R.id.tv_viewProfileBtn);

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
