package Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.revic_capstone.R;

import java.util.ArrayList;
import java.util.List;

import Models.Chat;

public class MessagesFragment extends Fragment {

    private List<Chat> arrChat = new ArrayList<>();;

    private ProgressBar progressBar;
    private TextView tv_noMessagesText;
    private RecyclerView recyclerView_chatProfiles;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        setRef(view);

        if (arrChat.isEmpty()) {
            recyclerView_chatProfiles.setVisibility(View.GONE);
            tv_noMessagesText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
        else {
            recyclerView_chatProfiles.setVisibility(View.VISIBLE);
            tv_noMessagesText.setVisibility(View.GONE);

        }
        return view;
    }

    private void setRef(View view) {

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView_chatProfiles = view.findViewById(R.id.recyclerView_chatProfiles);

        tv_noMessagesText = view.findViewById(R.id.tv_noMessagesText);
    }
}