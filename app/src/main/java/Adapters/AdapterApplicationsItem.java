package Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Models.Applications;
import Models.Contracts;
import Models.Events;
import Models.Notifications;
import Models.Transactions;
import Models.Wallets;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdapterApplicationsItem extends RecyclerView.Adapter<AdapterApplicationsItem.ItemViewHolder> {

    private List<Applications> arrApplications = new ArrayList<>();
    private List<String> arrApplicationId = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private String eventDate, walletId, timeCreated, dateCreated;
    private long eventDateInMillis;
    private double myFundAmount;
    private double eventPrice;
    private long dateTimeInMillis;
    private int currentPosition;
    private DatabaseReference walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
    private String eventId;
    private String employeeId;
    private String creatorUserId;
    private String applicationId;
    private String contractId2;
    private String eventName;
    private String userPhotoUrl;
    private String applicantsName;
    private ProgressDialog progressDialog;


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
    public void onBindViewHolder(@NonNull AdapterApplicationsItem.ItemViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Applications applications = arrApplications.get(position);
        currentPosition = position;

        eventId = applications.getEventId();
        employeeId = applications.getApplicantUsersId();
        creatorUserId = applications.getCreatorUserId();
        applicationId = arrApplicationId.get(position);

        setUpDate();
        generateEventData(eventId);

        userPhotoUrl = applications.getApplicantImageUrl();
        applicantsName = applications.getApplicantName();
        eventName = applications.getEventName();
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

                SweetAlertDialog pdialog;
                pdialog = new SweetAlertDialog(view.getContext(), SweetAlertDialog.WARNING_TYPE);
                pdialog.setTitleText("Application");
                pdialog.setContentText("Accept " + applicantsName + "'s \napplication?" +
                        "\n\n Php " + (int)eventPrice + " will be \ndeducted to your fund.");
                pdialog.setConfirmButton("Accept", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        progressDialog = new ProgressDialog(view.getContext());
                        progressDialog.setTitle("Processing...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        pdialog.dismiss();
                        arrApplications.remove(position);
                        arrApplicationId.remove(position);

                        deductFunds(view);

                    }
                });
                pdialog.setCancelButton("Back", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pdialog.dismiss();
                    }
                });
                pdialog.show();



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


    private void deductFunds(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUserId = user.getUid();

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        walletDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {

                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        String walletUserId = wallets.getUserID();

                        if(walletUserId.equals(myUserId))
                        {
                            walletId = dataSnapshot.getKey().toString();
                            myFundAmount = wallets.getFundAmount();
                        }

                    }

                    if(myFundAmount < eventPrice)
                    {
                        Toast.makeText(view.getContext(), "Insufficient fund to hire.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                    else
                    {
                        updateWalletData(view);

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateWalletData(View view) {

        double newFundValue = myFundAmount - eventPrice;

        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userID", creatorUserId);
        hashMap.put("fundAmount", newFundValue);

        walletDatabase.child(walletId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateTransactionData(view);
            }
        });


    }

    private void updateTransactionData(View view) {

        DatabaseReference transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        setUpDate();

        String transactionType = "deduct";
        String transactionNote = "Acceptance payment";

        Transactions transactions = new Transactions(dateTimeInMillis, dateCreated, timeCreated,
                transactionType, transactionNote, eventPrice, creatorUserId);

        transactionDatabase.push().setValue(transactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                updateApplicationbAndCreateContract(view);

            }
        });


    }

    private void updateApplicationbAndCreateContract(View view) {


        DatabaseReference contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");

        String category = "Musician";
        Boolean isRatedByMusician = false;
        Boolean isRatedByCreator = false;

        Contracts contracts = new Contracts(userPhotoUrl, applicantsName, category, eventName,
                eventDate, eventDateInMillis, eventId, employeeId, creatorUserId, "ongoing",
                isRatedByMusician, isRatedByCreator);

        contractDatabase.push().setValue(contracts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                generateContractId(view);


            }
        });
    }

    private void generateContractId(View view) {

        DatabaseReference contractDatabase = FirebaseDatabase.getInstance().getReference("Contracts");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String myUserId = user.getUid();

        Query query = contractDatabase.orderByChild("creatorUserId").equalTo(myUserId);

        contractDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for( DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Contracts contracts = dataSnapshot.getValue(Contracts.class);

                        String creatorUserId = contracts.getCreatorUserId();
                        String tempEventId = contracts.getEventId();
                        String tempEmployeeId = contracts.getEmployeeId();

                        if(creatorUserId.equals(myUserId))
                        {
                            if(tempEventId.equals(eventId))
                            {
                                if(tempEmployeeId.equals(employeeId))
                                {
                                    contractId2 = dataSnapshot.getKey();

                                }
                            }
                        }
                    }

                    generateNotification(view);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void generateNotification(View view) {

        DatabaseReference notificationDatabase = FirebaseDatabase.getInstance().getReference("Notifications");

        String notificationType = "hired";
        String notificationMessage = "Your application for event " + eventName + " has been accepted";
        String contractId = contractId2;
        String userId = employeeId;
        String chatId = "";
        String eventId2 = eventId;

        Notifications notifications = new Notifications(dateTimeInMillis, dateCreated, timeCreated, notificationType,
                notificationMessage, contractId, userId, chatId, eventId2);

        notificationDatabase.push().setValue(notifications).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseReference applicationDatabase = FirebaseDatabase.getInstance().getReference("Applications");

                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("status", "accepted");
                applicationDatabase.child(applicationId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(view.getContext(), applicantsName + " is accepted!", Toast.LENGTH_SHORT).show();
                            notifyItemRemoved(currentPosition);


                            progressDialog.dismiss();
                        }

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
                    eventPrice = events.getEventPrice();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setUpDate() {

        Date currentTime = Calendar.getInstance().getTime();
        String dateTime = DateFormat.getDateTimeInstance().format(currentTime);

        SimpleDateFormat formatDateTimeInMillis = new SimpleDateFormat("yyyyMMddhhmma");
        SimpleDateFormat formatDate = new SimpleDateFormat("MMM-dd-yyyy");
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a");

        dateTimeInMillis = Calendar.getInstance().getTimeInMillis();
        timeCreated = formatTime.format(Date.parse(dateTime));
        dateCreated = formatDate.format(Date.parse(dateTime));

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
