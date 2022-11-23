package com.example.revic_capstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Adapters.AdapterEventsItem;
import Adapters.AdapterTransactionsItem;
import Models.Events;
import Models.Posts;
import Models.Transactions;
import Models.Users;
import Models.Wallets;

public class my_wallet_page extends AppCompatActivity {

    private List<Transactions> arrTransactions = new ArrayList<>();

    private TextView tv_fundBalance, tv_back, tv_withdrawBalanceBtn, tv_message;
    private ImageView iv_addFund;
    private RecyclerView recyclerView;
    private AdapterTransactionsItem adapterTransactionsItem;

    private DatabaseReference walletDatabase, transactionDatabase;
    private FirebaseUser user;

    private List<Wallets> arrWallets = new ArrayList<Wallets>();

    private String myUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wallet_page);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myUserId = user.getUid();

        walletDatabase = FirebaseDatabase.getInstance().getReference("Wallets");
        transactionDatabase = FirebaseDatabase.getInstance().getReference("Transactions");

        setRef();
        generateWalletData();
        generateRecyclerLayout();
        clickListeners();
    }

    private void generateRecyclerLayout() {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(my_wallet_page.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapterTransactionsItem = new AdapterTransactionsItem(arrTransactions);
        recyclerView.setAdapter(adapterTransactionsItem);

        getViewHolderValues();

    }

    private void getViewHolderValues() {

        Query query = transactionDatabase.orderByChild("ownerID").equalTo(myUserId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Transactions transactions = dataSnapshot.getValue(Transactions.class);

                        arrTransactions.add(transactions);
                    }
                }

                if(arrTransactions.isEmpty())
                {
                    tv_message.setVisibility(View.VISIBLE);
                }

                Collections.reverse(arrTransactions);

                adapterTransactionsItem.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void generateWalletData() {

        Query query = walletDatabase.orderByChild("userID").equalTo(myUserId);

        walletDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists())
                {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                    {
                        Wallets wallets = dataSnapshot.getValue(Wallets.class);

                        double fundAmount = wallets.getFundAmount();

                        tv_fundBalance.setText(fundAmount + "");
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clickListeners() {

        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(my_wallet_page.this, homepage.class);
                intent.putExtra("pageNumber", "5");
                intent.putExtra("myCategory", "Event Organizer");
                intent.putExtra("myPosts", "1");
                startActivity(intent);
            }
        });

        iv_addFund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(my_wallet_page.this, add_funds_page.class);
                startActivity(intent);
            }
        });

        tv_withdrawBalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(my_wallet_page.this, "Withdraw button clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRef() {

        tv_fundBalance = findViewById(R.id.tv_fundBalance);
        tv_back = findViewById(R.id.tv_back);
        tv_withdrawBalanceBtn = findViewById(R.id.tv_withdrawBalanceBtn);
        tv_message = findViewById(R.id.tv_message);

        recyclerView = findViewById(R.id.recyclerView);

        iv_addFund = findViewById(R.id.iv_addFund);
    }
}