package Adapters;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.revic_capstone.R;
import com.example.revic_capstone.homepage;
import com.example.revic_capstone.profile_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Models.Applications;
import Models.Contracts;
import Models.Events;

public class AdapterApplicationsItem extends RecyclerView.Adapter<AdapterApplicationsItem.ItemViewHolder> {

    private List<Applications> arrApplications = new ArrayList<>();
    private List<String> arrApplicationId = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private String eventDate;
    private long eventDateInMillis;

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

        String eventId = applications.getEventId();
        String employeeId = applications.getApplicantUsersId();
        String creatorUserId = applications.getCreatorUserId();
        String applicationId = arrApplicationId.get(position);

        generateEventData(eventId);

        String userPhotoUrl = applications.getApplicantImageUrl();
        String applicantsName = applications.getApplicantName();
        String eventName = applications.getEventName();
        String applicantUserId = applications.getApplicantUsersId();

        Picasso.get().load(userPhotoUrl).into(holder.iv_userPhoto);

        String middleMessage = " is applying for the event ";

        String finalMessage = "<b>" + applicantsName + "</b>" + middleMessage + "<b>" + eventName + "</b>";

        holder.tv_message.setText(Html.fromHtml(finalMessage));

        holder.tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), profile_page.class);
                intent.putExtra("myPosts", "2");
                intent.putExtra("userID", applicantUserId);
                view.getContext().startActivity(intent);
            }
        });

        holder.iv_userPhoto.setOnClickListener(new View.OnClickListener() {
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
                DatabaseReference contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("status", "accepted");
                applicationDatabase.child(applicationId).updateChildren(hashMap);

                String category = "Musician";
                Boolean isRatedByMusician = false;
                Boolean isRatedByCreator = false;

                Contracts contracts = new Contracts(userPhotoUrl, applicantsName, category, eventName,
                        eventDate, eventDateInMillis, eventId, employeeId, creatorUserId, "ongoing",
                        isRatedByMusician, isRatedByCreator);

                contractDatabase.push().setValue(contracts).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        Toast.makeText(view.getContext(), applicantsName + "is accepted!", Toast.LENGTH_SHORT).show();
                        backToProfilePage(view);

                    }
                });
            }
        });

        holder.tv_declined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference applicationDatabase = FirebaseDatabase.getInstance().getReference("Applications");

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("status", "declined");
                applicationDatabase.child(applicationId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        backToProfilePage(view);
                    }
                });


            }
        });
    }

    private void backToProfilePage(View view) {

        Intent intent = new Intent(view.getContext(), homepage.class);
        intent.putExtra("pageNumber", "5");
        intent.putExtra("myCategory", "Organizer");
        view.getContext().startActivity(intent);
    }

    private void generateEventData(String eventId) {

        DatabaseReference applicationDatabase = FirebaseDatabase.getInstance().getReference("Events");

        applicationDatabase.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    Events events = snapshot.getValue(Events.class);
                    eventDate = events.getEventDateSched();
                    eventDateInMillis = events.getDateTimeInMillis();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        TextView tv_message, tv_acceptApplicant, tv_declined;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_userPhoto = itemView.findViewById(R.id.iv_userPhoto);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_declined = itemView.findViewById(R.id.tv_declined);
            tv_acceptApplicant = itemView.findViewById(R.id.tv_acceptApplicant);

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
